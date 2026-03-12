package com.Rohan.mini_redis;

public class CacheEntry {
    private Object value;
    private long expirytime;

    public CacheEntry(Object value, long expirytime) {
        this.value = value;
        this.expirytime = expirytime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getExpirytime() {
        return expirytime;
    }

    public void setExpirytime(long expirytime) {
        this.expirytime = expirytime;
    }
}
