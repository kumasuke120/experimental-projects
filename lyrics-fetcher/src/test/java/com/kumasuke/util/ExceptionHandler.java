package com.kumasuke.util;

@FunctionalInterface
public interface ExceptionHandler {
    boolean shouldOverlook(Exception e);
}
