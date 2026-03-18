package com.Rohan.jwt_auth.controller; // Make sure this matches your project's base package!

 // Updated import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Rohan.jwt_auth.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    // Cleaned up the variable name
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request){

        // 1. The Simulated Database Check
        if ("Rohan".equals(request.getUsername()) && "password123".equals(request.getPassword())) {

            // 2. Credentials match! Generate the cryptographic token.
            String token = jwtUtil.generateToken(request.getUsername());

            // 3. Return the token in a clean JSON response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        } else {
            // 4. Hacker detected. Return a 401 Unauthorized status.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}