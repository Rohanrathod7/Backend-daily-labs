package com.Rohan.mini_redis;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    private final ConcurrentHashMap<String, CacheEntry> cachemap = new ConcurrentHashMap<>();

    public void put(String key, Object value, long time){

        long updatedtime = System.currentTimeMillis() + time;

        CacheEntry entry = new CacheEntry(value, updatedtime);

        cachemap.put(key, entry);
        System.out.println("Cache Saved ");
    }

    public Object get(String key){
        CacheEntry cacheEntry = cachemap.get(key);

        if(cacheEntry != null){
            long time = cacheEntry.getExpirytime();
            if(System.currentTimeMillis() < time ){
                System.out.println("catch is still available - present");
                return cacheEntry.getValue();

            }
            else{
                cachemap.remove(key);
                System.out.println("catch passed limit - deleted");
                return null;

            }
        }
        else{

            System.out.println("catch is garbage collected - not Available anymore");
            return null;

        }

    }

    @Scheduled(fixedRate = 10000)
    public void cleanupCache(){
        long currtime = System.currentTimeMillis();

        cachemap.forEach((key, object)->{
            long time = object.getExpirytime();
            if(time < currtime){
                cachemap.remove(key, object);
                System.out.println("Catch_deleted - Garbage Collector delete");
            }
        });
    }
}
