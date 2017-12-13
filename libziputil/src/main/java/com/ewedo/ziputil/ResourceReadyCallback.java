package com.ewedo.ziputil;

import java.io.File;

/**
 * Created by fozei on 17-12-13.
 */

public interface ResourceReadyCallback {
    void onResourceReady(File resource);
}
