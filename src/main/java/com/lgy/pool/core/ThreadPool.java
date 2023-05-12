package com.lgy.pool.core;

import com.lgy.pool.core.bean.State;
import com.lgy.pool.core.bean.TaskBean;
import com.lgy.pool.notify.DataChanger;
import com.lgy.pool.notify.DataWatcher;
import com.lgy.util.LogUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public class ThreadPool<E extends ProgressTask> implements DownloadListener<E> {
    private ExecutorService executorService = null;
    private Map<String,E> runningMap = null;
    private int runningListSize = DownloadConfig.MAX_DOWNLOAD_TASKS;
    private LinkedBlockingDeque<E> waitingList = null;
    private DataChanger dataChanger = null;
    public ThreadPool(){
        this.executorService = Executors.newCachedThreadPool();
        this.runningMap = Collections.synchronizedMap(new LinkedHashMap<String,E>());
        this.waitingList = new LinkedBlockingDeque<E>();
    }

    public void setDataChanger(DataChanger dataChanger) {
        this.dataChanger = dataChanger;
    }

    public void addObserver(DataWatcher dataWatcher){
        if (dataWatcher != null) {
            this.dataChanger.addObserver(dataWatcher);
        }
    }

    public void removeObserver(DataWatcher dataWatcher){
        if (dataWatcher != null) {
            this.dataChanger.deleteObserver(dataWatcher);
        }
    }

    public void execute(E task) throws Exception{
        this.addTaskToWaitingList(task);
        this.addTaskToRunningList(this.waitingList);
    }
    /**
     * @param task 传入需要插队的任务
     * @return 返回被强迫停止的任务
     * @throws Exception
     */
    public TaskBean executeNow(E task) throws Exception {
        task.setDownloadListener(this);
        task.waiting();
        this.waitingList.add(task);
        E item = null;
        while (true){
            if (waitingList.peek() == task) {
                break;
            }
            item = this.waitingList.remove();
            if (item.getTask().id != task.getTask().id) {
                this.waitingList.add(item);
            }
        }

        if (runningMap.size() >= runningListSize) {
           E bean =  runningMap.values().iterator().next();
            if (bean.getTask().status != State.END) {
                this.pause(bean);
                return bean.getTask();
            }
        }
        return null;
    }

    /**
     * @return 正在下载的队列
     */
    public Collection<E> getRunningList(){
        return runningMap.values();
    }

    /**
     * 任务加入等待队列
     * @param task
     * @throws Exception
     */
    public void addTaskToWaitingList(E task) throws Exception{
        if (!this.isTaskExist(task)) {
            task.setDownloadListener(this);
            task.waiting();
            this.waitingList.add(task);
        }
    }

    /**
     * @param list
     * @Desc 当下载队列小于下载限制数量的时候，从等待队列取队头加入下载队列
     */
    public void addTaskToRunningList(LinkedBlockingDeque<E> list){
        if (this.runningMap.size()<this.runningListSize) {
            E task = list.remove();
            if (task != null) {
                task.start();
                LogUtils.eTag("onDownloadStart","this.runningMap.size:"+this.runningMap.size() +"/"+this.runningListSize);
                this.runningMap.put(task.getTask().id,task);
                this.executorService.execute(task);
            }
        }

    }

    private E getTaskByKey(LinkedBlockingDeque<E> list, String key){
        for (E temp:list) {
            if (key.equals(temp.getTask().id)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * @param task
     * @return
     * @throws Exception
     *
     * 判断下载队列和等待队列中是否已经存在该任务
     */
    public boolean isTaskExist(E task) throws Exception {
        if (task == null) {
            throw new Exception("task request no null!");
        }
        if (this.runningMap.containsKey(task.getTask().id) || getTaskByKey(waitingList,task.getTask().id)!=null) {
            return true;
        }
        return false;
    }

    private void pause(E task) {
        task.pause();
        this.runningMap.remove(task.getTask().id);
    }

    public void pause(String id) {
        if (this.runningMap.containsKey(id)) {
            this.pause(this.runningMap.get(id));
        }
    }

    public void cancel(String id) {
        if (this.runningMap.containsKey(id)) {
            this.cancel(this.runningMap.get(id));
        }
    }

    private void cancel(E task) {
        task.cancel();
        this.runningMap.remove(task.getTask().id);
    }

    @Override
    public void onDownloadStart(E task) {
        LogUtils.dTag("onDownloadStart", "name:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }

    @Override
    public void onProgressChanged(int progress, E task) {
        LogUtils.dTag("onProgressChanged", "name:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url+ "\n progress:"+task.getTask().percent);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }

    @Override
    public void onDownloadPaused(E task) {
        this.addTaskToRunningList(this.waitingList);
        LogUtils.dTag("onDownloadPaused", "name:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url+ "\n progress:"+task.getTask().percent);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }

    @Override
    public void onDownloadCanceled(E task) {
        LogUtils.dTag("onDownloadCanceled","name:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url+ "\n progress:"+task.getTask().percent);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }

    @Override
    public void onDownloadCompleted(E task) {
        task.end();
        this.runningMap.remove(task.getTask().id);
        this.addTaskToRunningList(this.waitingList);
        LogUtils.dTag("onDownloadCompleted", "name:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url+ "\n progress:"+task.getTask().percent);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }

    @Override
    public void onDownloadError(E task, String message) {
        task.cancel();
        this.runningMap.remove(task.getTask().id);
        LogUtils.dTag("onDownloadError",
                "errorMsg:"+ message +
                "\nname:" + task.getTask().name + " " +
                "\nurl:" + task.getTask().url+ "\nprogress:"+task.getTask().percent);
        if (dataChanger != null) {
            dataChanger.updateStatus(task.getTask());
        }
    }
}
