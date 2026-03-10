package com.Rohan.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, RequestInfo> clientRequestMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Icoming request are intercepted");
        String ipAddress = request.getRemoteAddr();

        RequestInfo info = clientRequestMap.get(ipAddress);

        if (Objects.isNull(info)) {
            RequestInfo reqinf = new RequestInfo(1, System.currentTimeMillis());

            clientRequestMap.put(ipAddress, reqinf);
            System.out.println("first visit - allowed");

            filterChain.doFilter(request, response);

        }
        else{
            long curr_time = System.currentTimeMillis();
            long prev_time = info.getWindowsStartTime();

            long time_diff = curr_time - prev_time;

            if(time_diff > 10000){
                info.setRequestTime(1);
                info.setWindowsStartTime(System.currentTimeMillis());


                System.out.println("window reset - allowed");

                filterChain.doFilter(request, response);
            }
            else {
                if(info.getRequestTime() < 5){
                    info.setRequestTime(info.getRequestTime() + 1);

                    System.out.println("request \" + info.getRequestTime() + \" - allowed");

                    filterChain.doFilter(request, response);
                }
                else{
                    response.setStatus(429);
                    response.getWriter().write("Too many requests! Please wait 10 seconds.");
                    System.out.println("spam detected - blocked!");
                    return;
                }
            }
        }

        System.out.println(ipAddress);

    }
}
