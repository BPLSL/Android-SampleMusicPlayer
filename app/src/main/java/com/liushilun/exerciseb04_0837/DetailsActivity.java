package com.liushilun.exerciseb04_0837;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView songNameTextView;
    private ImageButton equalizerImageButton;
    private MyLyricView myLyricView;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private SeekBar seekBar;
    private ImageView playImageView;
    private ImageView previous_song;
    private ImageView next_song;


    private int position = -1;
    private List<LrcBean> lrcBeanList;
    private int playStatus = 0;
    private int currentTime;
    private int totalTime;
    private ServiceConnection connection;
    private  MediaPlayerService.MyBinder binder;
    private MyBroadcastReceive myBroadcastReceive;
    private boolean isSeekBarChanging =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        toolbar = findViewById(R.id.detailsActivityToolBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        this.obtainAllAssembly();
        addEqualizerImageButtonListener();

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (MediaPlayerService.MyBinder)service;

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(DetailsActivity.this,MediaPlayerService.class);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);

        this.addPlayImageViewClickListener();
        this.addPrevious_songListener();
        this.addNext_songListener();
        addSeekBarChangeListener();
        this.addRegisterReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        //unregisterReceiver(myBroadcastReceive);

    }

    //从activity_main.xml获取所有组件
    private void obtainAllAssembly(){
        songNameTextView = findViewById(R.id.daSongNameTextView);
        equalizerImageButton = findViewById(R.id.equalizerImageButton);
        playImageView = findViewById(R.id.play);
        previous_song = findViewById(R.id.previous_song);
        next_song = findViewById(R.id.next_song);
        currentTimeTextView = findViewById(R.id.currentTime);
        totalTimeTextView = findViewById(R.id.totalTime);
        seekBar = findViewById(R.id.seekBar);
        myLyricView = findViewById(R.id.myLyricView);


    }

    private void addEqualizerImageButtonListener(){
        equalizerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,EqualizerActivity.class);
                startActivity(intent);
            }
        });
    }


    //将毫秒转化为mm：ss的形式
    private String timeFormat(int seconds){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:ss");
        String formalTime= simpleDateFormat.format(new Date(seconds));
        return formalTime;
    }


    private void addPlayImageViewClickListener(){
        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playStatus==0){
                    binder.doPlayMusic(0);;
                }else if(playStatus==1){
                    binder.pausePlay();
                }else {
                    binder.continuePlay();
                }

            }
        });
    }

    //给下一首上一首按钮设置监听
    private void addPrevious_songListener(){
        previous_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int n=binder.getPosition();
//                if(n==0){
//                    n=MainActivity.songList2.length-1;
//                }else{
//                    n-=1;
//                }
//                binder.resetSourcePath(n);
//                binder.setPosition(n);
                binder.playPreviousSong();
            }
        });
    }
    private void addNext_songListener(){
        next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int n=binder.getPosition();
//                int length = MainActivity.songList2.length;
//                if(n==length-1){
//                    n=0;
//                }else{
//                    n+=1;
//                }
//                binder.resetSourcePath(n);
//                binder.setPosition(n);
                binder.playNextSong();
            }
        });
    }

    private void addRegisterReceiver(){
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.liushilun.BROADCAST");
        registerReceiver(myBroadcastReceive,intentFilter);
    }

    private void addSeekBarChangeListener(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging=true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binder.seekTo(seekBar.getProgress()*300);
                isSeekBarChanging=false;
            }
        });
    }


    private class MyBroadcastReceive extends BroadcastReceiver{
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    String lrcPath = MainActivity.songList.get(position).getLrcPath();
                    if (lrcPath!=null){
                        lrcBeanList = LrcTransformer.transform(DetailsActivity.this,lrcPath);
                        myLyricView.setList(lrcBeanList);
                    }

                }catch (NullPointerException e){
                    System.out.println("歌词不存在");
                }

            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //如果歌曲没有换就不对lrcBeanList进行更新
            if (position!=bundle.getInt("position",0)){
                position = bundle.getInt("position");
//                totalTime = bundle.getInt("totalTime");
//                String str = MainActivity.songList2[position];
//                String[] string = str.split("-");
//                String songName = string[1].split(".mp3")[0];

                Song song = MainActivity.songList.get(position);
                totalTime = song.getDuration();
                totalTimeTextView.setText(timeFormat(song.getDuration()));
                songNameTextView.setText(song.getName());
                seekBar.setMax(totalTime/300);
                Thread thread = new Thread(runnable);
                thread.start();
            }
            playStatus = bundle.getInt("playStatus");
            if(playStatus==0||playStatus==2){
                playImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            }else {
                playImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));

            }

            currentTime = bundle.getInt("currentTime");
            currentTimeTextView.setText(timeFormat(currentTime));
            if (!isSeekBarChanging){
                seekBar.setProgress(currentTime/300);
            }
            myLyricView.setCurrentPosition(currentTime);

        }
    }






}
