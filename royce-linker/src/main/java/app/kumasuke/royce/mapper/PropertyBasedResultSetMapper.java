package app.kumasuke.royce.mapper;

import app.kumasuke.royce.util.ResultSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A <code>ResultSetMapper</code> that converts given <code>ResultSet</code> to required Bean
 * class, which is based on the names of properties of Bean
 *
 * @param <O> the type of Bean class
 */
class PropertyBasedResultSetMapper<O> implements ResultSetMapper<O> {
    private static final Logger logger = LoggerFactory.getLogger(PropertyBasedResultSetMapper.class);
    private static final Pattern snakeCasePattern = Pattern.compile("(?i)[_a-z][_a-z0-9]*_[_a-z0-9]*");

    private final Class<O> beanClass;
    private final Map<String, PropertyDescriptor> nameToProperties;

    PropertyBasedResultSetMapper(Class<O> beanClass) {
        this.beanClass = beanClass;
        try {
            this.nameToProperties = Collections.unmodifiableMap(getNameToProperties());
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("error occurred when introspecting bean", e);
        }
    }

    private Map<String, PropertyDescriptor> getNameToProperties() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
    }

    @Override
    public O mapRow(@Nonnull ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        O o = newInstance();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            PropertyDescriptor descriptor = labelToProperty(label);

            if (descriptor != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("column mapped: " + columnStringForLog(metaData, i) +
                                         " -> " + propertyStringForLog(descriptor));
                }

                Class<?> type = descriptor.getPropertyType();
                Object value = ResultSets.getValue(rs, i, type);
                setPropertyValue(o, descriptor, value);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("column map failed: cannot find suitable property for " +
                                         columnStringForLog(metaData, i));
                }
            }
        }

        return o;
    }

    private PropertyDescriptor labelToProperty(String label) {
        if (nameToProperties.containsKey(label)) {
            return nameToProperties.get(label);
        } else {
            if ((label != null && !label.isEmpty()) &&
                    snakeCasePattern.matcher(label).matches()) { // test if snake_case
                final String camelCase = snakeCaseToCamelCase(label);
                return nameToProperties.get(camelCase);
            } else {
                return null;
            }
        }
    }

    private String snakeCaseToCamelCase(String snakeCase) {
        String[] words = snakeCase.toLowerCase().split("_");
        for (int i = 1; i < words.length; i++) {
            final String w = words[i];
            words[i] = Character.toUpperCase(w.charAt(0)) + w.substring(1);
        }
        return String.join("", words);
    }

    private String columnStringForLog(ResultSetMetaData metaData, int index) throws SQLException {
        String table = metaData.getTableName(index);
        String label = metaData.getColumnLabel(index);
        table = table == null ? "" : table;
        final String column = table.isEmpty() ? label : table + "." + label;
        return "<column>('" + column + "')";
    }

    private String propertyStringForLog(PropertyDescriptor descriptor) {
        return "<property>('" + beanClass.getCanonicalName() + "#" + descriptor.getName() + "')";
    }

    private O newInstance() {
        try {
            return beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("cannot instantiate class '" + beanClass.getCanonicalName() +
                                                    "'", e);
        }
    }

    private void setPropertyValue(Object object, PropertyDescriptor descriptor, Object value) {
        Class<?> propertyType = descriptor.getPropertyType();
        if (propertyType.isPrimitive() && value == null) {
            throw new IllegalArgumentException
                    ("primitive property '" + descriptor.getName() + "' cannot be null");
        }

        Method writeMethod = descriptor.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalArgumentException("cannot find any setter of property '" + descriptor.getName() +
                                                       "' in the class '" + beanClass.getCanonicalName() + "'");
        }

        try {
            writeMethod.invoke(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("cannot invoke the setter of property '" + descriptor.getName() +
                                                       "' in the class '" + beanClass.getCanonicalName() + "'", e);
        } catch (InvocationTargetException e) {
            Throwable t;
            t = (t = e.getTargetException()) == null ? e : t;
            throw new IllegalStateException("error occurred when invoke setter of property '" +
                                                    descriptor.getName() + "'", t);
        }
    }

}
