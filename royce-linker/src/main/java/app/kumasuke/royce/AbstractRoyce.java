package app.kumasuke.royce;

import app.kumasuke.royce.except.MaybeException;
import app.kumasuke.royce.except.UncheckedSQLException;
import app.kumasuke.royce.except.VoidMaybeException;
import app.kumasuke.royce.linker.*;
import app.kumasuke.royce.util.SingletonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class AbstractRoyce implements Royce {
    private static final Logger logger = LoggerFactory.getLogger(Royce.class);

    private final ConnectionProvider provider;
    private SingletonContext<Royce> transactionalSingleton;
    private SingletonContext<Royce> nonTransactionalSingleton;

    AbstractRoyce(ConnectionProvider provider) {
        this.provider = provider;
        this.transactionalSingleton = null;
        this.nonTransactionalSingleton = null;
    }

    final void setTransactionalConstructor(Supplier<Royce> constructor) {
        if (transactionalSingleton == null) {
            this.transactionalSingleton = new SingletonContext<>(constructor);
        } else {
            throw new AssertionError();
        }
    }

    final void setNonTransactionalConstructor(Supplier<Royce> constructor) {
        if (nonTransactionalSingleton == null) {
            this.nonTransactionalSingleton = new SingletonContext<>(constructor);
        } else {
            throw new AssertionError();
        }
    }

    final <R> ConnectionProcessor<R> readOnly(final ConnectionProcessor<R> connProcessor) {
        return conn -> {
            final boolean oldReadOnly = conn.isReadOnly();
            if (!oldReadOnly) {
                conn.setReadOnly(true);
                logger.debug("connection properties changed: readOnly = true");
            }

            R retVal;
            try {
                retVal = connProcessor.process(conn);
            } finally {
                if (!oldReadOnly) {
                    conn.setReadOnly(false);
                    logger.debug("connection properties reverted: readonly = false");
                }
            }
            return retVal;
        };
    }

    final <R> ConnectionProcessor<R> nonTransact(final ConnectionProcessor<R> connProcessor) {
        return conn -> {
            final boolean oldAutoCommit = conn.getAutoCommit();
            if (!oldAutoCommit) {
                conn.setAutoCommit(true);
                logger.debug("connection properties changed: autoCommit = true");
            }

            R retVal;
            SQLException thrownInTry = null;
            try {
                retVal = connProcessor.process(conn);
            } catch (SQLException e) {
                thrownInTry = e;
                throw thrownInTry;
            } finally {
                try {
                    if (!oldAutoCommit) {
                        conn.setAutoCommit(false);
                        logger.debug("connection properties reverted: autoCommit = false");
                    }
                } catch (SQLException | RuntimeException e) {
                    // catch to prevent override exception thrown in try-block
                    if (thrownInTry != null) {
                        thrownInTry.addSuppressed(e);
                    } else {
                        throw e;
                    }
                }
            }
            return retVal;
        };
    }

    final <R> ConnectionProcessor<R> transact(final ConnectionProcessor<R> connProcessor) {
        return conn -> {
            final boolean oldAutoCommit = conn.getAutoCommit();
            if (oldAutoCommit) {
                conn.setAutoCommit(false);
                logger.debug("connection properties changed: autoCommit = false");
            }

            R retVal;
            SQLException thrownInTry = null;
            try {
                retVal = connProcessor.process(conn);
                conn.commit();
            } catch (SQLException e) {
                thrownInTry = e;
                throw thrownInTry;
            } finally {
                try {
                    // rollback on committed Connection affects nothing
                    conn.rollback();

                    if (oldAutoCommit) {
                        conn.setAutoCommit(true);
                        logger.debug("connection properties reverted: autoCommit = true");
                    }
                } catch (SQLException | RuntimeException e) {
                    // catch to prevent override exception thrown in try-block
                    if (thrownInTry != null) {
                        thrownInTry.addSuppressed(e);
                    } else {
                        throw e;
                    }
                }
            }
            return retVal;
        };
    }

    final <R> ConnectionProcessor<R> readProcessor(@Nonnull final Accessor<ReadableLinker, R> accessor) {
        return conn -> {
            try (ReadableLinker linker = Linkers.readable(conn)) {
                return accessor.access(linker);
            }
        };
    }

    final <R> ConnectionProcessor<R> writeProcessor(@Nonnull final Accessor<WritableLinker, R> accessor) {
        return conn -> {
            try (WritableLinker linker = Linkers.writable(conn)) {
                return accessor.access(linker);
            }
        };
    }

    final <R> ConnectionProcessor<R> readWriteProcessor(@Nonnull final Accessor<ReadWritableLinker, R> accessor) {
        return conn -> {
            try (ReadWritableLinker linker = Linkers.readWritable(conn)) {
                return accessor.access(linker);
            }
        };
    }

    final <R> ConnectionProcessor<R> callProcessor(@Nonnull final Accessor<CallableLinker, R> accessor) {
        return conn -> {
            try (CallableLinker linker = Linkers.callable(conn)) {
                return accessor.access(linker);
            }
        };
    }

    final <R> ConnectionProcessor<R> universalProcessor(@Nonnull final Accessor<UniversalLinker, R> accessor) {
        return conn -> {
            try (UniversalLinker linker = Linkers.universal(conn)) {
                return accessor.access(linker);
            }
        };
    }

    final <R> ConnectionProcessor<R> nativeJdbcProcessor(@Nonnull final Accessor<NativeJdbcLinker, R> accessor) {
        return conn -> {
            try (NativeJdbcLinker linker = Linkers.nativeJdbc(conn)) {
                return accessor.access(linker);
            }
        };
    }

    private <R> R doRunProcessor(ConnectionProcessor<R> connProcessor) throws SQLException {
        try (Connection conn = provider.getConnection()) {
            return connProcessor.process(conn);
        }
    }

    final <R> R runProcessor(ConnectionProcessor<R> connProcessor) {
        try {
            return doRunProcessor(connProcessor);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    final <R> MaybeException<SQLException, R> tryRunProcessor(ConnectionProcessor<R> connProcessor) {
        return new MaybeException<SQLException, R>() {
            private final R result;
            private final SQLException exception;

            private volatile boolean handled = false;

            {
                R tmpResult;
                SQLException tmpException;

                // only handles SQLException, other Exception will be thrown immediately
                try {
                    tmpResult = doRunProcessor(connProcessor);
                    tmpException = null;
                } catch (SQLException e) {
                    tmpResult = null;
                    tmpException = e;
                }

                result = tmpResult;
                exception = tmpException;
            }

            @Override
            public R handle(@Nonnull Function<SQLException, R> handler) {
                if (handled) {
                    throw new IllegalStateException("exception has been handled");
                }

                handled = true;
                return exception == null ? result : handler.apply(exception);
            }
        };
    }

    @Override
    public final void read(@Nonnull VoidAccessor<ReadableLinker> accessor) {
        read(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryRead(@Nonnull VoidAccessor<ReadableLinker> accessor) {
        return MaybeException.asVoid(tryRead(VoidAccessor.asAccessor(accessor)));
    }

    @Override
    public final void write(@Nonnull VoidAccessor<WritableLinker> accessor) {
        write(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryWrite(@Nonnull VoidAccessor<WritableLinker> accessor) {
        return MaybeException.asVoid(tryWrite(VoidAccessor.asAccessor(accessor)));
    }

    @Override
    public final void readWrite(@Nonnull VoidAccessor<ReadWritableLinker> accessor) {
        readWrite(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryReadWrite(@Nonnull VoidAccessor<ReadWritableLinker> accessor) {
        return MaybeException.asVoid(tryReadWrite(VoidAccessor.asAccessor(accessor)));
    }

    @Override
    public final void call(@Nonnull VoidAccessor<CallableLinker> accessor) {
        call(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryCall(@Nonnull VoidAccessor<CallableLinker> accessor) {
        return MaybeException.asVoid(tryCall(VoidAccessor.asAccessor(accessor)));
    }

    @Override
    public final void link(@Nonnull VoidAccessor<UniversalLinker> accessor) {
        link(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryLink(@Nonnull VoidAccessor<UniversalLinker> accessor) {
        return MaybeException.asVoid(tryLink(VoidAccessor.asAccessor(accessor)));
    }

    @Override
    public final void nativeJdbc(@Nonnull VoidAccessor<NativeJdbcLinker> accessor) {
        nativeJdbc(VoidAccessor.asAccessor(accessor));
    }

    @Nonnull
    @Override
    public final VoidMaybeException<SQLException> tryNativeJdbc(@Nonnull VoidAccessor<NativeJdbcLinker> accessor) {
        return MaybeException.asVoid(tryNativeJdbc(VoidAccessor.asAccessor(accessor)));
    }

    @Nonnull
    @Override
    public final Royce transactional() {
        if (transactionalSingleton != null) {
            return transactionalSingleton.getInstance();
        } else {
            throw new AssertionError();
        }
    }

    @Nonnull
    @Override
    public final Royce nonTransactional() {
        if (nonTransactionalSingleton != null) {
            return nonTransactionalSingleton.getInstance();
        } else {
            throw new AssertionError();
        }
    }

    @Override
    public final boolean isNonTransactional() {
        return Royce.super.isNonTransactional();
    }

    @FunctionalInterface
    interface ConnectionProcessor<R> {
        R process(Connection connection) throws SQLException;
    }
}
