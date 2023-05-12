package com.lgy.pool;

import android.content.Context;

import com.lgy.pool.core.ProgressTask;
import com.lgy.pool.core.ThreadPool;
import com.lgy.pool.core.bean.TaskBean;
import com.lgy.pool.notify.DataChanger;
import com.lgy.pool.notify.DataWatcher;

import java.util.List;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public class DownloadManager {
    private ThreadPool<ProgressTask> pool = null;
    private DataChanger dataChanger = null;
    private DownloadManager(){
        pool = new ThreadPool();
    }
    private final static class InnerDownLoad{
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance(){
        return InnerDownLoad.INSTANCE;
    }

    public void setContext(Context context) {
        if (context != null) {
            try {
                dataChanger = new DataChanger.Builder().init(context).build();
                pool.setDataChanger(dataChanger);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addObserver(DataWatcher watcher){
        pool.addObserver(watcher);
    }

    public void removeObserver(DataWatcher dataWatcher){
        pool.removeObserver(dataWatcher);
    }

    public void execute(TaskBean taskBean) throws Exception{
        ProgressTask task = new ProgressTask(taskBean);
        pool.execute(task);
    }

    /**
     * @param taskBean 传入需要插队的任务
     * @return 返回被强迫停止的任务
     * @throws Exception
     */
    public TaskBean executeNow(TaskBean taskBean) throws Exception{
        ProgressTask task = new ProgressTask(taskBean);
        return pool.executeNow(task);
    }

    public void pause(String id) {
        pool.pause(id);
    }

    public void cancel(String id) {
        pool.cancel(id);
    }

    public List<TaskBean> queryAll(){
        return dataChanger.queryAll();
    }

}
