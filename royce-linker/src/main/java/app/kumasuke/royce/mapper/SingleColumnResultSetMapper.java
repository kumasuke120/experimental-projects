package app.kumasuke.royce.mapper;

import app.kumasuke.royce.util.GenericSingletonContext;
import app.kumasuke.royce.util.ResultSets;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

class SingleColumnResultSetMapper<T> implements ResultSetMapper<T> {
    private static final GenericSingletonContext<SingleColumnResultSetMapper> singletons =
            new GenericSingletonContext<>(c -> new SingleColumnResultSetMapper<>(c, 1));

    private final Class<T> javaType;
    private final int columnIndex;

    private SingleColumnResultSetMapper(@Nonnull Class<T> javaType, int columnIndex) {
        this.javaType = javaType;
        this.columnIndex = columnIndex;
    }

    static <T> SingleColumnResultSetMapper<T> getInstance(Class<T> genericType) {
        @SuppressWarnings("unchecked")
        SingleColumnResultSetMapper<T> result =
                (SingleColumnResultSetMapper<T>) singletons.getInstance(genericType);
        return result;
    }

    static <T> SingleColumnResultSetMapper<T> getInstance(Class<T> genericType, int columnIndex) {
        if (columnIndex < 1) throw new IllegalArgumentException("'columnIndex' should be greater than or equal to 1");

        if (columnIndex == 1) {
            return getInstance(genericType);
        } else {
            return new SingleColumnResultSetMapper<>(genericType, columnIndex);
        }
    }

    @Override
    public T mapRow(@Nonnull ResultSet rs) throws SQLException {
        return javaType.cast(ResultSets.getValue(rs, columnIndex, javaType));
    }
}
