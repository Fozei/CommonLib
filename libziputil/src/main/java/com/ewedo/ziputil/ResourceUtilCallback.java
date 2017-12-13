package com.ewedo.ziputil;

/**
 * Created by fozei on 17-12-13.
 */

public interface ResourceUtilCallback {
    void onResourceReady(String... path2);

    void onError(Exception e);
}
