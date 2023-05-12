package com.lgy.pool.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lgy.pool.core.bean.TaskBean;

import java.sql.SQLException;

/**
 * 
 */
public class OrmDBHelper extends OrmLiteSqliteOpenHelper {
	
	public static final String DB_NAME = "lgy_downloader";
	public static final int DB_VERSION = 1;

	public OrmDBHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}

	public OrmDBHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, TaskBean.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int arg2, int arg3) {
	}

}