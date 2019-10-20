package app.kumasuke.royce;

import app.kumasuke.royce.except.MaybeException;
import app.kumasuke.royce.linker.*;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.function.Supplier;

class TransactionalRoyce extends AbstractRoyce {
    TransactionalRoyce(ConnectionProvider provider) {
        this(provider, null);
    }

    TransactionalRoyce(ConnectionProvider provider, Supplier<Royce> nonTransactionalConstructor) {
        super(provider);
        setTransactionalConstructor(() -> this);
        setNonTransactionalConstructor(
                nonTransactionalConstructor != null ? nonTransactionalConstructor :
                        () -> new NonTransactionalRoyce(provider, () -> TransactionalRoyce.this));
    }

    @Override
    public <R> R read(@Nonnull Accessor<ReadableLinker, R> accessor) {
        return runProcessor(readOnly(transact(readProcessor(accessor))));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryRead(@Nonnull Accessor<ReadableLinker, R> accessor) {
        return tryRunProcessor(readOnly(transact(readProcessor(accessor))));
    }

    @Override
    public <R> R write(@Nonnull Accessor<WritableLinker, R> accessor) {
        return runProcessor(transact(writeProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryWrite(@Nonnull Accessor<WritableLinker, R> accessor) {
        return tryRunProcessor(transact(writeProcessor(accessor)));
    }

    @Override
    public <R> R readWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor) {
        return runProcessor(transact(readWriteProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryReadWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor) {
        return tryRunProcessor(transact(readWriteProcessor(accessor)));
    }

    @Override
    public <R> R call(@Nonnull Accessor<CallableLinker, R> accessor) {
        return runProcessor(transact(callProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryCall(@Nonnull Accessor<CallableLinker, R> accessor) {
        return tryRunProcessor(transact(callProcessor(accessor)));
    }

    @Override
    public <R> R link(@Nonnull Accessor<UniversalLinker, R> accessor) {
        return runProcessor(transact(universalProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryLink(@Nonnull Accessor<UniversalLinker, R> accessor) {
        return tryRunProcessor(transact(universalProcessor(accessor)));
    }

    @Override
    public <R> R nativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor) {
        return runProcessor(transact(nativeJdbcProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryNativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor) {
        return tryRunProcessor(transact(nativeJdbcProcessor(accessor)));
    }

    @Override
    public boolean isTransactional() {
        return true;
    }
}
