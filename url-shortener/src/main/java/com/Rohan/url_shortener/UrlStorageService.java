package com.Rohan.url_shortener;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlStorageService {
    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    public void saveUrl(String id,  String Url){
        storage.put(id, Url);

    }

    public String getLongUrl(String id){
        String originalUrl = storage.get(id);

        return originalUrl != null? originalUrl : null;
    }
}
