package com.lgy.pool.core;

import com.lgy.pool.core.bean.TaskBean;

/**
 * @author: Administrator
 * @date: 2023/5/13
 */
public interface ITask extends Runnable{
    void pause();
    void cancel();
    void waiting();
    void start();
    void end();
    void setDownloadListener(DownloadListener downloadListener);
    TaskBean getTaskBean();
}
