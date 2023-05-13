package com.lgy.pool.core;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public interface DownloadStrategy<E extends ITask> {

   void download(E task);
   boolean isPaused();
   boolean isCanceled();
   void pause();
   void cancel();
}
