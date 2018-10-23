package app.kumasuke.blog.rof.config;

import app.kumasuke.blog.rof.annotation.RetryOnFailure;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static app.kumasuke.blog.rof.config.AbstractRetryOnFailureBeanMethodCallback.MethodWrapper;
import static app.kumasuke.blog.rof.config.AbstractRetryOnFailureBeanMethodCallback.RetryConfig;

@Component
public class RetryOnFailureBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Map<MethodWrapper, RetryConfig> methodToConfig = getAnnotatedMethodToConfig(bean);

        if (hasMethodAnnotated(methodToConfig)) {
            return enhanceBean(bean, methodToConfig);
        } else {
            return bean;
        }
    }

    private Object enhanceBean(Object bean, Map<MethodWrapper, RetryConfig> methodToConfig) {
        Class<?> beanClass = bean.getClass();

        if (isInterfaceBasedBean(beanClass)) {
            return enhanceWithProxy(bean, methodToConfig);
        } else {
            if (Modifier.isFinal(beanClass.getModifiers())) {
                throw new BeanCreationException("Cannot process with final modified class: " +
                                                        beanClass.getCanonicalName());
            } else {
                return enhanceWithCGLib(beanClass, methodToConfig);
            }
        }
    }

    private boolean hasMethodAnnotated(Map<MethodWrapper, RetryConfig> methodToConfig) {
        return !methodToConfig.isEmpty();
    }

    private Object enhanceWithProxy(Object bean, Map<MethodWrapper, RetryConfig> methodToConfig) {
        final Class<?> beanClass = bean.getClass();
        final RetryOnFailureBeanInvocationHandler handler =
                new RetryOnFailureBeanInvocationHandler(bean, methodToConfig);
        return Proxy.newProxyInstance(beanClass.getClassLoader(),
                                      beanClass.getInterfaces(),
                                      handler);
    }

    private Object enhanceWithCGLib(Class<?> beanClass, Map<MethodWrapper, RetryConfig> methodToConfig) {
        final RetryOnFailureBeanMethodInterceptor interceptor =
                new RetryOnFailureBeanMethodInterceptor(methodToConfig);

        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback(interceptor);
        return enhancer.create();
    }

    private Map<MethodWrapper, RetryConfig> getAnnotatedMethodToConfig(Object bean) {
        if (bean == null) {
            return Collections.emptyMap();
        } else {
            final Method[] publicMethods = bean.getClass().getMethods();
            final Map<MethodWrapper, RetryConfig> result = new HashMap<>();
            for (Method m : publicMethods) {
                if (Modifier.isStatic(m.getModifiers())) continue;

                RetryOnFailure rofAnnotation = m.getAnnotation(RetryOnFailure.class);
                if (rofAnnotation != null) {
                    result.put(new MethodWrapper(m), new RetryConfig(rofAnnotation));
                }
            }

            return Collections.unmodifiableMap(result);
        }
    }

    private boolean isInterfaceBasedBean(Class<?> beanClass) {
        final Class<?>[] interfaces = beanClass.getInterfaces();
        return interfaces.length != 0 &&
                !beanClass.getCanonicalName().startsWith("java") &&
                Stream.of(interfaces).anyMatch(i -> !i.getCanonicalName().startsWith("java"));
    }
}
