package com.liushilun.exerciseb04_0837;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class MyLyricView extends View {
    private List<LrcBean> list = new LinkedList<>();
    private Paint highlightPaint;
    private Paint plainPaint;
    private int width = 0,height = 0;
    private int currentPosition = 0;
    private int listNum;
    private int highlightColor;
    private int plainColor;
    private int mode = 0;

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setList(List<LrcBean> list) {
        this.list = list;
    }

    public MyLyricView(Context context) {
        super(context);
    }

    public MyLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        highlightPaint = new Paint();
        highlightPaint.setAntiAlias(true);
        highlightPaint.setColor(getResources().getColor(R.color.green));
        highlightPaint.setTextSize(36);
        highlightPaint.setTextAlign(Paint.Align.CENTER);

        plainPaint = new Paint();
        plainPaint.setAntiAlias(true);
        plainPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        plainPaint.setTextSize(36);
        plainPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (width==0||height==0){
            width=getMeasuredWidth();
            height = getMeasuredHeight();
        }



        if (list == null||list.size()==0){
            canvas.drawText("没有搜索到歌词",width/2,height/2,highlightPaint);
        }else {
            //找到当前时间在歌词list中的对应索引
            findCurrentPositionIndex();

            for (int i = 0; i < list.size(); i++) {
                if (i == listNum) {
                    canvas.drawText(list.get(i).getLrc(), width / 2, height / 2 + 80 * i, highlightPaint);
                } else {
                    canvas.drawText(list.get(i).getLrc(), width / 2, height / 2 + 80 * i, plainPaint);
                }

            }
            setScrollY(listNum*80);
        }

        postInvalidateDelayed(100);


    }

    private void findCurrentPositionIndex() {
        //System.out.println(currentPosition+"111111111"+"");
        if (currentPosition < list.get(0).getStartTime()) {
            currentPosition = 0;
            return;
        }
        if (currentPosition>list.get(list.size()-1).getStartTime()){
            listNum = list.size()-1;
            return;
        }
        for (int i=0 ; i<list.size()-1 ; i++){
            LrcBean lrcBean = list.get(i);
            String lrc = lrcBean.getLrc();
            if (currentPosition >= lrcBean.getStartTime() && currentPosition <list.get(i+1).getStartTime()){
                listNum = i;
            }
        }
    }


}
