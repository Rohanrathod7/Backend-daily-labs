package com.Rohan.url_shortener;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URL;


@RestController
@RequestMapping()
public class UrlController {

    private final UrlEncoderService encoderService;
    private final UrlStorageService storageService;


    public UrlController(UrlEncoderService encoderService, UrlStorageService storageService) {
        this.encoderService = encoderService;
        this.storageService = storageService;
    }

    @PostMapping("/api/url/shorten")
    public String post(@RequestParam String longUrl){
        String shortUrl = encoderService.encode(longUrl);

        storageService.saveUrl(shortUrl, longUrl);

        return "http://localhost:8090/" + shortUrl;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> get(@PathVariable String shortCode){
        String longUrl = storageService.getLongUrl(shortCode);

        if(longUrl == null){
            return ResponseEntity.notFound().build();
        }
        else{
            // 1. Clean the URL of hidden spaces or Postman quotes
            String cleanUrl = longUrl.trim().replace("\"", "");

            // 2. Ensure it has http:// or https:// (Browser requires this for external redirects)
            if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
                cleanUrl = "https://" + cleanUrl;
            }

            System.out.println("DEBUG: Successfully redirecting to -> " + cleanUrl);

            // 3. Perform the safe redirect
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(cleanUrl)).build();
        }
    }
}
