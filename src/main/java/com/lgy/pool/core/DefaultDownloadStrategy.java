package com.lgy.pool.core;

import com.lgy.pool.core.bean.TaskBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: Administrator
 * @date: 2023/5/10
 */
public class DefaultDownloadStrategy extends AbsDownloadStrategy<ProgressTask> {
    private long lastStamp = 0;
    @Override
    public void download(ProgressTask task) {
        DownloadListener listener = task.getDownloadListener();
        listener.onDownloadStart(task);
        TaskBean taskBean = task.getTask();
        String url = taskBean.url;

        CheckHttpParameter.CheckResult result = CheckHttpParameter.check(url);
        taskBean.isSupportRange = result.isSupportRange;
        taskBean.totalLength = result.contentLength;

        int startPos = taskBean.currentLength > 0 ? taskBean.currentLength : 0;
        int endPos = taskBean.totalLength > 0 ? taskBean.totalLength : 0;
        String path = taskBean.downloadPath+"/"+ taskBean.name;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            if (endPos > 0) {
                connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            }
            connection.setConnectTimeout(DownloadConfig.CONNECT_TIME);
            connection.setReadTimeout(DownloadConfig.READ_TIME);

            int responseCode = connection.getResponseCode();
            int contentLength = connection.getContentLength();
            if (taskBean.totalLength <= 0) {
                taskBean.totalLength = contentLength;
            }

            File file = new File(path);
            RandomAccessFile raf = null;
            FileOutputStream fos = null;
            InputStream is = null;

            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                //支持断点下载
                byte[] buffer = new byte[2048];
                int len = -1;

                if (DownloadConfig.MAX_DOWNLOAD_THREADS > 1) {
                    //子线程数>1时，使用RandomAccessFile
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(startPos);
                    is = connection.getInputStream();

                    while ((len = is.read(buffer)) != -1) {
                        if (isPaused() || isCanceled()) {
                            break;
                        }
                        raf.write(buffer, 0, len);
                        taskBean.currentLength += len;

                        long stamp = System.currentTimeMillis();
                        int percent = 0;
                        if(stamp - lastStamp  > 1000){
                            lastStamp = stamp;
                            percent = (int)(taskBean.currentLength * 100l / taskBean.totalLength);
                            taskBean.percent = percent;
                            listener.onProgressChanged(percent,task);
                        }

                    }

                    raf.close();
                    is.close();
                } else {
                    //子线程数为1时，使用FileOutputStream提高速度
                    BufferedInputStream bis = null;
                    BufferedOutputStream bos = null;

                    if (!file.exists()) {
                        File dir = file.getParentFile();
                        if (dir.exists() || dir.mkdirs()) {
                            file.createNewFile();
                        }
                    }
                    fos = new FileOutputStream(path, true);
                    bis = new BufferedInputStream(connection.getInputStream());
                    bos = new BufferedOutputStream(fos);

                    while ((len = bis.read(buffer)) != -1) {
                        if (isPaused() || isCanceled()) {
                            break;
                        }
                        bos.write(buffer, 0, len);
                        taskBean.currentLength += len;

                        long stamp = System.currentTimeMillis();
                        int percent = 0;
                        if(stamp - lastStamp  > 1000){
                            lastStamp = stamp;
                            percent = (int)(taskBean.currentLength * 100l / taskBean.totalLength);
                            taskBean.percent = percent;
                            listener.onProgressChanged(percent,task);
                        }
                    }
                    bos.flush();
                    bis.close();
                    bos.close();
                }

            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                //不支持断点下载
                fos = new FileOutputStream(file);
                is = connection.getInputStream();
                byte[] buffer = new byte[2048];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    if (isPaused() || isCanceled()) {
                        break;
                    }
                    fos.write(buffer, 0, len);

                    taskBean.currentLength += len;

                    long stamp = System.currentTimeMillis();
                    int percent = 0;
                    if(stamp - lastStamp  > 1000){
                        lastStamp = stamp;
                        percent = (int)(taskBean.currentLength * 100l / taskBean.totalLength);
                        taskBean.percent = percent;
                        listener.onProgressChanged(percent,task);
                    }

                }

                fos.close();
                is.close();
            } else {
                listener.onDownloadError(task, "server error:" + responseCode);
                return;
            }

            if (isPaused()) {
                listener.onDownloadPaused(task);
            } else if (isCanceled()) {
                listener.onDownloadCanceled(task);
            }  else {
                task.getTask().percent = 100;
                listener.onDownloadCompleted(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isPaused()) {
                listener.onDownloadPaused(task);
            } else if (isCanceled()) {
                listener.onDownloadCanceled(task);
            } else {
                listener.onDownloadError(task, e.getMessage());
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
