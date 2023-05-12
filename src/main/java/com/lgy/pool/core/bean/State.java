package com.lgy.pool.core.bean;

/**
 *
 * @author: Administrator
 * @date: 2023/5/10
 * @Description：任务运行状态
 */
public interface State {
    /**
     * 准备状态,等待进入等待队列
     */
    int READY = -1;
    /**
     * 等待进入运行池
     */
    int WAITING = 0;
    /**
     * 启动状态
     */
    int START = 1;
    /**
     * 进入运行池
     */
    int RUNNING = 2;
    /**
     * 下载结束的状态(下载完成状态)
     */
    int END = 4;
}
