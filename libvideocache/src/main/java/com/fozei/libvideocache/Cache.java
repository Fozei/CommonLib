package com.fozei.libvideocache;

/**
 * Cache for proxy.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface Cache {

    /**
     * @return 当前已经缓存的数据大小
     * @throws ProxyCacheException e
     */
    long available() throws ProxyCacheException;

    int read(byte[] buffer, long offset, int length) throws ProxyCacheException;

    void append(byte[] data, int length) throws ProxyCacheException;

    void close() throws ProxyCacheException;

    void complete() throws ProxyCacheException;

    boolean isCompleted();
}
