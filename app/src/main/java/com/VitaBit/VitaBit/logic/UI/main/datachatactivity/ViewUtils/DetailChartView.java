package com.VitaBit.VitaBit.logic.UI.main.datachatactivity.ViewUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.VitaBit.VitaBit.logic.UI.main.datachatactivity.ChartParameter;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.linkloving.band.dto.SportRecord;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.VitaBit.VitaBit.utils.sportUtils.TimeUtils;
import com.linkloving.utils.TimeUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zkx on 2016/7/18.
 */
public class DetailChartView  extends RelativeLayout{
    private final String TAG = DetailChartView.class.getSimpleName();
    //有两个textview,用来显示开始和结束时间
    ///还有一个 LinearLayout
    ImageView dataView;
    ImageView lineView;
    FrameLayout linearLayout;
    TextView timetv;
    Context context;

    int imageWidth;
    int imageHeight;

    /** 每个时间片对应像素宽度（px/片) */
    float xScale;
    float yScale;
    /** // 每个像素xxx分钟 */
    float xlineScale;
    /** 图片生成器 */
    CreateDetailBitmap detailBitmapCreator2;

    private List<SportRecord> sportRecords=new ArrayList<>();

    Date chartDate;

    public DetailChartView(Context context) {
        super(context);
        InitDetailChartControl(context);
    }
    public DetailChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitDetailChartControl(context);
    }
    public DetailChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitDetailChartControl(context);
    }
    /**
     * 初始化图表
     *
     * @param context
     */
    private void InitDetailChartControl(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(com.VitaBit.VitaBit.R.layout.sleepchat, this);
        InitView();
        getViewHigh();
    }

    private void InitView()
    {
        this.dataView = (ImageView) findViewById(com.VitaBit.VitaBit.R.id.sleep_chat1);
        lineView = (ImageView) findViewById(com.VitaBit.VitaBit.R.id.line_chat);
        timetv = (TextView) findViewById(com.VitaBit.VitaBit.R.id.time);
        linearLayout= (FrameLayout) findViewById(com.VitaBit.VitaBit.R.id.chat);
        linearLayout.setOnTouchListener(new OnTouchListenerImpl());
    }

    private class OnTouchListenerImpl implements OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            MyLog.e(TAG,"onTouch X:"+event.getX());
            String time = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(chartDate);
            //获取今天开始时间的long值
            long timeLong = TimeUtil.stringToLong(time, "yyyy-MM-dd");
//            MyLog.e(TAG,"event.getX():"+event.getX());
//            MyLog.e(TAG,"dataView.getWidth():"+dataView.getWidth());
//            MyLog.e(TAG,"每个像素的分钟:"+xlineScale);
//            MyLog.e(TAG,"chartDate:"+timeLong);//图表日期的0点
            if(event.getX()> 0 && event.getX()<dataView.getWidth()){
                MyLog.e(TAG,"当前的时间是："+( (long)(event.getX()*xlineScale) * 60+ timeLong ) );
                long nowtime =(long)(event.getX()*xlineScale) * 60+ timeLong ;
                timetv.setText(TimeUtils.formatTimeHHMM(nowtime));
                moveLineViewWithFinger(lineView,event.getX());
                moveTimeViewWithFinger(timetv,event.getX());
            }
            return true;
        }
    }


    private void moveTimeViewWithFinger(View view, float rawX) {
        MyLog.e(TAG,"view.getLeft()："+view.getLeft());
        MyLog.e(TAG,"view.getRight()："+view.getRight());
        MyLog.e(TAG,"rawX："+rawX);
        if(lineView.getLeft()<view.getWidth()/2){
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        if((dataView.getWidth()-lineView.getRight())<view.getWidth()/2 ){
            MyLog.e(TAG,"------------");
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 设置View的布局属性，使得view随着手指移动 注意：view所在的布局必须使用RelativeLayout 而且不得设置居中等样式
     *
     * @param view
     * @param rawX
     */
    private void moveLineViewWithFinger(View view, float rawX) {
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
        view.setLayoutParams(layoutParams);
    }

    private void initValues()
    {
        initXScale();
        detailBitmapCreator2 = new CreateDetailBitmap(context);
        detailBitmapCreator2.initChartParameter(new ChartParameter(xScale, yScale, imageWidth, imageHeight));
    }
    private void initXScale()
    {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int width = dm.widthPixels;//用屏幕的宽度
        imageWidth = dataView.getWidth();
        xScale = (float) imageWidth / (3600*24f);// 24小时一屏,(1s一片)  每个时间片所占的像素
        xlineScale = (60*24f) / (float) imageWidth;// 每个像素xxx分钟
        yScale = imageHeight / 80;
    }

    private void  getViewHigh(){
        final ViewTreeObserver vto = linearLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageHeight= linearLayout.getMeasuredHeight();
                int width = DetailChartView.this.getMeasuredWidth();
                initValues();
//                MyLog.i("linearLayout获取到的高度:" + imageHeight+"   "+width);
                return true;
            }
        });
    }

    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     * @param
     * @return
     */
    private void AsyncAddLineChart(final float xScale)
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                MyLog.i("=================AsyncAddChart（）");
                Bitmap bitmapChart2=detailBitmapCreator2.drawLineChar(xScale);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }
            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                lineView.setImageBitmap(bitmaps.get(0));
//                bitmaps.get(0).recycle();
            }
        }.execute();
    }

    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     * @param
     * @return
     */
    private void AsyncAddSitChart(final List<SportRecord> list)
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                MyLog.i("=================AsyncAddChart（）");
                Bitmap bitmapChart2=detailBitmapCreator2.drawDetailChartSit(list,chartDate);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }
            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                dataView.setImageBitmap(bitmaps.get(0));
            }
        }.execute();
    }

    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     * @param
     * @return
     */
    private void AsyncAddStandChart(final List<SportRecord> list, final int sportType)
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                MyLog.i("=================AsyncAddChart（）");
                Bitmap bitmapChart2=detailBitmapCreator2.drawDetailChartStand(list,chartDate,sportType);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }
            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                dataView.setImageBitmap(bitmaps.get(0));
            }
        }.execute();
    }


    public void initSitChart(ArrayList<SportRecord> list,Date dayindex,int sportType)
    {
        MyLog.i("=================来了initChart（）");
        dataView.setImageBitmap(null);
        chartDate=dayindex;
        if(list!=null && list.size()>0)
//            AsyncAddSitChart(list);
            AsyncAddStandChart(list,sportType);
    }

    public void initStandChart(ArrayList<SportRecord> list,Date dayindex,int sportType)
    {
        MyLog.i("=================来了initChart（）");
        dataView.setImageBitmap(null);
        chartDate=dayindex;
        if(list!=null && list.size()>0)
            AsyncAddStandChart(list,sportType);
    }

}

