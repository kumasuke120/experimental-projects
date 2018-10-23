package app.kumasuke.blog.rof.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RetryOnFailure {
    int attempts() default 3;

    long delay() default 1500;

    boolean randomize() default false;

    Class<? extends Throwable>[] retryFor() default {};

    Class<? extends Throwable>[] ignoreFor() default {};
}
