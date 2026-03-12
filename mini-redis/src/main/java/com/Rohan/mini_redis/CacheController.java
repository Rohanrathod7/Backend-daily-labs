package com.Rohan.mini_redis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/{key}")
    public ResponseEntity<String> postcache(
        @PathVariable String key,
        @RequestParam String value,
        @RequestParam long time
    ){

        cacheService.put(key, value, time);

        return ResponseEntity.ok("Successfully cached data for key: \" + key");

    }

    @GetMapping("/{key}")
    public ResponseEntity<Object> getCache(@PathVariable String key){
        // Store the result in a variable so we only call get() once
        Object data = cacheService.get(key);

        if(data == null){
            return ResponseEntity.notFound().build(); // Added .build()
        }
        else{
            return ResponseEntity.ok(data);
        }
    }
}
