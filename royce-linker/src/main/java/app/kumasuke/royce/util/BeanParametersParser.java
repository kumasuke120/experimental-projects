package app.kumasuke.royce.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>ParametersParser</code> that parses a SQL with named parameters and its corresponding Bean objects,
 * returning a SQL parameterized by quotation mark and an array of parameters' values in the order of their
 * position in the given SQL with named parameters
 */
public class BeanParametersParser implements ParametersParser<Object> {
    private static final int PS_READY = 0;
    private static final int PS_NAMED_PARAMETERS_SET = 1;
    private static final int PS_PARSER_PARSED = 2;

    private final String sqlWithNames;

    private Object bean;
    private Map<String, Object> namedParameters;
    private MapParametersParser parser;
    private int parseState;

    public BeanParametersParser(String sqlWithNames, Object bean) {
        this.sqlWithNames = sqlWithNames;
        this.bean = bean;
        this.parseState = PS_READY;
    }

    public void setBean(Object bean) {
        this.bean = bean;
        parseState = PS_READY;
    }

    @Override
    public void parse() {
        parseAndSetNamedParameters();
        prepareParserAndParse();
    }

    @Override
    public String getRawSql() {
        if (isParsed()) {
            return parser.getRawSql();
        } else {
            throw new IllegalStateException("bean parameters haven't been parsed yet");
        }
    }

    @Override
    public Object[] getParameters() {
        if (isParsed()) {
            return parser.getParameters();
        } else {
            throw new IllegalStateException("bean parameters haven't been parsed yet");
        }
    }

    private void prepareParserAndParse() {
        if ((parseState & PS_PARSER_PARSED) == PS_PARSER_PARSED) {
            return;
        }

        if (parser == null) {
            parser = new MapParametersParser(sqlWithNames, namedParameters);
        } else {
            parser.setNamedParameters(namedParameters);
        }
        parser.parse();
        parseState |= PS_PARSER_PARSED;
    }

    private void parseAndSetNamedParameters() {
        if ((parseState & PS_NAMED_PARAMETERS_SET) == PS_NAMED_PARAMETERS_SET) {
            return;
        }

        if (bean == null) {
            namedParameters = Collections.emptyMap();
        } else {
            final Class<?> beanClass = getBeanClass();
            final BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(beanClass);
            } catch (IntrospectionException e) {
                throw new IllegalArgumentException("error occurred when introspecting bean", e);
            }

            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            namedParameters = new HashMap<>();
            for (PropertyDescriptor pd : descriptors) {
                String propertyName = pd.getName();
                if (!"class".equals(propertyName)) {
                    Object propertyValue = getPropertyValue(beanClass, pd);
                    namedParameters.put(propertyName, propertyValue);
                }
            }
        }
        parseState |= PS_NAMED_PARAMETERS_SET;
    }

    private Object getPropertyValue(Class<?> beanClass, PropertyDescriptor pd) {
        final Method readMethod = pd.getReadMethod();

        try {
            // lets every public method of any class accessible.
            // it makes the following usage possible:
            // linker.namedUpdate("UPDATE test.table_name SET column_name = :value",
            //                    new Object() {
            //                        public String getValue() {
            //                            return "Value";
            //                        }
            //                    });
            if (Modifier.isPublic(readMethod.getModifiers()) && !readMethod.isAccessible()) {
                readMethod.setAccessible(true);
            }
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "cannot invoke the getter of property '" + pd.getName() + "' in the class '" +
                            getClassName(beanClass) + "'", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("error occurred when invoke getter of property '" +
                                                    pd.getName() + "'", unwrap(e));
        }
    }

    private Class<?> getBeanClass() {
        return bean.getClass();
    }

    private String getClassName(Class<?> clazz) {
        if (clazz == null) {
            return "";
        } else {
            String t;
            return (t = clazz.getCanonicalName()) == null ? clazz.getName() : t;
        }
    }

    private Throwable unwrap(InvocationTargetException e) {
        Throwable t;
        return (t = e.getTargetException()) == null ? e : t;
    }

    private boolean isParsed() {
        return parseState == (PS_NAMED_PARAMETERS_SET | PS_PARSER_PARSED);
    }
}
