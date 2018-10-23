package app.kumasuke.blog.rof.config;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

class RetryOnFailureBeanMethodInterceptor extends AbstractRetryOnFailureBeanMethodCallback
        implements MethodInterceptor {
    RetryOnFailureBeanMethodInterceptor(Map<MethodWrapper, RetryConfig> methodToConfig) {
        super(methodToConfig);
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return doCallback(new MethodWrapper(method), () -> proxy.invokeSuper(object, args));
    }
}
