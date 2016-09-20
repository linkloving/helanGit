package com.VitaBit.VitaBit.utils.sportUtils;

import android.content.Context;

import com.VitaBit.VitaBit.db.sport.UserDeviceRecord;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.example.android.bluetoothlegatt.utils.TimeZoneHelper;
import com.linkloving.band.dto.SportRecord;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo.wang on 2016/4/5.
 */
public class Datahelper {
    private static final String TAG = Datahelper.class.getSimpleName();
    public static final int TYPE_24H = 0X111; //24小时的时间类型
    public static final int TYPE_8H = 0X112;  //8小时的时间类型
    public static final int TYPE_3H = 0X113;  //3小时时间类型

    //数据的各个状态
    public static final int WALKING = 1;
    public static final int RUNNING = 2;
    public final static int SITTING = 112;
    public final static int STANDING = 113;

    private int TIME_BASE;

    ArrayList<SportRecord> sportRecords=new ArrayList<SportRecord>();//按照指定时期从数据库查到的数据
    ArrayList<Integer> list_end_time = new ArrayList<>();//结束时间集合
    ArrayList<Integer> list_start_time=new ArrayList<>();//开始时间集合
    Map<Integer,List<SportRecord>> computedData=new HashMap<>();  //計算完的新數據
    Context context;
    String inputTime;
    int timeType;

    public Datahelper(Context context, String userId, String startDate, String endDate, int type){

        sportRecords=new ArrayList<SportRecord>();//按照指定时期从数据库查到的数据
        list_end_time = new ArrayList<>();//结束时间集合
        list_start_time=new ArrayList<>();//开始时间集合
        computedData=new HashMap<>();  //計算完的新數據

        initTime(startDate,type);
        this.context=context;
        inputTime = startDate;
        timeType = type;
        //查询获取到指定日期时间内的明细数据
        if(type ==TYPE_24H ){
            sportRecords= UserDeviceRecord.findHistoryChartHour(context, userId, startDate, endDate,true);
        }else{
            sportRecords=UserDeviceRecord.findHistoryChartHour(context, userId, startDate, endDate,false);
        }
        MyLog.i(TAG, context + userId + startDate + endDate);
        MyLog.i(TAG, "查询到的日明细数据条数：" + sportRecords.size());
        for(SportRecord sp:sportRecords){
            MyLog.i(TAG, "查询到的日明细数据：" + sp.toString());
        }
    }

    public static float[] getFloatsData(List<SportRecord> sportRecords) {
        float[] barData = new float[3];
        float walk = 0, sit=0, stand=0;
        for (int i = 0;i<sportRecords.size();i++) {
            switch (Integer.parseInt(sportRecords.get(i).getState())) {
                case WALKING:
                    //运动数据里加了一条数据
                    walk = walk + Float.parseFloat(sportRecords.get(i).getDuration());
                    break;
                case RUNNING:
                    walk = walk+ Float.parseFloat(sportRecords.get(i).getDuration());
                    break;
                case SITTING:
                    sit = sit+ Float.parseFloat(sportRecords.get(i).getDuration());
                    break;
                case STANDING:
                    stand = stand+ Float.parseFloat(sportRecords.get(i).getDuration());
                    break;
            }
        }
        barData[0] = stand;
        barData[1] = sit;
        barData[2] = walk;
        return barData;
    }

    public  Map<Integer,List<SportRecord>> getSportRecords(){
            if(sportRecords==null || sportRecords.size()<=0)  {
                return null;
            }
            int first=0;
            //--------------------------判断第一条运动数据属于哪个时间段--------------------------------//
            //数据的start_time 的秒数   （2016-08-16 00:01:00 是60）
            int srsStartTime=getDuration(sportRecords.get(0).getStart_time());
            if(srsStartTime < list_start_time.get(0) && srsStartTime+Integer.parseInt(sportRecords.get(0).getDuration())>list_start_time.get(0)){
                //若第一条数据不在开始时间内 但跨过开始时间
                SportRecord cp = new SportRecord();
                cp.setDevice_id(sportRecords.get(0).getDevice_id());
                cp.setStart_time(addDuration(list_start_time.get(0)));
                cp.setState(sportRecords.get(0).getState());
                //数据的处理
                int duration = Integer.parseInt(sportRecords.get(0).getDuration()) - (list_start_time.get(0) - srsStartTime);
                cp.setDuration(String.valueOf(duration));
                cp.setStep("100");
                cp.setDistance("100");
                MyLog.i(TAG, "第0条的数据分割=" + cp.toString());
                sportRecords.add(0,cp);
            }
                srsStartTime=getDuration(sportRecords.get(0).getStart_time());
                for(int m=0;m<list_end_time.size();m++){
                    MyLog.i(TAG, "第一条数据的开始时间："+ srsStartTime);
                    MyLog.i(TAG, "第"+m+"个时间区间的开始时间:"+list_start_time.get(m));
                    MyLog.i(TAG, "第"+m+"个时间区间的结束时间"+list_end_time.get(m));

                    if(srsStartTime>=list_start_time.get(m) && srsStartTime < list_end_time.get(m)){
                        first = m;
                        break;
                    }
                }
            //--------------------------判断第一条运动数据属于哪个时间段--------------------------------//
            int n  = 0;
            //判断从哪个时间段开始查找
            time:for(int i=first;i<list_end_time.size();i++){
                //取出一个时间段
                data:for(int j=n;j<sportRecords.size();j++){
//                    MyLog.i(TAG,"当前的i=" +i+"当前的j=" +j);
//                    MyLog.i(TAG, "当数据"+sportRecords.get(j).toString());
                    //取出一条数据,来判断当前的时间应该在那条数据内
                    //首先计算,开始时间的是在哪个时间段
                    int srsStartTimeSecond=getDuration(sportRecords.get(j).getStart_time());
                    if(srsStartTimeSecond<list_end_time.get(i) && srsStartTimeSecond>=list_start_time.get(i)){
                            if((srsStartTimeSecond + Integer.parseInt(sportRecords.get(j).getDuration()))<= list_end_time.get(i)){
                                    //还在当前的时间段,直接加上就可以了
                                    computedData.get(i).add(sportRecords.get(j));
                                    MyLog.i(TAG, "时间区间内的数据" +sportRecords.get(j).toString());
                            }else{
                                    //不在这个时间段,要做处理,
                                    MyLog.i(TAG, "这是一条跨时间区间的数据" +sportRecords.get(j).toString());
                                    // list_end_time-stattime,这么多是在这个时间范围内的,
                                    SportRecord cp = new SportRecord();
                                    cp.setDevice_id(sportRecords.get(j).getDevice_id());
//                                  cp.setStart_time(myDayData.get(j).getStart_time());
                                    cp.setStart_time(sportRecords.get(j).getStart_time());
                                    MyLog.i(TAG, "srsStartTimeSecond：" + srsStartTimeSecond);
                                    MyLog.i(TAG, "list_start_time.get(i)：" + list_start_time.get(i));
                                    MyLog.i(TAG, "重置的Start_time：" + cp.getStart_time());
                                    cp.setState(sportRecords.get(j).getState());
                                    //数据的处理
                                    cp.setDuration(String.valueOf(list_end_time.get(i) - srsStartTimeSecond));
                                    MyLog.i(TAG, "重置的Duration：" + cp.getDuration());
//                                  cp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
                                    cp.setStep(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * (list_end_time.get(i) - srsStartTimeSecond)));
//                                  cp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
                                    cp.setDistance(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * (list_end_time.get(i) - srsStartTimeSecond)));
                                    MyLog.i(TAG, "跨时间区间的数据首次分割=" + cp.toString());
                                    computedData.get(i).add(cp);
                                    //除去上面去掉的数据,现在还身下起始时间为下一次开始的时间
                                    //剩下的Duration如果是最后一条,很有可能超过了最后一条的时间区间这时候直接将最后改为一天的最后值
                                    if(i != list_end_time.size()-1){
                                        //数据的处理
                                        //剩下的时间,再去做个半个小时,为一次跨度的循环,判断是不是在下一个,或者跟下一个的循环
                                        i++;//切换到下一个时间区间
                                        //分割了一个子数据出来后
                                        int durationShengxia = Integer.parseInt(sportRecords.get(j).getDuration()) -Integer.parseInt(cp.getDuration());
                                        int cycleSize = durationShengxia / TIME_BASE;   //判断循环几次
                                        int dulast = durationShengxia % TIME_BASE;//最后一次的
                                        temp:for(int cycle=0;cycle<=cycleSize;cycle++){
                                            MyLog.i(TAG, "最后一次的tempi=" +i);
                                            //2016-08-16 08:37:30

                                            if(cycle==cycleSize){
                                                SportRecord temp = new SportRecord();
                                                temp.setDevice_id(sportRecords.get(j).getDevice_id());
                                                temp.setState(sportRecords.get(j).getState());
                                                temp.setStart_time(addDuration(list_start_time.get(i)));   //除去上面去掉的数据,现在还身下起始时间为下一次开始的时间
                                                //最后一次
                                                temp.setDuration(String.valueOf(dulast));
//                                              temp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * dulast));
//                                              temp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * dulast));
                                                temp.setStep(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * dulast));
                                                temp.setDistance(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * dulast));
                                                MyLog.i(TAG, "最后一次的temp=" + temp.toString());
                                                computedData.get(i).add(temp);
                                            }else{
                                                SportRecord newdata = new SportRecord();
                                                newdata.setDevice_id(sportRecords.get(j).getDevice_id());
                                                newdata.setState(sportRecords.get(j).getState());
                                                newdata.setStart_time(addDuration(list_start_time.get(i)));   //除去上面去掉的数据,现在还身下起始时间为下一次开始的时间
                                                newdata.setDuration(String.valueOf(TIME_BASE));
                                                newdata.setStep(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * TIME_BASE));
                                                newdata.setDistance(String.valueOf(Double.parseDouble(sportRecords.get(j).getStep()) / Double.parseDouble(sportRecords.get(j).getDuration()) * TIME_BASE));
//                                              temp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * 1800));
//                                              temp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * 1800));
                                                MyLog.i(TAG, "newdata=" + newdata.toString());
                                                computedData.get(i).add(newdata);
                                                //改变时间
//                                                newdata.setStart_time(add(newdata.getStart_time(),TIME_BASE));   //开始时间加上1800s
                                                i++;//切换时间区间
                                                MyLog.i(TAG, "此时的i值是：" + newdata.toString());
                                                if(i >= list_start_time.size()-1){
                                                    return computedData;
                                                }
                                            }
                                    }
                            }
                            }
                             n=n+1;//下次直接从j+1条开始
                    }else{
                    //跳到下一个时间区域
                     break data;
                    }
        }

     }
        for(int i = 0;i<computedData.size();i++){
            List<SportRecord> sportRecords = computedData.get(i);
            for(SportRecord sp:sportRecords){
                MyLog.e(TAG, "转换完的数据:" +sp.toString());
            }
        }
       return computedData;
    }
    //计算当前的时间的Duration,当天开始时间,Duration是0 ,最后的时间Duration是24*3600
    private int getDuration(String stringDate){
        MyLog.i(TAG, "传来计算的时间:" + stringDate);
        String s=TimeZoneHelper.__getLocalTimeFromUTC0(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, stringDate);
        MyLog.i(TAG, "计算的时间:" + s);
        Date date1=ToolKits.stringToDate(s,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        SimpleDateFormat s1=new SimpleDateFormat("HH");
        SimpleDateFormat s2=new SimpleDateFormat("mm");
        SimpleDateFormat s3=new SimpleDateFormat("ss");
        int duration=Integer.parseInt(s1.format(date1))*3600+Integer.parseInt(s2.format(date1))*60+Integer.parseInt(s3.format(date1));
        MyLog.i(TAG,"duration:"+duration+"       "+stringDate);
        return duration;
    }

    //将起始时间加上s
    private String add(String date,int s){
        Date d = ToolKits.stringToDate(date,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        Date d2 = new Date(d.getTime() + s*1000 );
        return new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(d2);
    }

    //将起始时间加上s
    private String addDuration(int resetDuration){
        MyLog.e(TAG, "当天的时间:" + inputTime);
        String time="";
        if(timeType == TYPE_24H){
            String dateDD = TimeUtils.getstartDateTimeUTC(inputTime,false);
            MyLog.i(TAG, "24H时区转换后的时间:" + dateDD);

            try {
                Date now = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).parse(dateDD);
                Date d2 = new Date(now.getTime() + resetDuration*1000+1);
                time = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(d2);
                MyLog.i(TAG, "计算完的时间:" + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(timeType == TYPE_3H || timeType == TYPE_8H ){
            //2016-08-17 8
            try {
                MyLog.e(TAG, "resetDuration:" + resetDuration);
                Date now = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH).parse(inputTime);
                Date date3H=ToolKits.stringToDate(inputTime,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH);
                SimpleDateFormat s2=new SimpleDateFormat("HH");
                int duration3H=Integer.parseInt(s2.format(date3H))*3600;
                //UI上的开始时间 - 时区偏移值 + 时间区间值
                Date d2 = new Date(now.getTime() - TimeZoneHelper.getTimeZoneOffsetSecond()*1000 + (resetDuration*1000 - duration3H*1000));
                time = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(d2);
                MyLog.i(TAG, "计算完的时间:" + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        return time;
    }


    private void initTime(String timeStr,int type){
        int startTime;
        int endTime;
        switch (type){
            case TYPE_24H:  //一天画分成32（24*60 再除以 45）个时间段

                TIME_BASE = 45*60;
                startTime = 0;
                endTime = 45*60;
                for(int i=0; i<32; i++){
                    list_start_time.add(startTime);
                    list_end_time.add(endTime);
                    startTime=startTime+(45*60);
                    endTime=endTime+(45*60);
                    MyLog.i(TAG,"时间后"+startTime+"   "+endTime+">>>>>>"+list_end_time.get(i)+"start+"+list_start_time.get(i));
                    computedData.put(i, new ArrayList<SportRecord>());
                }
                break;
            case TYPE_8H:  //一天画分成32（8*60 再除以 15）个时间段
                Date date8H=ToolKits.stringToDate(timeStr,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH);
                SimpleDateFormat s1=new SimpleDateFormat("HH");
                MyLog.i(TAG,"传尽时间后："+Integer.parseInt(s1.format(date8H)));
                int duration8H=Integer.parseInt(s1.format(date8H))*3600;
                TIME_BASE = 15*60;
                startTime = duration8H;
                endTime =duration8H+ 15*60;
                for(int i=0; i<32; i++){
                    list_start_time.add(startTime);
                    list_end_time.add(endTime);
                    startTime=startTime+(15*60);
                    endTime=endTime+(15*60);
                    computedData.put(i, new ArrayList<SportRecord>());
                }
                break;

            case TYPE_3H:  //一天画分成36（3*60 再除以 5）个时间段
                MyLog.e(TAG,"传尽时间后："+timeStr);
                Date date3H=ToolKits.stringToDate(timeStr,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH);
                SimpleDateFormat s2=new SimpleDateFormat("HH");
                MyLog.i(TAG,"传尽时间后："+Integer.parseInt(s2.format(date3H)));
                int duration=Integer.parseInt(s2.format(date3H))*3600;
                TIME_BASE = 5*60;
                startTime =duration+ 0;
                endTime = duration + 5*60;
                for(int i=0; i<36; i++){
                    list_start_time.add(startTime);
                    list_end_time.add(endTime);
                    startTime=startTime+(5*60);
                    endTime=endTime+(5*60);
                    MyLog.i(TAG,"时间后"+startTime+"   "+endTime+">>>>>>"+list_end_time.get(i)+"start+"+list_start_time.get(i));
                    computedData.put(i, new ArrayList<SportRecord>());
                }
                break;

        }

    }
    private class TimeInterval{
        private int start_time;
        private int end_time;
    }


    }
