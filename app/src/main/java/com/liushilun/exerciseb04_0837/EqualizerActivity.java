package com.liushilun.exerciseb04_0837;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class EqualizerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MediaPlayerService.MyBinder myBinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        toolbar = findViewById(R.id.equalizerActivityToolBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (MediaPlayerService.MyBinder) service;
                final Equalizer equalizer = new Equalizer(0,myBinder.getAudioSessionId());
                equalizer.setEnabled(true);
                LinearLayout equalizerLinearLayout = findViewById(R.id.equalizerLinearLayout);
                final short minEQLevel = equalizer.getBandLevelRange()[0];
                short maxEQLevel = equalizer.getBandLevelRange()[1];
                short brands = equalizer.getNumberOfBands();
                for(short i = 0; i < brands; i++){
                    TextView eqTextView = new TextView(EqualizerActivity.this);
                    eqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    eqTextView.setText((equalizer.getCenterFreq(i)/1000)+"Hz");
                    equalizerLinearLayout.addView(eqTextView);
                    LinearLayout tmpLayout = new LinearLayout(EqualizerActivity.this);
                    tmpLayout.setOrientation(LinearLayout.HORIZONTAL);

                    TextView minDbTextView  = new TextView(EqualizerActivity.this);
                    minDbTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    minDbTextView.setText((minEQLevel/100)+"db");

                    TextView maxDbTextView  = new TextView(EqualizerActivity.this);
                    maxDbTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    maxDbTextView.setText((maxEQLevel/100)+"db");

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.weight = 1;

                    SeekBar bar = new SeekBar(EqualizerActivity.this);
                    bar.setLayoutParams(layoutParams);
                    bar.setMax(maxEQLevel-minEQLevel);
                    bar.setProgress(equalizer.getBandLevel(i));
                    final short brand = i;
                    bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            equalizer.setBandLevel(brand,(short)(progress+minEQLevel));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    tmpLayout.addView(minDbTextView);
                    tmpLayout.addView(bar);
                    tmpLayout.addView(maxDbTextView);
                    equalizerLinearLayout.addView(tmpLayout);

                }


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(EqualizerActivity.this,MediaPlayerService.class);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);


    }
}
