package com.example.cloudgateway;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    Environment env;

    @GetMapping("/health_check")
    public String status() {
        return String.format("ok %s",env.getProperty("token.secret"));
    }
}
