package app.kumasuke.blog.rof.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class RetryOnFailureBeanInvocationHandler extends AbstractRetryOnFailureBeanMethodCallback
        implements InvocationHandler {

    private final Object bean;

    RetryOnFailureBeanInvocationHandler(Object bean, Map<MethodWrapper, RetryConfig> methodToConfig) {
        super(methodToConfig);
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retVal = doCallback(new MethodWrapper(method), () -> {
            try {
                return method.invoke(bean, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });

        Class<?> retType = method.getReturnType();
        if (retVal == null && retType.isPrimitive()) {
            return getDefaultValueForPrimitive(retType);
        } else {
            return retVal;
        }
    }

    private Object getDefaultValueForPrimitive(Class<?> clazz) {
        if (byte.class.equals(clazz)) {
            return (byte) 0;
        } else if (short.class.equals(clazz)) {
            return (short) 0;
        } else if (int.class.equals(clazz)) {
            return 0;
        } else if (long.class.equals(clazz)) {
            return 0L;
        } else if (float.class.equals(clazz)) {
            return 0F;
        } else if (double.class.equals(clazz)) {
            return 0D;
        } else if (char.class.equals(clazz)) {
            return (char) 0;
        } else if (boolean.class.equals(clazz)) {
            return false;
        } else if (void.class.equals(clazz)) {
            return null;
        } else {
            throw new AssertionError();
        }
    }
}
