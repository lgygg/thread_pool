package com.lgy.pool.core;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public abstract class AbsDownloadStrategy<E extends ProgressTask> implements DownloadStrategy {
    private volatile boolean isPaused;
    private volatile boolean isCanceled;
    public void pause() {
        isPaused = true;
        Thread.currentThread().interrupt();
    }

    public void cancel() {
        isCanceled = true;
        Thread.currentThread().interrupt();
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isPaused() {
        return isPaused;
    }

}
