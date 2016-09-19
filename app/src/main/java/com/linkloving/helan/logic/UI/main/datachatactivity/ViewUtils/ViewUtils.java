package com.linkloving.helan.logic.UI.main.datachatactivity.ViewUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.linkloving.helan.R;
import com.linkloving.helan.utils.sportUtils.Datahelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class ViewUtils {

    public static void initchat(Context context, CombinedChart barChart) {
        //设置图表的一些属性
        barChart.setDrawOrder(new CombinedChart.DrawOrder[] {CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(context.getResources().getColor(R.color.white));
        // 设置背景
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true); //是否显示X坐标轴及对应的刻度竖线，默认是true
        xAxis.setSpaceBetweenLabels(0);
        //X坐标点描述的颜色
        xAxis.setTextColor(Color.WHITE);
        // 隐藏左Y坐标轴
        barChart.getAxisLeft().setEnabled(false);
        // 隐藏右Y坐标轴
        barChart.getAxisRight().setEnabled(false);
        // 显示表格颜色
        barChart.setGridBackgroundColor(context.getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        barChart.setDrawBorders(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 是否可以拖拽
        barChart.setDragEnabled(false);
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(3000);
        barChart.fitScreen();
        //图表描述
        barChart.setDescription("");
        //去除了中间的字体
        barChart.setNoDataText(" ");
        barChart.setNoDataTextDescription("No data(⊙o⊙)");
    }

    /**
     * 日数据 24h 8h 3h UI
     * @param context
     * @param barChart
     */
    public static void initDayChat(Context context, CombinedChart barChart) {
        //设置图表的一些属性
        barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(context.getResources().getColor(R.color.white));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //zhang 修改
        xAxis.setDrawGridLines(true);
//        xAxis.setGridColor(Color.WHITE);
//        xAxis.setGridLineWidth(1f);

        //x坐标的颜色
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setLabelsToSkip(0);
//        xAxis.setAvoidFirstLastClipping(true);//如果设置为true，则在绘制时会避免“剪掉”在x轴上的图表或屏幕边缘的第一个和最后一个坐标轴标签项。但是会偏移掉
        //画x坐标
        xAxis.setDrawAxisLine(true);
        //X坐标点描述的颜色
        xAxis.setTextColor(Color.WHITE);
        //设置下标字体的大小
        //xAxis.setTextSize(5);
        Paint xPaint = new Paint();
        xPaint.setColor(Color.rgb(255, 255, 255));
        barChart.setPaint(xPaint, 0);
        // 隐藏左Y坐标轴
        barChart.getAxisLeft().setEnabled(false);
        // 隐藏右Y坐标轴
        barChart.getAxisRight().setEnabled(false);
        // 显示表格背景颜色
        barChart.setGridBackgroundColor(context.getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        barChart.setDrawBorders(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 是否可以拖拽
        barChart.setDragEnabled(false);
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(3000);
        //图表描述
        barChart.setDescription("");
        //去除了中间的字体
        barChart.setNoDataText(" ");
        //没有数据显示
        barChart.setNoDataTextDescription("");
    }

    public static void initXAxis(Context context, CombinedChart barChart,int type) {
        XAxis xAxis = barChart.getXAxis();
        //x坐标的颜色
        xAxis.enableGridDashedLine(20f,2000f,0f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setSpaceBetweenLabels(0);
        if(type == Datahelper.TYPE_3H){
            xAxis.setLabelsToSkip(5);
        }else if(type == Datahelper.TYPE_8H){
            xAxis.setLabelsToSkip(3);
        }else if(type == Datahelper.TYPE_24H){
            xAxis.setLabelsToSkip(7);
        }else{
            xAxis.setLabelsToSkip(0);
        }
    }


    public static BarDataSet getDataSet(Context context,List<BarEntry> entries){
        BarDataSet dataSet = new BarDataSet(entries, "");
        // 柱形图颜色
        dataSet.setColor(context.getResources().getColor(R.color.sleep_blue));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(context.getResources().getColor(R.color.white));
        // 柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        //柱状图粗细
//        dataSet.setBarSpacePercent(60);
        dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return dataSet;
    }

    /**
     * 创建X坐标点
     * @param startTime
     * @return
     */
    public static List<String> creat3HListXxis(String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        List<String> timeList = new ArrayList<>();
        try {
            Date startDate= new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            timeList.add(df.format(calendar.getTime())); //00.00
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //00.30
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //10.00
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //01.30
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //02.00
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //02.30
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
            timeList.add(df.format(calendar.getTime())); //03.00
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeList;
    }

    /**
     * 创建X坐标点
     * @param startTime
     * @return
     */
    public static List<String> creat8HListXxis(String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        List<String> timeList = new ArrayList<>();
        try {
            Date startDate= new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            timeList.add(df.format(calendar.getTime())); //00.00
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //01

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //02

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //03

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //04

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //05

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //06

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //07

            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            timeList.add(df.format(calendar.getTime())); //08

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeList;
    }

    public static int[] getChartColors(Context context){
        int[] colors = new int[]{
                context.getResources().getColor(R.color.chart_color_stand),
                context.getResources().getColor(R.color.chart_color_sit),
                context.getResources().getColor(R.color.chart_color_walk)};
        return colors;
    }
}
