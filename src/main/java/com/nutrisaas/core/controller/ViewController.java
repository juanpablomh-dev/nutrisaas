package com.nutrisaas.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/app/")
    public String app() {
        return "forward:/app/app.html";
    }

    @GetMapping("/app")
    public String redirectApp() {
        return "redirect:/app/";
    }

    @GetMapping("/login/")
    public String login() {
        return "forward:/app/login.html";
    }

    @GetMapping("/login")
    public String redirectLogin() {
        return "redirect:/login/";
    }

    @GetMapping("/docs")
    public String docs() {
        return "redirect:/swagger-ui/index.html";
    }
    
}