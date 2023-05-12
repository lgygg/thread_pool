package com.lgy.pool.notify;

import com.lgy.pool.core.bean.TaskBean;

import java.util.Observable;
import java.util.Observer;


/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public abstract class DataWatcher implements Observer{

	@Override
	public void update(Observable observable, Object data) {
		if(data instanceof TaskBean){
			onDataChanged((TaskBean) data);
		}
	}
	
	public abstract void onDataChanged(TaskBean downloadEntry);

}
