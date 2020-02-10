package com.kumasuke.util;

@FunctionalInterface
public interface ResultGetter<T> {
    T getResult(Object... args) throws Exception;
}
