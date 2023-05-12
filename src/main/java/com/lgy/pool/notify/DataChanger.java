package com.lgy.pool.notify;

import android.content.Context;

import com.lgy.pool.core.bean.TaskBean;
import com.lgy.pool.db.DBController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;

/**
 * @des Observable which notifies all the observers.
 */
public class DataChanger extends Observable{
	
	private LinkedHashMap<String, TaskBean> mOperateEntries;
	
	private DataChanger(){
		mOperateEntries = new LinkedHashMap<String, TaskBean>();
	}

	public static class Builder{
		private Context context;

		private void checkInit() throws Exception {
			if (context == null) {
				throw new Exception("context is not init!");
			}
		}
		public Builder init(Context context){
			this.context = context;
			DBController.getInstance().init(context);
			return this;
		}

		public DataChanger build() throws Exception {
			checkInit();
			return new DataChanger();
		}
	}

	
	public void updateStatus(TaskBean entry) {
		mOperateEntries.put(entry.url, entry);
        DBController.getInstance().newOrUpdate(entry);
		setChanged();
		notifyObservers(entry);
	}
	
	public List<TaskBean> queryAll() {
        return  DBController.getInstance().queryAll();
    }

	public TaskBean queryEntryByUrl(String url) {
		return DBController.getInstance().queryByUrl(url);
	}
}
