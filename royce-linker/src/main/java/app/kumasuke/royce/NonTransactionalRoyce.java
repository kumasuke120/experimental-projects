package app.kumasuke.royce;

import app.kumasuke.royce.except.MaybeException;
import app.kumasuke.royce.linker.*;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.function.Supplier;

class NonTransactionalRoyce extends AbstractRoyce {
    NonTransactionalRoyce(ConnectionProvider provider) {
        this(provider, null);
    }

    NonTransactionalRoyce(ConnectionProvider provider, Supplier<Royce> transactionalConstructor) {
        super(provider);
        setNonTransactionalConstructor(() -> this);
        setTransactionalConstructor(
                transactionalConstructor != null ? transactionalConstructor :
                        () -> new TransactionalRoyce(provider, () -> NonTransactionalRoyce.this));
    }

    @Override
    public <R> R read(@Nonnull Accessor<ReadableLinker, R> accessor) {
        return runProcessor(readOnly(nonTransact(readProcessor(accessor))));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryRead(@Nonnull Accessor<ReadableLinker, R> accessor) {
        return tryRunProcessor(readOnly(nonTransact(readProcessor(accessor))));
    }

    @Override
    public <R> R write(@Nonnull Accessor<WritableLinker, R> accessor) {
        return runProcessor(nonTransact(writeProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryWrite(@Nonnull Accessor<WritableLinker, R> accessor) {
        return tryRunProcessor(nonTransact(writeProcessor(accessor)));
    }

    @Override
    public <R> R readWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor) {
        return runProcessor(nonTransact(readWriteProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryReadWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor) {
        return tryRunProcessor(nonTransact(readWriteProcessor(accessor)));
    }

    @Override
    public <R> R call(@Nonnull Accessor<CallableLinker, R> accessor) {
        return runProcessor(nonTransact(callProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryCall(@Nonnull Accessor<CallableLinker, R> accessor) {
        return tryRunProcessor(nonTransact(callProcessor(accessor)));
    }

    @Override
    public <R> R link(@Nonnull Accessor<UniversalLinker, R> accessor) {
        return runProcessor(nonTransact(universalProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryLink(@Nonnull Accessor<UniversalLinker, R> accessor) {
        return tryRunProcessor(nonTransact(universalProcessor(accessor)));
    }

    @Override
    public <R> R nativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor) {
        return runProcessor(nonTransact(nativeJdbcProcessor(accessor)));
    }

    @Nonnull
    @Override
    public <R> MaybeException<SQLException, R> tryNativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor) {
        return tryRunProcessor(nonTransact(nativeJdbcProcessor(accessor)));
    }

    @Override
    public boolean isTransactional() {
        return false;
    }
}
