package com.lgy.pool.core;

import com.lgy.pool.core.bean.State;
import com.lgy.pool.core.bean.TaskBean;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public class ProgressTask implements ITask{
    private TaskBean taskBean;
    private DownloadStrategy downloadStrategy;
    private DownloadListener downloadListener;
    public ProgressTask(TaskBean taskBean){
        this(new DefaultDownloadStrategy(),taskBean);
    }

    public ProgressTask(DownloadStrategy downloadStrategy,TaskBean taskBean){
        this.downloadStrategy = downloadStrategy;
        this.taskBean = taskBean;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public TaskBean getTaskBean() {
        return taskBean;
    }

    @Override
    public void run() {
        this.taskBean.status = State.RUNNING;
        this.downloadStrategy.download(this);
    }

    public void pause() {
        this.taskBean.status = State.READY;
        this.downloadStrategy.pause();
    }

    public void cancel() {
        this.taskBean.status = State.READY;
        this.downloadStrategy.cancel();
    }
    public void waiting() {
        this.taskBean.status = State.WAITING;
    }
    public void start() {
        this.taskBean.status = State.START;
    }
    public void end() {
        this.taskBean.status = State.END;
    }

}
