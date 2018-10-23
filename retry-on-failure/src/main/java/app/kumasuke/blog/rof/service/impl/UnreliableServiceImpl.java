package app.kumasuke.blog.rof.service.impl;

import app.kumasuke.blog.rof.annotation.RetryOnFailure;
import app.kumasuke.blog.rof.except.ServiceUnavailableException;
import app.kumasuke.blog.rof.service.UnreliableService;
import app.kumasuke.blog.rof.util.UnreliableApiCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UnreliableServiceImpl implements UnreliableService {
    private final UnreliableApiCaller apiCaller;

    @Autowired
    public UnreliableServiceImpl(UnreliableApiCaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    @Override
    @RetryOnFailure(attempts = 5, delay = 300)
    public String sayHello() {
        if (Math.random() < 0.5) {
            return "Hello World!";
        } else {
            throw new ServiceUnavailableException();
        }
    }

    @Override
    public String sayHelloFromForeigners() {
        try {
            return apiCaller.sayHelloFromForeigners();
        } catch (IOException e) {
            throw new ServiceUnavailableException();
        }
    }
}
