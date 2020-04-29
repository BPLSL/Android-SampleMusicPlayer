package com.liushilun.exerciseb04_0837;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LrcTransformer {
    //用来将lrc文件中的每一句歌词转化为LrcBean并添加到List<LrcBean>中
    public static List<LrcBean> transform(Context context,String lrcPath){
        List<LrcBean> list = new LinkedList<>();
        try {
            InputStream inputStream = context.getAssets().open("lrc/"+lrcPath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"GBK");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;

            while ((line = bufferedReader.readLine())!=null){
                //System.out.println(line);
                String[] strings = line.split("\\[|\\:|\\.|\\]");
                //System.out.println("这一行长度为："+strings.length);
                if(line.contains(".")){

                    try{
                        LrcBean lrcBean = new LrcBean();
                        lrcBean.setLrc(strings[4]);
                        int minute = Integer.parseInt(strings[1]);
                        int second = Integer.parseInt(strings[2]);
                        int afterSecond = Integer.parseInt(strings[3]);

                        long startTime = minute*60*1000+second*1000+afterSecond*10;
                        lrcBean.setStartTime(startTime);
                        list.add(lrcBean);
                    }catch (IndexOutOfBoundsException e){
                        //lrcBean.setLrc("节奏。。。。。。");
                    }

//                    System.out.println("歌词为"+lrcBean.getLrc());
//                    System.out.println("开始时间为"+lrcBean.getStartTime());
                }


//                for (String str :strings){
//                    System.out.println(str);
//                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }
}
