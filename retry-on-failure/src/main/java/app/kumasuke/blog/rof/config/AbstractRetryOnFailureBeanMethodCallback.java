package app.kumasuke.blog.rof.config;

import app.kumasuke.blog.rof.annotation.RetryOnFailure;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;

abstract class AbstractRetryOnFailureBeanMethodCallback {
    private static final Random rand = new SecureRandom();

    private final Map<MethodWrapper, RetryConfig> methodToConfig;

    AbstractRetryOnFailureBeanMethodCallback(Map<MethodWrapper, RetryConfig> methodToConfig) {
        this.methodToConfig = methodToConfig;
    }

    Object doCallback(MethodWrapper calledMethod, MethodInvoker invoker) throws Throwable {
        if (methodToConfig.containsKey(calledMethod)) {
            RetryConfig conf = methodToConfig.get(calledMethod);
            return invokeWithRetry(invoker, conf);
        }

        return invoker.invoke();
    }

    private Object invokeWithRetry(MethodInvoker invoker, RetryConfig conf) throws Throwable {
        int retryCount = 0;
        Exception lastThrown = null;

        while (retryCount++ < conf.attempts) {
            try {
                return invoker.invoke();
            } catch (Exception e) {
                if (!conf.retryFor.isEmpty() && !conf.retryFor.contains(e.getClass())) {
                    throw e;
                } else {
                    if (lastThrown == null) {
                        lastThrown = conf.ignoreFor.contains(e.getClass()) ? null : e;
                    } else {
                        e.addSuppressed(lastThrown);
                        lastThrown = e;
                    }

                    try {
                        waitForRetry(conf);
                    } catch (InterruptedException ie) {
                        e.addSuppressed(ie);
                    }
                }
            }
        }

        if (lastThrown == null) {
            return null;
        } else {
            throw lastThrown;
        }
    }

    private void waitForRetry(RetryConfig conf) throws InterruptedException {
        if (conf.randomize) {
            Thread.sleep(Math.abs(rand.nextLong()) % conf.delay + 1);
        } else {
            Thread.sleep(conf.delay);
        }
    }

    @FunctionalInterface
    interface MethodInvoker<R> {
        R invoke() throws Throwable;
    }

    static final class MethodWrapper {
        private final Method method;

        MethodWrapper(Method method) {
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodWrapper that = (MethodWrapper) o;
            return Objects.equals(method.getName(), that.method.getName()) &&
                    Arrays.equals(method.getParameterTypes(), that.method.getParameterTypes()) &&
                    Objects.equals(method.getReturnType(), that.method.getReturnType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(method.getName(), Arrays.hashCode(method.getParameterTypes()),
                                method.getReturnType());
        }

        @Override
        public String toString() {
            return "MethodWrapper{" +
                    "method=" + method +
                    '}';
        }
    }

    static class RetryConfig {
        final int attempts;
        final long delay;
        final boolean randomize;
        final Set<Class<? extends Throwable>> retryFor;
        final Set<Class<? extends Throwable>> ignoreFor;

        RetryConfig(RetryOnFailure rofAnnotation) {
            this.attempts = rofAnnotation.attempts();
            this.delay = rofAnnotation.delay();
            this.randomize = rofAnnotation.randomize();
            this.retryFor = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(rofAnnotation.retryFor())));
            this.ignoreFor = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(rofAnnotation.ignoreFor())));
        }
    }
}
