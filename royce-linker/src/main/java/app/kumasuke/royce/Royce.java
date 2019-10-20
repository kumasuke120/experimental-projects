package app.kumasuke.royce;

import app.kumasuke.royce.except.MaybeException;
import app.kumasuke.royce.except.UncheckedSQLException;
import app.kumasuke.royce.except.VoidMaybeException;
import app.kumasuke.royce.linker.*;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * The main interface of <code>Royce-Liner</code> that provides method to commute with
 * JDBC by provided auto-created <code>Linker</code>s
 *
 * @author Kumasuke&lt;bearcomingx@gmail.com&gt;
 * @apiNote <a href="https://github.com/kumasuke120/royce-linker/" target="_blank">
 * https://github.com/kumasuke120/royce-linker/</a>
 */
public interface Royce {
    /**
     * Creates a new instance of transactional <code>Royce</code> by specified <code>ConnectionProvider</code>.
     *
     * @param provider the <code>ConnectionProvider</code> instance that this <code>Royce</code> will use
     * @return a newly-created <code>Royce</code>
     */
    @Nonnull
    static Royce transactional(@Nonnull ConnectionProvider provider) {
        return new TransactionalRoyce(provider);
    }

    /**
     * Creates a new instance of non-transactional <code>Royce</code> by specified <code>ConnectionProvider</code>.
     *
     * @param provider the <code>ConnectionProvider</code> instance that this <code>Royce</code> will use
     * @return a newly-created <code>Royce</code>
     */
    @Nonnull
    static Royce nonTransactional(@Nonnull ConnectionProvider provider) {
        return new NonTransactionalRoyce(provider);
    }

    /**
     * Use <code>ReadableLinker</code> provided by this <code>Royce</code> to read from database, without
     * returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>ReadableLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see ReadableLinker
     */
    void read(@Nonnull VoidAccessor<ReadableLinker> accessor);

    /**
     * Use <code>ReadableLinker</code> provided by this <code>Royce</code> to read from database, return any
     * value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>ReadableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see ReadableLinker
     */
    <R> R read(@Nonnull Accessor<ReadableLinker, R> accessor);

    /**
     * Use <code>ReadableLinker</code> provided by this <code>Royce</code> to read from database, without
     * returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>ReadableLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see ReadableLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryRead(@Nonnull VoidAccessor<ReadableLinker> accessor);

    /**
     * Use <code>ReadableLinker</code> provided by this <code>Royce</code> to read from database, return any
     * value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>ReadableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see ReadableLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryRead(@Nonnull Accessor<ReadableLinker, R> accessor);

    /**
     * Use <code>WritableLinker</code> provided by this <code>Royce</code> to write to database, without
     * returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>WritableLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see WritableLinker
     */
    void write(@Nonnull VoidAccessor<WritableLinker> accessor);

    /**
     * Use <code>WritableLinker</code> provided by this <code>Royce</code> to write to database, return any
     * value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>WritableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see WritableLinker
     */
    <R> R write(@Nonnull Accessor<WritableLinker, R> accessor);

    /**
     * Use <code>WritableLinker</code> provided by this <code>Royce</code> to write to database, without
     * returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>ReadableLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see WritableLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryWrite(@Nonnull VoidAccessor<WritableLinker> accessor);

    /**
     * Use <code>WritableLinker</code> provided by this <code>Royce</code> to write to database, return any
     * value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>WritableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see WritableLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryWrite(@Nonnull Accessor<WritableLinker, R> accessor);

    /**
     * Use <code>ReadWritableLinker</code> provided by this <code>Royce</code> to read from or write to database
     * , without returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>ReadWritableLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see ReadWritableLinker
     */
    void readWrite(@Nonnull VoidAccessor<ReadWritableLinker> accessor);

    /**
     * Use <code>ReadWritableLinker</code> provided by this <code>Royce</code> to read from or write to database
     * , return any value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>ReadWritableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see ReadWritableLinker
     */
    <R> R readWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor);

    /**
     * Use <code>ReadWritableLinker</code> provided by this <code>Royce</code> to read from or write to database
     * , without returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>ReadWritableLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see ReadWritableLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryReadWrite(@Nonnull VoidAccessor<ReadWritableLinker> accessor);

    /**
     * Use <code>ReadWritableLinker</code> provided by this <code>Royce</code> to read from or write to database
     * , return any value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>ReadWritableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see ReadWritableLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryReadWrite(@Nonnull Accessor<ReadWritableLinker, R> accessor);

    /**
     * Use <code>CallableLinker</code> provided by this <code>Royce</code> to operate with procedures in database
     * , without returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>ReadWritableLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see CallableLinker
     */
    void call(@Nonnull VoidAccessor<CallableLinker> accessor);

    /**
     * Use <code>CallableLinker</code> provided by this <code>Royce</code> to operate with procedures database
     * , return any value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>CallableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see CallableLinker
     */
    <R> R call(@Nonnull Accessor<CallableLinker, R> accessor);

    /**
     * Use <code>CallableLinker</code> provided by this <code>Royce</code> to operate with procedures database
     * , without returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>CallableLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see CallableLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryCall(@Nonnull VoidAccessor<CallableLinker> accessor);

    /**
     * Use <code>CallableLinker</code> provided by this <code>Royce</code> to operate with procedures database
     * , return any value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>CallableLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see CallableLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryCall(@Nonnull Accessor<CallableLinker, R> accessor);

    /**
     * Use <code>UniversalLinker</code> provided by this <code>Royce</code> to operate with database, without
     * returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>UniversalLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see UniversalLinker
     */
    void link(@Nonnull VoidAccessor<UniversalLinker> accessor);

    /**
     * Use <code>UniversalLinker</code> provided by this <code>Royce</code> to operate with database, return
     * any value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>UniversalLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see ReadableLinker
     */
    <R> R link(@Nonnull Accessor<UniversalLinker, R> accessor);

    /**
     * Use <code>UniversalLinker</code> provided by this <code>Royce</code> to operate with database, without
     * returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>UniversalLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see UniversalLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryLink(@Nonnull VoidAccessor<UniversalLinker> accessor);

    /**
     * Use <code>UniversalLinker</code> provided by this <code>Royce</code> to operate with database, return
     * any value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>UniversalLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see UniversalLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryLink(@Nonnull Accessor<UniversalLinker, R> accessor);

    /**
     * Use <code>NativeJdbcLinker</code> provided by this <code>Royce</code> to operate with database via JDBC,
     * without returning any value.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>NativeJdbcLinker</code>, without returning any value
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see NativeJdbcLinker
     */
    void nativeJdbc(@Nonnull VoidAccessor<NativeJdbcLinker> accessor);

    /**
     * Use <code>NativeJdbcLinker</code> provided by this <code>Royce</code> to operate with database via JDBC,
     * return any value returned by accessor.<br>
     * Any exception thrown within the accessor will be thrown right away in the form of
     * <code>UncheckedSQLException</code>.
     *
     * @param accessor accessor to operate with <code>NativeJdbcLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return the return value returned by accessor
     * @throws UncheckedSQLException wrapped <code>SQLException</code> that is thrown within accessor
     * @see NativeJdbcLinker
     */
    <R> R nativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor);

    /**
     * Use <code>NativeJdbcLinker</code> provided by this <code>Royce</code> to operate with database via JDBC,
     * without returning any value in the accessor.<br>
     * Any exception could be handled by return value of type <code>VoidMaybeException</code>.
     *
     * @param accessor accessor to operate with <code>NativeJdbcLinker</code>, without returning any value
     * @return <code>VoidMaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see NativeJdbcLinker
     * @see VoidMaybeException
     */
    @Nonnull
    VoidMaybeException<SQLException> tryNativeJdbc(@Nonnull VoidAccessor<NativeJdbcLinker> accessor);

    /**
     * Use <code>NativeJdbcLinker</code> provided by this <code>Royce</code> to operate with database via JDBC,
     * return any value returned by accessor in the form of <code>MaybeException</code>.<br>
     * Any exception could be handled by return value of type <code>MaybeException</code>.
     *
     * @param accessor accessor to operate with <code>NativeJdbcLinker</code>, which could return any value of
     *                 type {@code <R>}
     * @param <R>      the type of return value
     * @return <code>MaybeException</code> to handle with <code>SQLException</code> thrown within accessor
     * @see NativeJdbcLinker
     * @see MaybeException
     */
    @Nonnull
    <R> MaybeException<SQLException, R> tryNativeJdbc(@Nonnull Accessor<NativeJdbcLinker, R> accessor);

    /**
     * Converts this <code>Royce</code> to a transactional counterpart if it is non-transactional,
     * return itself otherwise.
     *
     * @return transactional version of <code>Royce</code> using the underlying <code>ConnectionProvider</code> of
     * this <code>Royce</code>
     */
    @Nonnull
    Royce transactional();

    /**
     * Converts this <code>Royce</code> to a non-transactional counterpart if it is transactional,
     * return itself otherwise.
     *
     * @return non-transactional version of <code>Royce</code> using the underlying <code>ConnectionProvider</code>
     * of this <code>Royce</code>
     */
    @Nonnull
    Royce nonTransactional();

    /**
     * Tests if this <code>Royce</code> is transactional.
     *
     * @return <code>true</code> if if this <code>Royce</code> is transactional, <code>false</code> otherwise
     */
    boolean isTransactional();

    /**
     * Tests if this <code>Royce</code> is non-transactional.
     *
     * @return <code>false</code> if if this <code>Royce</code> is transactional, <code>true</code> otherwise
     */
    default boolean isNonTransactional() {
        return !isTransactional();
    }
}
