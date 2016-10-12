package com.VitaBit.VitaBit.logic.UI.main.datachatactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.VitaBit.VitaBit.utils.manager.AsyncTaskManger;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.R;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.db.summary.DaySynopicTable;
import com.VitaBit.VitaBit.logic.UI.main.datachatactivity.ViewUtils.ViewUtils;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.prefrences.PreferencesToolkits;
import com.VitaBit.VitaBit.utils.CommonUtils;
import com.VitaBit.VitaBit.utils.DateSwitcher;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.VitaBit.VitaBit.utils.sportUtils.Datahelper;
import com.VitaBit.VitaBit.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkx on 2016/7/18.
 */
public class StandDataActivity extends ToolBarActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private static final String TAG = StandDataActivity.class.getSimpleName();
    SimpleDateFormat dateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD);
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> dayDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> weekDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> monthDataAsync = null;
    private CombinedChart combbarChart;
    private UserEntity userEntity;
    private RadioButton dayButton,weekButton,monthButton,leftDay,middleDay,rightDay;
    private List<BarEntry> mentries = new ArrayList<>();
    private List<DaySynopic> listData = new ArrayList<>();
    Map<Integer, List<SportRecord>> sportRecords = new HashMap<>();//日数据查询结果
    private Map<Integer,View> listView;//存放viewpager的view
    private ViewPager mViewPager;
    MyPageadapter dayadapter;
    MyPageadapter weekadapter;
    MyPageadapter monthadapter;
    private DateSwitcher weekSwitcher = null;
    private DateSwitcher monthSwitcher= null;
    String timeNow;
    Date date;
    String startDateString;
    String endDateString;
    ProgressDialog progressDialog;

    LinearLayout chart_hour_layout;
    RadioGroup chart_hour_type;
    RadioButton radio_3h,radio_8h,radio_24h;

    int pageSelectPosition; //记住切换日期后的日期
    int hourSelectPosition = Datahelper.TYPE_24H; //记住切换小时
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_data);
        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        monthSwitcher= new DateSwitcher(DateSwitcher.PeriodSwitchType.month);
        listView=new HashMap<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.summarizing_data));
        progressDialog.setCanceledOnTouchOutside(false);
        initTitle();//刚刚进页面的时候初始化那三个字段
    }

    @Override
    protected void getIntentforActivity() {
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        timeNow=getIntent().getStringExtra("time");
        date= ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
    }
    private void initTitle() {
        //日详情
        if (dayButton.isChecked()) {
            int days = ToolKits.getBetweenDay(new Date(0), new Date());
            dayadapter = new MyPageadapter(days);
            mViewPager.setAdapter(dayadapter);
            int day = ToolKits.getBetweenDay(date, new Date());
            MyLog.e(TAG, "days=" + days + "  " + day);
            mViewPager.setCurrentItem(days - day-1);
        }

        //周数据
        else if (weekButton.isChecked()) {
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            //计算传进来时间与1970/01/01差几周
            int day=ToolKits.getBetweenDay(new Date(0),date);
            int weeknow=day/7;//系统时间1970/01/01与总的周数
            int week2=day%7;
            if(week2>3){
                weeknow=weeknow+1;
            }
            weekadapter=new MyPageadapter(weekcount);
            mViewPager.setAdapter(weekadapter);
            MyLog.e(TAG, "相差几周=" + (weekcount - weeknow));
            mViewPager.setCurrentItem(weeknow-1);
        }

        //月数据
        else if (monthButton.isChecked()) {
            //判断传进来的时间是几月
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim2 = new java.text.SimpleDateFormat("yyyy-MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            int year = Integer.parseInt(sim3.format(new Date()));
            int nowyear = Integer.parseInt(sim3.format(date));
            int s1 = Integer.parseInt(sim1.format(date));//传进来时间的月份
            int s2 = Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int count = (year - 1970) * 12 + s2 + 1;
            monthadapter = new MyPageadapter(count);
            mViewPager.setAdapter(monthadapter);
            mViewPager.setCurrentItem((nowyear - 1970) * 12 + s1);//计算出当前时间应该是第几个月
        }
    }

    public class MyPageadapter extends PagerAdapter {
        int mcount;

        public void setMcount(int mcount) {
            this.mcount = mcount;
        }
        public MyPageadapter(int count){
            mcount=count;
        }
        @Override
        public int getCount() {
            return mcount;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        //销毁Item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(listView.get(position));
        }
        //实例化Item
        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            //根据position区添加新的view
            View view=getView(position);
            view.setId(position);
            listView.put(position, view);
            container.addView(view);
            return view;
        }
    }
    private View getView(int position) {
        if(dayButton.isChecked()){
            LayoutInflater inflater=getLayoutInflater();
            View view = inflater.inflate(R.layout.diatance_data_view, null);
            return view;
        }else {
            LayoutInflater inflater=getLayoutInflater();
            View view = inflater.inflate(R.layout.step_data_view, null);
            return view;
        }
    }
    //查询到数据之后再来给viewpager的各个view赋值
    private void setDataToView(int position,List<DaySynopic> daySynopicList){
        if(dayButton.isChecked()){
            View view=mViewPager.findViewById(position);

            RelativeLayout shuju= (RelativeLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView data= (TextView) view.findViewById(R.id.data);
            TextView title= (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.unit_minute));
            if(daySynopicList==null || daySynopicList.size()<=0 ){
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }else{

                double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopicList.get(0).getStandTime()), 1);
                NumberFormat nf=new DecimalFormat( "0.0 ");
                if(standDuration==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);
                    data.setText(nf.format(standDuration)+ " ");
                }
            }
        }else{
            View view=mViewPager.findViewById(position);
            LinearLayout shuju= (LinearLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView data= (TextView) view.findViewById(R.id.step_count);
            TextView dataTitle= (TextView) view.findViewById(R.id.data_count_title);
            ListView listviewstep= (ListView) view.findViewById(R.id.listview_step);
            if(daySynopicList!=null && daySynopicList.size()>0){
                double count=0;
                for(DaySynopic mDaySynopic : daySynopicList){
                    double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getStandTime()), 1);
                    count += standDuration;
                }
                if(count==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    NumberFormat nf=new DecimalFormat( "0.0 ");
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);
                    dataTitle.setText(getString(R.string.count_title));
                    data.setText(nf.format(count) + "");//getString(R.string.unit_minute)
                    MySleepAdapter adapter=new MySleepAdapter(StandDataActivity.this, (ArrayList<DaySynopic>) daySynopicList);
                    listviewstep.setAdapter(adapter);
                }
            }else {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }
        }
    }

    public class MySleepAdapter extends BaseAdapter {
        String s1=getString(R.string.sunday);
        String s2=getString(R.string.monday);
        String s3=getString(R.string.tuesday);
        String s4=getString(R.string.wednesday);
        String s5=getString(R.string.thursday);
        String s6=getString(R.string.friday);
        String s7=getString(R.string.saturday);
        String[] weekDays = { s1, s2, s3, s4, s5, s6, s7 };
        private Context mcontext;
        private ArrayList<DaySynopic> list;
        private ArrayList<DaySynopic> mlist=new ArrayList<>();

        public MySleepAdapter(Context context, ArrayList<DaySynopic> list){
            this.list=list;
            this.mcontext=context;
            initlist();
        }
        //过滤掉0的记录
        private void initlist  (){
            for(DaySynopic d:list){
                double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(d.getStandTime()), 1);
                double count = standDuration;
                if(count != 0){
                    mlist.add(d);
                }
            }
        }
        @Override
        public int getCount() {
            return mlist.size();
        }
        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.step_listview_item, null);
                vh = new ViewHolder();
                vh.chatItem= (LinearLayout) convertView.findViewById(R.id.chart_item);
                vh.textViewcount= (TextView) convertView.findViewById(R.id.count);
                vh.textViewdate=(TextView) convertView.findViewById(R.id.date);
                vh.textViewpercent=(TextView) convertView.findViewById(R.id.percent);
                vh.textViewpercent.setVisibility(View.GONE);
                vh.textViewweek=(TextView) convertView.findViewById(R.id.week);
                vh.unit_step= (TextView)convertView.findViewById(R.id.unit_step);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }


//            vh.chatItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //切换图表到当前的日期
//                    if(!CommonUtils.isStringEmpty(mlist.get(position).getData_date())){
//                        Date dateChage=ToolKits.stringToDate(mlist.get(position).getData_date(),ToolKits.DATE_FORMAT_YYYY_MM_DD);
//                        date=dateChage;
//                        dayButton.setChecked(true);
//                        combbarChart.setVisibility(View.GONE);
//                        initTitle();
//                    }else {
//                        //得到的日期是空的
//                        MyLog.i(TAG,"点击后,获得的时间是空的");
//                    }
//                }
//            });

            vh.textViewdate.setText(mlist.get(position).getData_date());
            //星期,目标
            Calendar cal=Calendar.getInstance();
            cal.setTime(ToolKits.stringToDate(mlist.get(position).getData_date(),ToolKits.DATE_FORMAT_YYYY_MM_DD));
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }
            vh.textViewweek.setText(weekDays[w]);
            DaySynopic mDaySynopic=mlist.get(position);
            double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getStandTime()), 1);
            double count = standDuration;
            NumberFormat nf=new DecimalFormat( "0.0 ");
            vh.textViewcount.setText(nf.format(count)+" ");
            vh.unit_step.setText(getString(R.string.unit_minute));
            //运动目标
            float  sleeptime_goal= Float.parseFloat(PreferencesToolkits.getGoalInfo(mcontext, PreferencesToolkits.KEY_GOAL_SLEEP));
            MyLog.i(TAG,"算出来的时间"+count+"目标:"+sleeptime_goal);

            if(sleeptime_goal==0){

            }else {
                vh.textViewpercent.setText(Math.ceil(count*100*1.0f/sleeptime_goal)+"%");
            }
            return convertView;
        }
        public class ViewHolder {
            public TextView textViewdate, textViewweek,textViewcount,textViewpercent,unit_step;
            public LinearLayout chatItem;
        }
    }
    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.detail_stand_title));
        combbarChart = (CombinedChart) findViewById(R.id.barChart);

        chart_hour_layout = (LinearLayout) findViewById(R.id.chart_hour_layout);
        chart_hour_type = (RadioGroup) findViewById(R.id.chart_hour_type);
        radio_3h = (RadioButton) findViewById(R.id.radio_3h);
        radio_8h = (RadioButton) findViewById(R.id.radio_8h);
        radio_24h = (RadioButton) findViewById(R.id.radio_24h);
        radio_24h.setChecked(true);

        dayButton= (RadioButton) findViewById(R.id.sleep_page_activity_circleviews_dayRb);
        weekButton= (RadioButton) findViewById(R.id.sleep_page_activity_circleviews_weekRb);
        monthButton= (RadioButton) findViewById(R.id.sleep_page_activity_circleviews_monthRb);
        leftDay = (RadioButton) findViewById(R.id.day01);
        middleDay = (RadioButton) findViewById(R.id.day02);
        rightDay = (RadioButton) findViewById(R.id.day03);
        mViewPager = (ViewPager) findViewById(R.id.my_ViewPager);

        ViewUtils.initDayChat(this,combbarChart);
    }

    //选择小时的RadioGroup切换设置
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        MyLog.e(TAG, "--------------onCheckedChanged");
        if(radio_3h.isChecked()){
            MyLog.e(TAG, "选中3小时的了");
            hourSelectPosition =  Datahelper.TYPE_3H;
            changeChat(pageSelectPosition);
        }else if(radio_8h.isChecked()){
            hourSelectPosition =  Datahelper.TYPE_8H;
            MyLog.e(TAG, "选中8小时的了");
            changeChat(pageSelectPosition);
        }else if(radio_24h.isChecked()){
            hourSelectPosition =  Datahelper.TYPE_24H;
            MyLog.e(TAG, "选中24小时的了");
            changeChat(pageSelectPosition);
        }
    }

    //构建日数据X轴 并且获取日数据
    private CombinedData generateDayBarData(String startTime,String endTime) {
        YAxis yAxis = combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        List<String> xVals = creatXxis(startTime,endTime);
        CombinedData data = new CombinedData(xVals);
        if (sportRecords != null) {
            data.setData(getDayBarData());
        }
        return data;
    }

    private CombinedData generateWeekBarData() {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        String s1=getString(R.string.sun);
        String s2=getString(R.string.mon);
        String s3=getString(R.string.tues);
        String s4=getString(R.string.wed);
        String s5=getString(R.string.thurs);
        String s6=getString(R.string.fri);
        String s7=getString(R.string.sat);
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 7 ;i++){
            switch (i){
                case 0:
                    xVals.add(s2);
                    break;
                case 1:
                    xVals.add(s3);
                    break;
                case 2:
                    xVals.add(s4);
                    break;
                case 3:
                    xVals.add(s5);
                    break;
                case 4:
                    xVals.add(s6);
                    break;
                case 5:
                    xVals.add(s7);
                    break;
                default: xVals.add(s1);
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        data.setData(getWeekBarData());
        return data;
    }

    private CombinedData generateMonthBarData(String timeNow) {
        YAxis yAxis =combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        //横坐标标签
        Calendar c = Calendar.getInstance();
        Date date=ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        c.setTime(date);
        int j=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        MyLog.e(TAG, "这个月多少天" + j);
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 1;i<=j ;i++){
            switch (i){
                case 1:
                    xVals.add(i-1,i+"");
                    break;
                case 5:
                    xVals.add(i-1,i+"");
                    break;
                case 10:
                    xVals.add(i-1,i+"");
                    break;
                case 15:
                    xVals.add(i-1,i+"");
                    break;
                case 20:
                    xVals.add(i-1,i+"");
                    break;
                case 25:
                    xVals.add(i-1,i+"");
                    break;
                case 30:
                    xVals.add(i-1,i+"");
                    break;
                default:
                    xVals.add(i-1,"");
                    break;
            }

        }
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        data.setData(getMonthBarData());
        return data;
    }

    /**
     * 创建图表数据属性 获取数据  显示在图表上
     * @return BarData
     */
    private BarData getDayBarData() {
        /**图表具体设置*/
        List<BarEntry> chartData = new ArrayList<>();//显示条目
        int chartDataSzie;
        if(hourSelectPosition == Datahelper.TYPE_3H){
            combbarChart.getAxisLeft().setAxisMaxValue(300);
            chartDataSzie = 36;
        }else if(hourSelectPosition == Datahelper.TYPE_8H){
            combbarChart.getAxisLeft().setAxisMaxValue(900);
            chartDataSzie = 32;
        }
        else
        {
            combbarChart.getAxisLeft().setAxisMaxValue(2700);
            chartDataSzie = 32;
        }
        for (int i = 0; i < chartDataSzie; i++) {
            chartData.add(new BarEntry(Datahelper.getFloatsData(sportRecords.get(i)), i)); //走坐站
            MyLog.e(TAG,"第"+i+"个Datahelper.站:"+Datahelper.getFloatsData(sportRecords.get(i))[0]);
            MyLog.e(TAG,"第"+i+"个Datahelper.坐:"+Datahelper.getFloatsData(sportRecords.get(i))[1]);
            MyLog.e(TAG,"第"+i+"个Datahelper.走:"+Datahelper.getFloatsData(sportRecords.get(i))[2]);
        }

        //经过测试,添加参考线放在这里才会显示出来,具体原因还是不知道
        mentries = chartData;
//        addline(R.id.report_page_activity_circleviews_dayRb);
        BarDataSet dataSet = new BarDataSet(chartData, "");
        dataSet.setColors(ViewUtils.getChartColors(StandDataActivity.this)); //设置多种颜色
        dataSet.setDrawValues(false);// 柱形图顶端字是否显示
        dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色'
        dataSet.setHighLightColor(Color.BLUE); //触摸的时候回变成蓝色
//        dataSet.setColor(Color.rgb(223,167,54));
        BarData bardata = new BarData();
        bardata.addDataSet(dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }
    //获取周数据
    private BarData getWeekBarData() {
        if(listData==null||listData.size()<=0){
            return new BarData();
        }
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一周7条数据
        for(int i=0;i<listData.size();i++){
            MyLog.e(TAG, listData.get(i).toString());
            Calendar c = Calendar.getInstance();
            Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
            c.setTime(date);
            //计算站立时间
            double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getStandTime()), 1);
            if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){
                entries.add(new BarEntry((float) standDuration, 6));
            }else {
                entries.add(new BarEntry((float) standDuration, c.get(c.DAY_OF_WEEK)-2));
            }
        }
        mentries=entries;
//        addLimitline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet =ViewUtils.getDataSet(StandDataActivity.this,entries);
        bardata.addDataSet(dataSet);
        return bardata;
    }
    //获取月数据
    private BarData getMonthBarData() {
        if(listData==null||listData.size()<=0){
            return new BarData();
        }
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一周7条数据
        MyLog.e(TAG, "获得数据" + listData.size());
        for(int i=0;i<listData.size();i++){
            // float profit= random.nextFloat()*1000;
            MyLog.e(TAG, listData.get(i).toString());
            Calendar calendar = Calendar.getInstance();
            Date date = ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
            calendar.setTime(date);
            String dayStr = new java.text.SimpleDateFormat("dd").format(calendar.getTime()); //获取月的每一天day
            int dayInt=Integer.parseInt(dayStr)-1;
            double standDuration = CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getStandTime()), 1);
            entries.add(new BarEntry((float) standDuration,dayInt));
            //entries.add(new BarEntry((Float.parseFloat(listData.get(i).getSleepMinute())/60+Float.parseFloat(listData.get(i).getDeepSleepMiute())/60),s-1));
        }
        mentries=entries;
//        addLimitline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet =ViewUtils.getDataSet(StandDataActivity.this,entries);
        bardata.addDataSet(dataSet);
        return bardata;
    }

    private void addLimitline(int i){
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        float f=0;
        for(BarEntry barEntry:mentries){
            MyLog.e(TAG, "barEntry.getVal()："+barEntry.getVal());
            if(barEntry.getVal()>f){
                f=barEntry.getVal();
            }
        }
        //没有数据就不画了
        if(f==0){
            return;
        }
        switch (i){
            case R.id.report_page_activity_circleviews_dayRb:
                MyLog.e(TAG, yAxis.getAxisMaxValue() + "最大值");
                LimitLine limitLine1 = new LimitLine((int)f-50,(int)f-50+"");
                limitLine1.setLineColor(Color.WHITE);
                limitLine1.setLineWidth(1f);
                limitLine1.setTextColor(Color.WHITE);
                limitLine1.setTextSize(12f);
                limitLine1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                LimitLine limitLine2 = new LimitLine((int)f/2,(int)f/2+"");
                limitLine2.setLineColor(Color.WHITE);
                limitLine2.setLineWidth(1f);
                limitLine2.setTextColor(Color.WHITE);
                limitLine2.setTextSize(12f);
                limitLine2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
               /* yAxis.addLimitLine(limitLine2);
                yAxis.addLimitLine(limitLine1);*/
                break;
            case R.id.report_page_activity_circleviews_weekRb:
                //紧界线
                if((int)f<100){
                    yAxis.setAxisMaxValue(100);
                }else {
                    yAxis.setAxisMaxValue(f+10);
                }
                MyLog.e(TAG, yAxis.getAxisMaxValue() + "最大值");
                LimitLine limitLine = new LimitLine(100,10+"");
                limitLine.setLineColor(Color.WHITE);
                limitLine.setLineWidth(1f);
                limitLine.setTextColor(Color.WHITE);
                limitLine.setTextSize(12f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                yAxis.addLimitLine(limitLine);
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                break;
        }
    }

    @Override
    protected void initListeners() {
        dayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        chart_hour_type.setOnCheckedChangeListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                MyLog.e(TAG, "onPageSelected=" + position);
                pageSelectPosition = position;
                changePageTitle(position);
                changeChat(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void changePageTitle(int position){
        //日详情
        if(dayButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0), new Date());
            int day=days-position-1;
            Date dataChange=ToolKits.getDayFromDate(new Date(),-(day));
            date=dataChange;

            if(day==0){
                //传进来的是今天的数据
                leftDay.setText(getString(R.string.yesterday));
                middleDay.setText(getString(R.string.today));
                middleDay.setChecked(true);
                rightDay.setText("");
            } else if(day==1) {
                //计算传传进来的日期是几天
                Date d=ToolKits.getDayFromDate(new Date(),-2);
                leftDay.setText(dateFormat.format(d));
                middleDay.setText(getString(R.string.yesterday));
                middleDay.setChecked(true);
                rightDay.setText(getString(R.string.today));
            }else if(day==2){
                Date date1=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day+1));
                leftDay.setText(dateFormat.format(date3));
                middleDay.setText(dateFormat.format(date1));
                rightDay.setText(getString(R.string.yesterday));
            }
            else{
                Date date1=ToolKits.getDayFromDate(new Date(),-(day+1));
                Date data2=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day-1));
                leftDay.setText(dateFormat.format(date1));
                middleDay.setText(dateFormat.format(data2));
                middleDay.setChecked(true);
                rightDay.setText(dateFormat.format(date3));
            }
        }
        //周数据
        else if(weekButton.isChecked()){
            //传来的position是的几周//更当前时间比较
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            MyLog.e(TAG, "相差几周=" + (weekcount - position - 1));
            if(weekcount-position-1==0){
                //传进来的是本周
                // 上周  本周
                leftDay.setText(getString(R.string.lastweek));
                middleDay.setText(getString(R.string.thisweek));
                middleDay.setChecked(true);
                rightDay.setText("");
            }
            else if(weekcount-position-1==1){
                // 日期  上周  本周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));
                leftDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                middleDay.setText(getString(R.string.lastweek));
                middleDay.setChecked(true);
                rightDay.setText(getString(R.string.thisweek));
            }else if(weekcount-position-1==2){
                //日期 日期  上周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*3))));//相差三周的日期
                leftDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));//相差两周周的日期
                middleDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                rightDay.setText(getString(R.string.lastweek));

            }else {
                //日期 日期  日期
                GregorianCalendar base1 = new GregorianCalendar();
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position)*7)));//减一周的日期
                leftDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-1)*7)));//中间是当前日期
                middleDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-2)*7)));//加一周的日期
                rightDay.setText(dateFormat.format(weekSwitcher.getStartDate())+ "~" +dateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
            }

        }
        //月数据
        else if(monthButton.isChecked()){
            //根据position当前是第几月  代表的是第几个月
            int month=monthadapter.getCount();//当前的总共的月
            MyLog.e(TAG, "month=" + month + "position=" + position);
            java.text.SimpleDateFormat sim2 = new java.text.SimpleDateFormat("yyyy-MM");
            if(position==month-1){
                //传进来的是本月的数据
                //当前是本月
                leftDay.setText(getString(R.string.lastmonth));
                middleDay.setText(getString(R.string.thismonth));
                middleDay.setChecked(true);
                rightDay.setText("");
            } else if(position==month-2) {
                //上个月
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -2);
                String s=sim2.format(base1.getTime());
                leftDay.setText(s);
                middleDay.setText(getString(R.string.lastmonth));
                middleDay.setChecked(true);
                rightDay.setText(getString(R.string.thismonth));
            }else if(position==month-3){
                GregorianCalendar base1 = new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -3);
                String days1=sim2.format(base1.getTime()) ;//相差三个月
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH, -2);
                String days2=sim2.format(base2.getTime()) ;//相差两个个月
                leftDay.setText(days1);
                middleDay.setText(days2);//相差两个月
                middleDay.setChecked(true);
                rightDay.setText(getString(R.string.lastmonth));
            }else{
                //相差了month-position-1
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -(month-position));
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH,-(month-position-1));
                GregorianCalendar base3 = new GregorianCalendar();
                base3.setTime(new Date());
                base3.add(GregorianCalendar.MONTH, -(month-position-2));
                String days1=sim2.format(base1.getTime()) ;//前一个月
                String days2=sim2.format(base2.getTime()) ;//本月
                String days3=sim2.format(base3.getTime());//后一个月
                leftDay.setText(days1);
                middleDay.setText(days2);
                middleDay.setChecked(true);
                rightDay.setText(days3);
            }
        }
    }

    //切换图表设置
    private void changeChat(final int position){

        if(dayButton.isChecked()){
            //计算出当前的时期
            AsyncTask myDayAsync=new AsyncTask<Object,Object,List<DaySynopic>>(){
                Date dateNow = null;
                String startTime = null,endTime = null;
                @Override
                protected void onPreExecute() {
                    if(progressDialog!=null){
                        progressDialog.show();
                    }
                }
                @Override
                protected List<DaySynopic> doInBackground(Object... params) {
                    dateNow = ToolKits.getDayFromDate(new Date(0), position + 1);
                    int days = ToolKits.getBetweenDay(new Date(0), new Date());
                    if(days - position - 1 == 0){ //这是今天
                        if(hourSelectPosition == Datahelper.TYPE_3H){       //若用户选择3H类型
                            String hourstr= new SimpleDateFormat("HH").format(new Date());         //首先获得当前的小时数
                            int hour = Integer.parseInt(hourstr);
                            if(hour<3){     //   若当前小时 -3 跨天了 则从当天0点 查到3点的数据
                                startTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date())+" 00";
                                endTime   = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date())+" 03";
                            }else{  //若当前小时 -3 未跨天 查询当前时间到3小时前的数据
                                startTime = ToolKits.getChartBeforeHourTime(new Date(),3);
                                endTime = ToolKits.getChartHourTime(new Date());
                                Log.e(TAG,"当前时间startTime："+startTime);
                                Log.e(TAG,"当前时间endTime："+endTime);
                            }
                            //构建数据集
                            sportRecords = new Datahelper(StandDataActivity.this, String.valueOf(userEntity.getUser_id()), startTime, endTime, Datahelper.TYPE_3H).getSportRecords();
                        }else  if(hourSelectPosition == Datahelper.TYPE_8H){       //若用户选择8H类型
                            String hourstr= new SimpleDateFormat("HH").format(new Date());         //首先获得当前的小时数
                            int hour = Integer.parseInt(hourstr);
                            if(hour<8){ //若当前小时 -3 跨天了 则从当天0点 查到8点的数据
                                startTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date())+" 00";
                                endTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date())+" 08";
                            }else{  //若当前小时 -3 未跨天 查询当前时间到3小时前的数据
                                startTime = ToolKits.getChartBeforeHourTime(new Date(),8);
                                endTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM).format(new Date());
                            }
                            sportRecords = new Datahelper(StandDataActivity.this, String.valueOf(userEntity.getUser_id()), startTime, endTime, Datahelper.TYPE_8H).getSportRecords();
                        }else  if(hourSelectPosition == Datahelper.TYPE_24H){       //若用户选择24H类型
                            startTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(dateNow);
                            endTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(dateNow);
                            sportRecords = new Datahelper(StandDataActivity.this, String.valueOf(userEntity.getUser_id()), startTime, endTime, Datahelper.TYPE_24H).getSportRecords();
                        }
                    }else{//不是今天只显示24h的
                        hourSelectPosition =  Datahelper.TYPE_24H;
                        startTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(dateNow);
                        endTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(dateNow);
                        sportRecords = new Datahelper(StandDataActivity.this, String.valueOf(userEntity.getUser_id()), startTime, endTime, Datahelper.TYPE_24H).getSportRecords();
                    }
                    //查询这天的数据
                    String time = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(dateNow);
                    //查询汇总表的数据
                    List<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(StandDataActivity.this, userEntity.getUser_id()+"", time, time, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                    DaySynopic mDaySynopic = new DaySynopic();
                    List<DaySynopic> temp=new ArrayList<>();
                    if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                        mDaySynopic= SportDataHelper.offlineReadMultiDaySleepDataToServer(StandDataActivity.this, time, time);
                        if(mDaySynopic.getTime_zone()==null){
                            //没有这一天的数据
                        }else {
                            temp.add(mDaySynopic);
                            DaySynopicTable.saveToSqliteAsync(StandDataActivity.this,temp,userEntity.getUser_id()+"");
                        }

                    }else {
                        temp = mDaySynopicArrayList;
                    }
                    listData = temp;
                    return temp;
                }

                //取出数据 开始绘图
                @Override
                protected void onPostExecute(List<DaySynopic> aVoid) {
                    dateNow = ToolKits.getDayFromDate(new Date(0), position + 1);
                    int days = ToolKits.getBetweenDay(new Date(0), new Date());
                    if(days - position - 1 != 0){ //不是今天无需切换时间
                        chart_hour_type.setVisibility(View.GONE);
                    }else{                         //今天则显示
                        chart_hour_type.setVisibility(View.VISIBLE);
                    }
                    ViewUtils.initXAxis(StandDataActivity.this,combbarChart,hourSelectPosition);
                    combbarChart.setData(generateDayBarData(startTime,endTime));
                    combbarChart.invalidate();
                    setDataToView(position, listData);
                    if(progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (dayDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(dayDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(dayDataAsync = myDayAsync);
            myDayAsync.execute();
        }

        if(weekButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            MyLog.e(TAG, "相差几周=" + (weekcount - position - 1));
            weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount - position - 1) * 7)));//中间是当前日期
            Date lastDate=weekSwitcher.getEndDate();
            startDateString=weekSwitcher.getStartDateStr();
            endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
            MyLog.e(TAG, weekSwitcher.getSQLBetweenAnd());
            MyLog.e(TAG, "startDateString=" + startDateString + "endDateString=" + endDateString);
            AsyncTask myWeekAsync=new AsyncTask<Object,Object,Object>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(progressDialog!=null){
                        progressDialog.show();
                    }
                }
                @Override
                protected Object doInBackground(Object... params) {
                    MyLog.e(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindWeekData(StandDataActivity.this,weekSwitcher.getStartDate(),userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.e(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ViewUtils.initXAxis(StandDataActivity.this,combbarChart,0);
                    combbarChart.setData(generateWeekBarData());
                    combbarChart.invalidate();

                    setDataToView(position, listData);

                    if(progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (weekDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(weekDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(weekDataAsync = myWeekAsync);
            myWeekAsync.execute();
        }
        if(monthButton.isChecked()){
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int month=(year-1970)*12+s2+1;
            GregorianCalendar base2 = new GregorianCalendar();
            base2.setTime(new Date());
            base2.add(GregorianCalendar.MONTH, -(month - position-1));
            monthSwitcher.setBaseTime(base2.getTime());
            MyLog.e(TAG, "结束时间" + monthSwitcher.getEndDateStr());
            Date lastDate=monthSwitcher.getEndDate();
            startDateString=monthSwitcher.getStartDateStr();
            endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
            MyLog.e(TAG, monthSwitcher.getSQLBetweenAnd());
            MyLog.e(TAG, "startDateString=" + startDateString + "endDateString=" + endDateString);
            AsyncTask myMonthAsybc=new AsyncTask<Object,Object,Object>(){

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(progressDialog!=null){
                        progressDialog.show();
                    }
                }
                @Override
                protected Object doInBackground(Object... params) {
                    MyLog.e(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindMonthData(StandDataActivity.this,monthSwitcher.getStartDate(),userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.e(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ViewUtils.initXAxis(StandDataActivity.this,combbarChart,0);
                    combbarChart.setData(generateMonthBarData(monthSwitcher.getStartDateStr()));
                    combbarChart.invalidate();

                    setDataToView(position, listData);

                    if(progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (monthDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(monthDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(monthDataAsync = myMonthAsybc);
            myMonthAsybc.execute();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sleep_page_activity_circleviews_dayRb:
                if(dayButton.isChecked()){
                    chart_hour_layout.setVisibility(View.VISIBLE);
                    initTitle();
                }
                break;
            case R.id.sleep_page_activity_circleviews_weekRb:
                if(weekButton.isChecked()){
                    chart_hour_layout.setVisibility(View.GONE);
                    initTitle();
                }
                break;
            case R.id.sleep_page_activity_circleviews_monthRb:
                if(monthButton.isChecked()){
                    chart_hour_layout.setVisibility(View.GONE);
                    initTitle();
                }
                break;
        }

    }

    /**
     * 构建X坐标系
     * @return
     */
    private List<String> creatXxis(String startTime,String endTime) {
        MyLog.e(TAG, "creatXxis startTime:" + startTime);
        MyLog.e(TAG, "creatXxis endTime:" + endTime);
        //横坐标标签
        List<String> xVals = new ArrayList<String>();
        if(hourSelectPosition == Datahelper.TYPE_3H){
            //构建X轴的坐标值
            List<String>  xList = ViewUtils.creat3HListXxis(startTime);  //7个
            //查看天时
            for (int i = 0; i < 36; i++) {
                switch (i) {
                    case 0:
                        xVals.add(xList.get(0));
                        break;
                    case 6:
                        xVals.add(xList.get(1));
                        break;
                    case 12:
                        xVals.add(xList.get(2));
                        break;
                    case 18:
                        xVals.add(xList.get(3));
                        break;
                    case 24:
                        xVals.add(xList.get(4));
                        break;
                    case 30:
                        xVals.add(xList.get(5));
                        break;
                    case 35:
                        xVals.add(xList.get(6));
                        break;
                    default:
                        xVals.add(" ");
                        break;
                }
            }
        }else if(hourSelectPosition == Datahelper.TYPE_8H){
            //构建X轴的坐标值
            List<String>  xList = ViewUtils.creat8HListXxis(startTime);  //9个
            //查看天时
            for (int i = 0; i < 32; i++) {
                switch (i) {
                    case 0:
                        xVals.add(xList.get(0));
                        break;
                    case 4:
                        xVals.add(xList.get(1));
                        break;
                    case 8:
                        xVals.add(xList.get(2));
                        break;
                    case 12:
                        xVals.add(xList.get(3));
                        break;
                    case 16:
                        xVals.add(xList.get(4));
                        break;
                    case 20:
                        xVals.add(xList.get(5));
                        break;
                    case 24:
                        xVals.add(xList.get(6));
                        break;
                    case 28:
                        xVals.add(xList.get(7));
                        break;
                    case 31:
                        xVals.add(xList.get(8));
                        break;
                    default:
                        xVals.add(" ");
                        break;
                }
            }

        }else if(hourSelectPosition == Datahelper.TYPE_24H){
            //查看天时
            for (int i = 0; i < 32; i++) {
                switch (i) {
                    case 0:
                        xVals.add("0am");
                        break;
                    case 8:
                        xVals.add("6am");
                        break;
                    case 16:
                        xVals.add("12pm");
                        break;
                    case 24:
                        xVals.add("6pm");
                        break;
                    case 31:
                        xVals.add("12am");
                        break;
                    default:
                        xVals.add(" ");
                        break;
                }
            }
        }
        return xVals;

    }
}
