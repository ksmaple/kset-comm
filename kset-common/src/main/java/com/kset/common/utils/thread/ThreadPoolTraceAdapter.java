package com.kset.common.utils.thread;


public interface ThreadPoolTraceAdapter {

    
    String getTraceId();

    
    void setTraceId(String traceId);

    
    void clear();
}
