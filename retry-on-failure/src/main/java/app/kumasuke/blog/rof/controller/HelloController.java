package app.kumasuke.blog.rof.controller;

import app.kumasuke.blog.rof.service.UnreliableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {
    private final UnreliableService service;

    @Autowired
    public HelloController(UnreliableService service) {
        this.service = service;
    }

    @GetMapping("/say-hello")
    public Map<String, String> sayHello() {
        return Collections.singletonMap("message", service.sayHello());
    }

    @GetMapping("/say-hello-from-foreigners")
    public Map<String, String> sayHelloFromForeigners() {
        return Collections.singletonMap("message", service.sayHelloFromForeigners());
    }

    @ExceptionHandler
    public Map<String, String> handleException(Exception e) {
        return Collections.singletonMap("exception", e.getClass().getCanonicalName());
    }
}
