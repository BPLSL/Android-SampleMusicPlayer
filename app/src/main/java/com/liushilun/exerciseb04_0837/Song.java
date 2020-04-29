package com.liushilun.exerciseb04_0837;

import androidx.annotation.NonNull;

public class Song {
    private String name;
    private String artist;
    private int duration;
    private String displayName;
    private String lrcPath;
    private String path;

    public Song() {
        this.name = null;
        this.artist = null;
        this.duration = -1;
        this.displayName = null;
        this.lrcPath = null;
        this.path = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        return "歌名："+name+"\n歌手名："+artist+"\n时长："+duration+"\n歌曲路径："+ displayName +"\n歌词路径："+lrcPath+"绝对路径："+path;
    }
}
