package app.kumasuke.blog.rof.util;

import app.kumasuke.blog.rof.annotation.RetryOnFailure;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnreliableApiCaller {
    @RetryOnFailure(randomize = true, retryFor = IOException.class)
    public String sayHelloFromForeigners() throws IOException {
        if (Math.random() < 0.3) {
            return "你好，世界！";
        } else {
            if (Math.random() < 0.2) {
                throw new RuntimeException();
            } else {
                throw new IOException();
            }
        }
    }
}
