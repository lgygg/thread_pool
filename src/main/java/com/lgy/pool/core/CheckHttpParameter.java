package com.lgy.pool.core;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 第一次下载的時候，判断是否支持断点续传，并获取待下载文件的总长度
 */
public class CheckHttpParameter {

	public static CheckResult check(String url){
		CheckResult result = new CheckResult();
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(DownloadConfig.CONNECT_TIME);
			connection.setReadTimeout(DownloadConfig.READ_TIME);
			int responseCode = connection.getResponseCode();
			result.contentLength = connection.getContentLength();
			if(responseCode == HttpURLConnection.HTTP_OK){
				//用来告知客户端服务器是否能处理范围请求，以指定获取服务器某个部分资源。也就是是否支持断点续传，如果返回的是bytes,则说明支持断点续传
				String ranges = connection.getHeaderField("Accept-Ranges");
				if ("bytes".equals(ranges)){
					result.isSupportRange = true;
				}
			}
		}  catch (Exception e) {
		} finally{
			if (connection != null){
				connection.disconnect();
			}
		}
		return result;
	}


	public static class CheckResult{
		public boolean isSupportRange = false;
		public int contentLength = 0;
	}
}
