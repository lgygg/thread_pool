package com.lgy.pool.core.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * 下载任务相关信息bean
 */
@DatabaseTable(tableName = "TaskBean")
public class TaskBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(id = true)
    public String id;//主键
    @DatabaseField
    public String url;//下载地址

    @DatabaseField
    public String name;//下载文件名

    @DatabaseField
    public int currentLength;//当前文件长度

    @DatabaseField
    public int totalLength;//文件总长度
    @DatabaseField
    public volatile int status = State.READY;//下载状态
    @DatabaseField
    public String downloadPath;//保存地址
    @DatabaseField
    public boolean isSupportRange;//是否支持断点续传

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<Integer, Integer> ranges;//将当前任务分为几段同时下载

    @DatabaseField
    public int percent;//下载进度的百分数

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<String, String> other;//其他信息，预留字段

    public void reset() {
        currentLength = 0;
        percent = 0;
        ranges = null;
        File file = new File(downloadPath);
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return name + " is " + status + " with " + currentLength + "/" + totalLength + " " + percent +"%";
    }
}
