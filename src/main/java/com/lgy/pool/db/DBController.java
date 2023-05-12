package com.lgy.pool.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lgy.pool.core.bean.TaskBean;
import com.lgy.util.LogUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @des DBController
 */
public class DBController {
    private static DBController mInstance;
    private OrmDBHelper mDBHelper;

    private DBController() {
    }

    public static DBController getInstance() {
        if (mInstance == null) {
            mInstance = new DBController();
        }
        return mInstance;
    }

    public void init(Context context){
        mDBHelper = new OrmDBHelper(context);
        mDBHelper.getWritableDatabase();
    }

    public synchronized void newOrUpdate(TaskBean entry) {
        try {
            Dao<TaskBean, String> dao = mDBHelper.getDao(TaskBean.class);
            dao.createOrUpdate(entry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<TaskBean> queryAll() {
        Dao<TaskBean, String> dao;
        try {
            dao = mDBHelper.getDao(TaskBean.class);
            return (ArrayList<TaskBean>) dao.query(dao.queryBuilder().prepare());
        } catch (SQLException e) {
            LogUtils.e(e.getMessage());
            return null;
        }
    }

    public synchronized TaskBean queryByUrl(String url) {
        try {
            Dao<TaskBean, String> dao = mDBHelper.getDao(TaskBean.class);
            return dao.queryForId(url);
        } catch (SQLException e) {
            LogUtils.e(e.getMessage());
            return null;
        }
    }
    
    public synchronized void deleteByUrl(String url){
    	 try {
			Dao<TaskBean, String> dao = mDBHelper.getDao(TaskBean.class);
			dao.deleteById(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}
