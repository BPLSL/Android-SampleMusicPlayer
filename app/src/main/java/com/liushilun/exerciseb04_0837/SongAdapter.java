package com.liushilun.exerciseb04_0837;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private Context context;
    private List<Song> songList;
    private MediaPlayerService.MyBinder binder;
    public SongAdapter(Context context,  List<Song> songList, MediaPlayerService.MyBinder binder){
        this.songList=songList;
        this.context = context;
        this.binder=binder;
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MyViewHolder holder, final int position) {
//        String str = songList[position];
//        String[] string = str.split("-");
//        String songName = string[1].split(".mp3")[0];
//        String singerName = string[0];
//        holder.songNameTextView.setText(songName.trim());
//        holder.singerNameTextView.setText(singerName);
        Song song = songList.get(position);
        holder.songNameTextView.setText(song.getName());
        holder.singerNameTextView.setText(song.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.playStatus==0){
                    MainActivity.playStatus = 1;
                    binder.doPlayMusic(position);
                }else{
                    MainActivity.binder.resetSourcePath(position);
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
    public void updateSongList(List<Song> songList){
        this.songList=songList;
        notifyDataSetChanged();

        //notifyAll();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView songNameTextView;
        TextView singerNameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.singerNameTextView=itemView.findViewById(R.id.singerNameTextView);
            this.songNameTextView = itemView.findViewById(R.id.songNameTextView);
        }
    }

}
