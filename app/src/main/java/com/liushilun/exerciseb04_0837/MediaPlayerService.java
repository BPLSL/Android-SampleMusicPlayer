package com.liushilun.exerciseb04_0837;

import android.app.Service;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MediaPlayerService extends Service {
    private int playStatus = 0;//0代表未启动，1代表播放中，2代表暂停中
    private int position=0;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MyBinder myBinder = new MyBinder();
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            notifyAllActivity();
            handler.postDelayed(this,100);
        }
    };

    public class MyBinder extends Binder {


        public void doPlayMusic(int position){
               playMusic(position);

        }
        public int getPosition() {
            return position;
        }
        public void setPosition(int num){
            position = num;
        }
        public void seekTo(int currentPosition){
            mediaPlayer.seekTo(currentPosition,MediaPlayer.SEEK_CLOSEST);
        }

        public void pausePlay(){
            if(playStatus==1){
                mediaPlayer.pause();
                playStatus=2;
            }
        }
        public void continuePlay(){
            if (playStatus==2){
                mediaPlayer.start();
                playStatus=1;
            }
        }
        public void playNextSong(){
            if(position == MainActivity.songList.size() - 1){
                position=0;
                resetSourcePath(position);
            }else {
                resetSourcePath(position+1);
            }

        }
        public void playPreviousSong(){
            if(position == 0){
                resetSourcePath(MainActivity.songList.size()-1);
            }else {
                resetSourcePath(position-1);
            }
        }
        public void resetSourcePath(int position){
            mediaPlayer.reset();
            playMusic(position);
        }
        public int getAudioSessionId(){
            return mediaPlayer.getAudioSessionId();
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        if(playStatus==0){
//            String fileName = intent.getStringExtra("fileName");
//            position = intent.getIntExtra("position",0);
//            playMusic(position);
//            System.out.println("开始播放——————————————————————");
//        }
        System.out.println("绑定人数+1！！！！！！！");
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        //Log.v("tag","启动成功");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("tag","你好unbindddddddddddd123123");
        return super.onUnbind(intent);



    }

    private void notifyAllActivity(){
        Intent updateIntent = new Intent("com.liushilun.BROADCAST");
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        bundle.putInt("playStatus",playStatus);
        bundle.putInt("currentTime",mediaPlayer.getCurrentPosition());
        updateIntent.putExtras(bundle);
        sendBroadcast(updateIntent);
    }

    private void playMusic(int position){
        Song song = MainActivity.songList.get(position);
        String displayName = song.getDisplayName();
        try {
            if (song.getPath()==null){
                AssetFileDescriptor aFD = getAssets().openFd("mp3/"+displayName);
                mediaPlayer.setDataSource(aFD.getFileDescriptor(),aFD.getStartOffset(),aFD.getLength());
            }else {
                mediaPlayer.setDataSource(song.getPath());
            }
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        song.setDuration(mediaPlayer.getDuration());
        mediaPlayer.setLooping(true);
        playStatus = 1;
        this.position = position;
        handler.postDelayed(runnable,0);
    }
}





