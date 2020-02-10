package com.kumasuke.util;

import java.io.IOException;

public class TestHelper {
    private static final int RETRY_TIME = 5;

    public static <T> T tryToGet(ResultGetter<T> getter, ExceptionHandler handler, Object... args) {
        boolean finished = false;
        int retryTime = 0;
        T result = null;

        while (!finished && retryTime < RETRY_TIME) {
            try {
                result = getter.getResult(args);
                finished = true;
            } catch (Exception e) {
                if (handler.shouldOverlook(e))
                    retryTime++;
                else
                    finished = true;
            }
        }

        return result;
    }

    public static <T> T tryToGet(ResultGetter<T> getter, Object... args) {
        return tryToGet(getter,
                e -> e instanceof IOException
                        && e.getMessage().contains("timed out"), args);
    }
}
