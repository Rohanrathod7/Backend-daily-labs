package com.Rohan.RateLimiter;

public class RequestInfo {

    int requestTime;
    long windowsStartTime;

    public RequestInfo(int req, long wind){
        this.requestTime = req;
        this.windowsStartTime = wind;
    }

    public int getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(int requestTime) {
        this.requestTime = requestTime;
    }

    public long getWindowsStartTime() {
        return windowsStartTime;
    }

    public void setWindowsStartTime(long windowsStartTime) {
        this.windowsStartTime = windowsStartTime;
    }
}
