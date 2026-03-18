package com.Rohan.jwt_auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    JwtInterceptor(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Strip out the word "Bearer "

            try {
                // 3. Extract the username and mathematically validate the signature
                String username = jwtUtil.extractUsername(token);

                if (jwtUtil.validateToken(token, username)) {
                    // Success! Attach the username to the request so our controllers know who called them
                    request.setAttribute("username", username);
                    return true; // Let the request pass through to the controller!
                }
            } catch (Exception e) {
                // If the math fails (hacked token, expired token), it throws an exception. We catch it and fail quietly.
            }
        }

        // 4. If we get here, they don't have a valid VIP pass. Kick them out!
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("401 Unauthorized: Invalid or missing JWT token");
        return false; // Block the request from going any further

    }
}
