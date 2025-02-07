package com.example.springbootkeycloak.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmin() {
        return "KEYCLOAK ADMIN";
    }

    @GetMapping("/hello-user")
    @PreAuthorize("hasAnyRole('admin_client_role','user_client_role')")
    public String helloUser() {
        return "KEYCLOAK USER";
    }
}
