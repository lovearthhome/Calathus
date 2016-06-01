package com.wikicivi.net.glideprogress;

/**
 * Created by zhaoliang on 16/4/24.
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
