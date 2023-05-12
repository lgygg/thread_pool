package com.lgy.pool.core;

import android.os.Environment;

import java.io.File;

/**
 * @des Set download config.
 */
public class DownloadConfig {

	public static final int CONNECT_TIME = 10 * 1000;
	public static final int READ_TIME = 10 * 1000;
	/**
	 * 最大下载任务数
	 */
	public static final int MAX_DOWNLOAD_TASKS = 2;

	/**
	 * 下载一个任务的时候，同时开启MAX_DOWNLOAD_THREADS个线程来下载
	 */
	public static final int MAX_DOWNLOAD_THREADS = 1;
	/**
	 * 文件保存到本地的路径
	 */
    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + File.separator +
    		"lgy-download" + File.separator;
    static {
		File file = new File(DOWNLOAD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	public static long getRefreshInterval(int fileSize){
		if(fileSize <= 1024 * 1024 * 20){
			//<=20M
			return 2 * 1000;
		}else if(fileSize > 1024 * 1024 * 20 && fileSize <= 1024 * 1024 * 100){
			//20M~100M
			return 10 * 1000;
		}else{
			//>100M
			return 20 * 1000;
		}
	}
}
