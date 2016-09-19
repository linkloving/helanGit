package com.linkloving.helan.logic.UI.main.datachatactivity.ViewUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.helan.logic.UI.main.datachatactivity.ChartParameter;
import com.linkloving.helan.utils.ToolKits;
import com.linkloving.helan.utils.logUtils.MyLog;
import com.linkloving.helan.utils.sportUtils.TimeUtils;
import com.linkloving.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class CreateDetailBitmap {
    private final String TAG = CreateDetailBitmap.class.getSimpleName();

    public final static int WALK_TYPE = 1;
    public final static int SIT_TYPE = 112;
    public final static int STAND_TYPE = 113;

    private ChartParameter chartParameter;
    private Date chartDate;

    String time;

    public CreateDetailBitmap(Context context){
    }

    public void initChartParameter(ChartParameter chartParameter){
        this.chartParameter = chartParameter;
    }

    public Bitmap drawLineChar(float xScale)
    {
        Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(chartBitmap);
        Paint paint = new Paint();
        paint.setColor(GetLineColor());
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(xScale, chartParameter.getChartHeight()-30); //583
        MyLog.e(TAG,"chartParameter.getChartHeight()-30"+(chartParameter.getChartHeight()-30));
        path.lineTo(xScale,0);
        //再往右移动
        path.lineTo(xScale+5,0);
        //再往下移动
        path.lineTo(xScale+5,chartParameter.getChartHeight()-30);
        //回归原点画成封闭矩形
        path.moveTo(xScale, chartParameter.getChartHeight()-30);
        path.close();
        canvas.drawPath(path, paint);
        //在上面所画出来的横线下面加上坐标轴
        return chartBitmap;
    }

    public Bitmap drawDetailChartSit(List<SportRecord> sportRecords, Date dayindexNow)
    {
        Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(chartBitmap);
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.rgb(255, 255, 255));
        canvas.drawLine(0, chartParameter.getChartHeight() - 30, chartParameter.getWidth(), chartParameter.getHeight() - 30, rectPaint);
        //在上面所画出来的横线下面加上坐标轴
        rectPaint.setStyle(Paint.Style.FILL);
//        drawStandChart(sportRecords, canvas,dayindexNow);
        drawSitChart(sportRecords, canvas,dayindexNow);
        drawX(rectPaint, canvas);
        return chartBitmap;
    }


    public Bitmap drawDetailChartStand(List<SportRecord> sportRecords, Date dayindexNow,int sportType)
    {
        Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(chartBitmap);
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.rgb(255, 255, 255));
        canvas.drawLine(0, chartParameter.getChartHeight() - 30, chartParameter.getWidth(), chartParameter.getHeight() - 30, rectPaint);
        //在上面所画出来的横线下面加上坐标轴
        rectPaint.setStyle(Paint.Style.FILL);
        drawStandChart(sportRecords, canvas,dayindexNow,sportType);
//        drawSitChart(sportRecords, canvas,dayindexNow);
        drawX(rectPaint, canvas);
        return chartBitmap;
    }

    private void drawSitChart(List<SportRecord> sportRecords,Canvas canvas,Date date){
        chartDate = date; //图表的日期
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.rgb(0x0f, 0xb4, 0xc9));	//#0FB4C9
        rectPaint.setStyle(Paint.Style.FILL);
        for(int i = 0; i< sportRecords.size(); i ++){
            SportRecord data = sportRecords.get(i);
            Paint paint = new Paint();
            paint.setColor(GetSleepColor());
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            //此时是坐着的
            MyLog.i(TAG,"转换后的数据"+data.toString());
            if(Integer.parseInt(data.getState()) == BRDetailData.STATE_SIT ){
                //根据本条数据属于当地时间日期 获取当天的开始UTC时间 方便找到0的坐标点
                String timeChart = TimeUtils.getstartDateTimeUTC(data.getLocalDate(),false);
                //获取今天开始时间的long值
                long timeLong = TimeUtil.stringToLong(timeChart, "yyyy-MM-dd HH:mm:ss");
                //运动数据开始时间相当于今天0点的偏移值 计算出x坐标
                int chazhi = (int)(Long.parseLong(data.getStart_time_utc())-timeLong);
                //运动的持续时间
                int durtaion = Integer.parseInt(data.getDuration());
                //画一个矩形
                //找到一个点
                path.moveTo(getBeginXScale(chazhi), chartParameter.getChartHeight()-30);
                //往下移动到点的下方GetSleepY()的距离
                path.lineTo(getBeginXScale(chazhi),GetSleepY());
                //再往右移动
                path.lineTo(getBeginXScale(chazhi+durtaion),GetSleepY());
                //再往上移动
                path.lineTo(getBeginXScale(chazhi+durtaion),chartParameter.getChartHeight()-30);
                //回归原点画成封闭矩形
                path.moveTo(getBeginXScale(chazhi),chartParameter.getChartHeight()-30);
                path.close();
                canvas.drawPath(path, paint);
            }
        }
    }


    private void drawStandChart(List<SportRecord> sportRecords,Canvas canvas,Date date,int sportType){
        chartDate = date; //图表的日期
        Paint rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.rgb(0x7E, 0xF6, 0x8C));	//#7EF68C  // F5FE76  //#DFD796
        for(int i = 0; i< sportRecords.size(); i ++){
            SportRecord data = sportRecords.get(i);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            //根据本条数据属于当地时间日期 获取当天的开始UTC时间 方便找到0的坐标点
            String timeChart = TimeUtils.getstartDateTimeUTC(data.getLocalDate(),false);
            //获取今天开始时间的long值
            long timeLong = TimeUtil.stringToLong(timeChart, "yyyy-MM-dd HH:mm:ss");
            //此时是站着的
            if(Integer.parseInt(data.getState()) == BRDetailData.STATE_SIT){
                if(sportType == SIT_TYPE){
                    paint.setColor(GetSitColor());
                }else{
                    paint.setColor(GetWhiteColor());
                }
                //运动数据开始时间相当于今天0点的偏移值 计算出x坐标
                int chazhi = (int)(Long.parseLong(data.getStart_time_utc())-timeLong);
                //运动的持续时间
                int durtaion = Integer.parseInt(data.getDuration());
                //画一个矩形
                //找到一个点
                path.moveTo(getBeginXScale(chazhi), chartParameter.getChartHeight()-30);
                //往上移动到点的上方GetSleepY()的距离
                path.lineTo(getBeginXScale(chazhi),GetSleepY());
                //再往右移动
                path.lineTo(getBeginXScale(chazhi+durtaion),GetSleepY());
                //再往下移动
                path.lineTo(getBeginXScale(chazhi+durtaion),chartParameter.getChartHeight()-30);
                //回归原点画成封闭矩形
                path.moveTo(getBeginXScale(chazhi),chartParameter.getChartHeight()-30);
                path.close();
                canvas.drawPath(path, paint);
            }else if(Integer.parseInt(data.getState()) == BRDetailData.STATE_STAND){
                if(sportType == STAND_TYPE){
                    paint.setColor(GetStandColor());
                }else{
                    paint.setColor(GetWhiteColor());
                }
                //运动数据开始时间相当于今天0点的偏移值 计算出x坐标
                int chazhi = (int)(Long.parseLong(data.getStart_time_utc())-timeLong);
                //运动的持续时间
                int durtaion = Integer.parseInt(data.getDuration());
                //画一个矩形
                //找到一个点
                path.moveTo(getBeginXScale(chazhi), chartParameter.getChartHeight()-30);
                //往上移动到点的上方GetSleepY()的距离
                path.lineTo(getBeginXScale(chazhi),GetSleepY());
                //再往右移动
                path.lineTo(getBeginXScale(chazhi+durtaion),GetSleepY());
                //再往下移动
                path.lineTo(getBeginXScale(chazhi+durtaion),chartParameter.getChartHeight()-30);
                //回归原点画成封闭矩形
                path.moveTo(getBeginXScale(chazhi),chartParameter.getChartHeight()-30);
                path.close();
                canvas.drawPath(path, paint);
            }else if(Integer.parseInt(data.getState()) == BRDetailData.STATE_WALKING || Integer.parseInt(data.getState()) == BRDetailData.STATE_RUNNING){
                if(sportType == WALK_TYPE){
                    paint.setColor(GetWalkColor());
                }else{
                    paint.setColor(GetWhiteColor());
                }
                MyLog.i(TAG,"画图时的走路==============:"+data.toString());
                //运动数据开始时间相当于今天0点的偏移值 计算出x坐标
                int chazhi = (int)(Long.parseLong(data.getStart_time_utc())-timeLong);
                //运动的持续时间
                int durtaion = Integer.parseInt(data.getDuration());
                //画一个矩形
                //找到一个点
                path.moveTo(getBeginXScale(chazhi), chartParameter.getChartHeight()-30);
                //往上移动到点的上方GetSleepY()的距离
                path.lineTo(getBeginXScale(chazhi),GetSleepY());
                //再往右移动
                path.lineTo(getBeginXScale(chazhi+durtaion),GetSleepY());
                //再往下移动
                path.lineTo(getBeginXScale(chazhi+durtaion),chartParameter.getChartHeight()-30);
                //回归原点画成封闭矩形
                path.moveTo(getBeginXScale(chazhi),chartParameter.getChartHeight()-30);
                path.close();
                canvas.drawPath(path, paint);
            }
        }
    }

    /**
     * 画X坐标
     * @param xPaint 画笔
     * @param canvas 画布
     */
    private void drawX(Paint xPaint,Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        //现在准备将那条直线分为六段,从前一天的下午六点,到今天的晚上的零点
        //还要在直线上画出两个像素的坐标线
        String [] x=new String[]{"0am","6am","12am","6pm","12am"};
        xPaint.setTextSize(20);
        //画X轴   起始端点的X坐标  起始端点的Y坐标。终止端点的X坐标 终止端点的Y坐标。
        canvas.drawLine(	0, chartParameter.getChartHeight() - 30,
                chartParameter.getWidth(), chartParameter.getHeight() - 30, xPaint);
        //xPointIndex 第N个X坐标点
        int xPointIndex=0;
        //xPoint x坐标点的位置
        for(int xPoint=0;xPoint <= chartParameter.getWidth();){
			/* public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {*/
            if(xPointIndex<x.length){
                xPaint.setTextAlign(Paint.Align.CENTER);
                if(xPointIndex==0){
                    xPaint.setTextAlign(Paint.Align.LEFT);
                }
                if(xPointIndex==4){
                    xPaint.setTextAlign(Paint.Align.RIGHT);
                }
                //画X轴上的坐标[6pm]
                canvas.drawText(x[xPointIndex],xPoint,chartParameter.getHeight()-5,xPaint);
                //画X轴上的坐标突出点 高度5
                xPaint.setStrokeWidth((float) 1.5);
                canvas.drawLine(xPoint,chartParameter.getChartHeight() - 30,xPoint,chartParameter.getHeight() - 35,xPaint);
                xPoint=xPoint + chartParameter.getWidth()/4;
                xPointIndex++;
            }else {
                break;
            }
        }
    }

    //计算当前数据的时间距离图表时间是多少秒
    /** @deprecated */
    private int getS(String s)  {
        String timenow= TimeUtils.getsleepStartTimeUTC(time);//查询的起始时间,也就是昨天下午六点
        //计算该时间和传进来的时间相差多少秒
        Date d1= null;
        Date d2=null;
        try {
            d1 = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(s);
            d2=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(timenow);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (d1.getTime()/1000-d2.getTime()/1000);
    }




    private float getBeginXScale(int chazhi)
    {
        float width =chartParameter.getXScale()*chazhi;
        return width;
    }


    private float getSportY(BRDetailData  data){
        if(data.getDuration()  > 0 &&( data.getState() == BRDetailData.STATE_WALKING || data.getState() == BRDetailData.STATE_RUNNING)){
            float height =  data.getSteps()/data.getDuration()* chartParameter.getYScale();
            return  (chartParameter.getChartHeight() - height);
        }else{
            return chartParameter.getChartHeight();
        }
    }

    private float GetSleepY(){
        return chartParameter.getChartHeight()-60*chartParameter.getYScale();
    }

    private int GetSportColor(BRDetailData  data){
        int average = 0;
        if(data.getDuration() > 0)
        {
            average = data.getSteps()/data.getDuration();
        }
        if(average > 40){
            return Color.rgb(0xF3, 0x2D, 0x0C); //#F32D0C
        }else if(average > 20){
            return Color.rgb(0xF5, 0xB2, 0x27); //#F5B227
        }else{
            return Color.rgb(0xF7, 0xCA, 0x3F); //#F7CA3F
        }
    }

/*	private int GetSleepColor(int state){
		if(state == BRDetailData.STATE_SLEEP_ACTIVE){
			return Color.rgb(0xFF, 0xB6, 0x30); //#FFB630
		}else if (state == BRDetailData.STATE_SLEEP_LIGHT){
			return Color.rgb(0x30, 0xC3, 0xF9); //#30C3F9
		}else{
			return Color.rgb(0x08, 0x7B, 0xC4); //#087BC4
		}
	}*/

    private int GetSleepColor(){
        return Color.rgb(0x30, 0xC3, 0xF9); //#30C3F9
    }
    private int GetWalkColor(){
        return Color.rgb(0x7E, 0xF6, 0x8C); //#7EF68C
    }

    private int GetSitColor(){
        return Color.rgb(0xF5, 0xFE, 0x76); //#F5FE76
    }

    private int GetStandColor(){
        return Color.rgb(0xDF, 0xD7, 0x96); //#DFD796
    }

    private int GetWhiteColor(){
        return Color.rgb(0xFF, 0xFF, 0xFF); //#DFD796
    }


    private int GetLineColor(){
           return Color.rgb(0xFF, 0xB6, 0x30); //#30C3F9
    }
}
