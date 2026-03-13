package com.Rohan.url_shortener;

import org.springframework.stereotype.Service;


import java.util.Random;

@Service
public class UrlEncoderService {
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static Random random = new Random();

    public String encode(String originalUrl){
        StringBuilder shortid = new StringBuilder();
        for(int i =0;i<6;i++){

            int randomnum = random.nextInt(ALLOWED_CHARS.length());
            shortid.append(ALLOWED_CHARS.charAt(randomnum));
        }
        
        return shortid.toString();
    }
}
