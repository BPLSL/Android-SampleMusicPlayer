package com.liushilun.exerciseb04_0837;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int playStatus = 0;
    public static List<Song> songList = new ArrayList<>();
    public static String[] songList2;
    public static String[] lrcList;
    private RecyclerView recyclerView;
    private ServiceConnection connection;
    public static MediaPlayerService.MyBinder binder;
    private SongAdapter songAdapter;
    private TextView mainSongNameTextView;
    private ImageView mainControlImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();       //隐藏ActionBar
        //连接MediaPlayerService



        //获取歌曲列表
        AssetManager assetManager = getAssets();
        try {
                    songList2 = assetManager.list("mp3");
                    lrcList = assetManager.list("lrc");
                    for (String str:songList2){
                        //从文件名中提取歌曲名和歌手名
                        String[] string1 = str.split(".mp3");
                        String[] filename = string1[0].split("-");
                        String songName = filename[1].trim();
                        String singerName = filename[0].trim();

                        Song song = new Song();
                        song.setName(songName);
                        song.setArtist(singerName);
                        song.setDisplayName(str);
                        //去lrc文件夹下找对应的歌词文件
                        for (String lrcStr:lrcList){
                            String[] string2 = lrcStr.split(".lrc");
                            if( string1[0].equals(string2[0]) ){
                                song.setLrcPath(lrcStr);
                            }

                        }
                        songList.add(song);

                    }

        } catch (IOException e) {
                    e.printStackTrace();
        }


        //设置监听器获取binder
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (MediaPlayerService.MyBinder)service;
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
                songAdapter = new SongAdapter(MainActivity.this, songList,binder);
                recyclerView.setAdapter(songAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        //绑定MediaPlayerService
        Intent intent = new Intent(MainActivity.this,MediaPlayerService.class);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);

        Button button1= findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                startActivity(intent);
            }
        });


        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Cursor cursor = MainActivity.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            null,null,null,null);
                    if (cursor==null){
                        Toast.makeText(MainActivity.this,"没有找到本地音乐",Toast.LENGTH_SHORT).show();

                    }else {
                        while(cursor.moveToNext()){
                            Song song = new Song();
                            song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                            song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                            song.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                            song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                            song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                            songList.add(song);
                            songAdapter.updateSongList(songList);
                            //System.out.println(song.toString());

                        }
                    }

                }else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0x123);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==0x123&&grantResults[0]== PackageManager.PERMISSION_GRANTED){


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);

    }

}
