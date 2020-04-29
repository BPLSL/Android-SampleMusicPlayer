package com.liushilun.exerciseb04_0837;

//用来存放每一句歌词
public class LrcBean {
    private String lrc=null;
    private long startTime;//以毫秒为单位
    private long endTime;

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
