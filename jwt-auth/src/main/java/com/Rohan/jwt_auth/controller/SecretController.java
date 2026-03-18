package com.Rohan.jwt_auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secret")
public class SecretController {

    @GetMapping("/data")
    public ResponseEntity<String> getSecretData(HttpServletRequest request) {
        // We pull the username that the Interceptor safely attached to the request
        String username = (String) request.getAttribute("username");

        return ResponseEntity.ok("Welcome to the VIP lounge, " + username + "! Your JWT token unlocked this data.");
    }
}