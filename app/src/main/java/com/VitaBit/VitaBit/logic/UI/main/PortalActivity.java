package com.VitaBit.VitaBit.logic.UI.main;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.ILifecycle;
import com.VitaBit.VitaBit.R;
import com.VitaBit.VitaBit.db.sport.UserDeviceRecord;
import com.VitaBit.VitaBit.db.summary.DaySynopicTable;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.http.data.DataFromServer;
import com.VitaBit.VitaBit.logic.UI.device.DeviceActivity;
import com.VitaBit.VitaBit.logic.UI.device.FirmwareDTO;
import com.VitaBit.VitaBit.logic.UI.main.bundband.BandListActivity;
import com.VitaBit.VitaBit.logic.UI.main.bundband.BandListActivity3;
import com.VitaBit.VitaBit.logic.UI.main.bundband.bundbandstep1;
import com.VitaBit.VitaBit.logic.UI.main.materialmenu.MenuNewAdapter;
import com.VitaBit.VitaBit.logic.UI.main.update.UpdateClientAsyncTask;
import com.VitaBit.VitaBit.logic.dto.Profile;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.prefrences.PreferencesToolkits;
import com.VitaBit.VitaBit.prefrences.devicebean.LocalInfoVO;
import com.VitaBit.VitaBit.utils.PermissionUtil;
import com.VitaBit.VitaBit.utils.ScreenUtils;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.VitaBit.VitaBit.utils.manager.AsyncTaskManger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jaeger.library.StatusBarUtil;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.VitaBit.VitaBit.BleService;
import com.VitaBit.VitaBit.IntentFactory;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.basic.AppManager;
import com.VitaBit.VitaBit.basic.CustomProgressBar;
import com.VitaBit.VitaBit.http.HttpHelper;
import com.VitaBit.VitaBit.logic.UI.main.boundwatch.BoundActivity;
import com.VitaBit.VitaBit.logic.UI.main.materialmenu.Left_viewVO;
import com.VitaBit.VitaBit.logic.UI.main.materialmenu.MenuVO;
import com.VitaBit.VitaBit.logic.UI.more.MoreActivity;
import com.VitaBit.VitaBit.utils.CommonUtils;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.VitaBit.VitaBit.utils.sportUtils.SportDataHelper;
import com.VitaBit.VitaBit.utils.sportUtils.TimeUtils;
import com.linkloving.utils.TimeZoneHelper;
import com.linkloving.utils._Utils;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.rest.Response;
import com.zhy.autolayout.AutoLayoutActivity;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PortalActivity extends AutoLayoutActivity implements MenuNewAdapter.OnRecyclerViewListener {

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
    private SimpleDateFormat sdfHMS = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
    private static final String TAG = PortalActivity.class.getSimpleName();
    private static final int REQUSET_FOR_PERSONAL = 1;
    private static final int LOW_BATTERY = 1;
    private static final int JUMP_FRIEND_TAG_TWO = 2;
    ViewGroup contentLayout;
    @InjectView(com.VitaBit.VitaBit.R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(com.VitaBit.VitaBit.R.id.toolbar)
    Toolbar toolbar;
    @InjectView(com.VitaBit.VitaBit.R.id.recycler_view)
    RecyclerView menu_RecyclerView;


    @InjectView(com.VitaBit.VitaBit.R.id.user_head_layout)
    CircleClipLayout user_head_layout;
    @InjectView(com.VitaBit.VitaBit.R.id.user_head)
    ImageView user_head;//头像
    @InjectView(com.VitaBit.VitaBit.R.id.portal_name_view)
    TextView user_head_name; //头像中的文字

    @InjectView(com.VitaBit.VitaBit.R.id.user_name)
    TextView user_name;//昵称
//    @InjectView(R.id.logout) LinearLayout logout;//登出

    @InjectView(com.VitaBit.VitaBit.R.id.device_img)
    ImageView device_img; //电量图片
    @InjectView(com.VitaBit.VitaBit.R.id.step_img)
    ImageView step_img;     //步数图片
    @InjectView(com.VitaBit.VitaBit.R.id.sit_img)
    ImageView sit_img;       //坐下
    @InjectView(com.VitaBit.VitaBit.R.id.stand_img)
    ImageView stand_img;   //站立

    @InjectView(com.VitaBit.VitaBit.R.id.text_battery)
    TextView text_Battery;
    @InjectView(com.VitaBit.VitaBit.R.id.text_step)
    TextView text_Step;
    @InjectView(com.VitaBit.VitaBit.R.id.text_sit)
    TextView text_Sit;
    @InjectView(com.VitaBit.VitaBit.R.id.text_stand)
    TextView text_stand;
    @InjectView(com.VitaBit.VitaBit.R.id.text_sleep)
    TextView text_Sleep;
    /**
     * 进度条系列
     */
    @InjectView(com.VitaBit.VitaBit.R.id.progressBar_battery)
    CustomProgressBar Battery_ProgressBar;
    @InjectView(com.VitaBit.VitaBit.R.id.progressBar_step)
    CustomProgressBar Step_ProgressBar;
    @InjectView(com.VitaBit.VitaBit.R.id.progressBar_sit)
    CustomProgressBar Sit_ProgressBar;
    @InjectView(com.VitaBit.VitaBit.R.id.progressBar_stand)
    CustomProgressBar stand_ProgressBar;
    @InjectView(com.VitaBit.VitaBit.R.id.progressBar_sleep)
    CustomProgressBar Sleep_ProgressBar;
/**
 * 三个右按钮
 */
    @InjectView(R.id.stepNext)
    Button stepnext ;
    @InjectView(R.id.sitNext)
    Button sitNext ;
    @InjectView(R.id.standNext)
    Button standNext ;

    /**
     * ITEM布局系列
     */
    @InjectView(com.VitaBit.VitaBit.R.id.linear_date)
    LinearLayout date;

    @InjectView(com.VitaBit.VitaBit.R.id.layout_bund)
    RelativeLayout layout_bund;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_unbund)
    LinearLayout linear_unbund;
    @InjectView(com.VitaBit.VitaBit.R.id.add_device)
    ImageView add_device;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_step)
    LinearLayout linear_Step;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_sit)
    LinearLayout linear_Distance;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_stand)
    LinearLayout linear_Stand;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_sleep)
    LinearLayout linear_Sleep;
    @InjectView(com.VitaBit.VitaBit.R.id.linear_battery)
    LinearLayout linear_Battery;
    @InjectView(com.VitaBit.VitaBit.R.id.text_time)
    TextView time;
    //下拉刷新
    @InjectView(com.VitaBit.VitaBit.R.id.mainScrollView)
    PullToRefreshScrollView mScrollView;
    @InjectView(com.VitaBit.VitaBit.R.id.nav_headView)
    LinearLayout nav_headView;
    //修改日期左右的按钮
    @InjectView(com.VitaBit.VitaBit.R.id.leftBtn)
    Button btnleft;
    @InjectView(com.VitaBit.VitaBit.R.id.rightBtn)
    Button btnright;
    private MenuNewAdapter menuAdapter;
    private ProgressDialog progressDialog;
    private String User_avatar_file_name;
    private UserEntity userEntity;
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    private BLEProvider provider;
    //获取钱包详情专用
    private LPDeviceInfo deviceInfo = new LPDeviceInfo();
    String startDateString;
    String endDateString;
    String timeNow;
    int COUNT_MODELNAME = 0;   //防止用户多次强制进入设备页面
    int PROGRESS_DEFULT = 80;  //主界面变化颜色进度分界线
    boolean isRunning = false;
    boolean isReadCard = false; //防止主界面没读卡完毕用户就点击金额导致读取失败
    //目标值
    private float step_goal, sit_goal, stand_goal;
    private float sleeptime_goal, weight_goal;

    /**
     * 下拉同步ui的超时复位延迟执行handler （防止意外情况下，一直处于“同步中”的状态）
     */
    private Handler mScrollViewRefreshingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mScrollView.isRefreshing())
                mScrollView.onRefreshComplete();
            super.handleMessage(msg);
        }
    };

    /**
     * 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM)
     */
    private AsyncTask<Object, Object, DaySynopic> currentDataAsync = null;
    private int stepTotal;

    @Override
    protected void onPause() {
        super.onPause();
        if (provider.getBleProviderObserver() != null) {
            provider.setBleProviderObserver(null);
        }
//        if(!ToolKits.isAppOnForeground(this)){
//            isRunning = false;
//        }
    }


    @Override
    protected void onPostResume() {
        MyLog.e(TAG, "onPostResume()了");
        super.onPostResume();
        isRunning = true;
        provider = BleService.getInstance(PortalActivity.this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(bleProviderObserver);
        if (!PermissionUtil.checkPermission(this, PermissionUtil.PERMISSIONS_BLE)) {
            MyLog.e(TAG, "权限未打开");
            requestPermissions(PermissionUtil.PERMISSIONS_BLE, PermissionUtil.REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            MyLog.e(TAG, "权限已经打开");
        }
        //判断下要隐藏哪些
        refreshVISIBLE();
        //运动目标重置一下
        initGoal();
        getInfoFromDB();
        setSyncTimeFromLocaVo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        provider.setBleProviderObserver(null);
        AppManager.getAppManager().removeActivity(this);
        // 如果有未执行完成的AsyncTask则强制退出之，否则线程执行时会空指针异常哦！！！
        AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        检查蓝牙权限
        checkBle();
        setContentView(com.VitaBit.VitaBit.R.layout.activity_portal_main);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        contentLayout = (ViewGroup) findViewById(com.VitaBit.VitaBit.R.id.main);
        //主界面开始运行
        isRunning = true;
        COUNT_MODELNAME = 0;
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        // 系统到本界面中，应该已经完成准备好了，开启在网络连上事件时自动启动同步线程的开关吧
        MyApplication.getInstance(this).setPreparedForOfflineSyncToServer(true);
        //初始化目标
//        initGoal();
        //初始化UI
        initView();
        initListener();
        // 刷新电量UI
        refreshBatteryUI();
        // 刷新头像
        /**通过token去获取个人信息*/
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_BOUND, HttpHelper.getProfile(userEntity.getUserBase().getThirdparty_access_token()), httpCallback);
        //开始时间
        startDateString = TimeUtils.getstartDateTime(0, new Date());
        //结束时间
        endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());

        //自动下拉刷新
        mScrollView.autoRefresh();
        mScrollViewRefreshingHandler.post(new Runnable() {
            @Override
            public void run() {
                Message ms = new Message();
                mScrollViewRefreshingHandler.sendMessageDelayed(ms, 10000);
            }
        });

        MyApplication.getInstance(this).setILifecycle(new ILifecycle() {
            @Override
            public void back2Foreground() {
                if (!CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getLast_sync_device_id())) {
                    BleService.getInstance(PortalActivity.this).syncAllDeviceInfo(PortalActivity.this);
                }
            }
        });
//        initCheckUpdate();
    }

    private void initCheckUpdate() {
        MyLog.e(TAG,"initCheckUpdate执行了");
        if (ToolKits.isStoreVersion(PortalActivity.this)){
            MyLog.e(TAG,"initCheckUpdate执行了if");
            MyLog.e(TAG,"initCheckUpdate"+ToolKits.isStoreVersion(PortalActivity.this));
        }else {
            MyLog.e(TAG,"initCheckUpdate执行了else");
            new UpdateClientAsyncTask(PortalActivity.this){

                @Override
                protected void relogin() {

                }
            }.execute();
        }

    }

    private void checkBle() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(com.VitaBit.VitaBit.R.string.general_loading));
        timeNow = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
        time.setText(timeNow);
        toolbar.setBackgroundColor(getResources().getColor(com.VitaBit.VitaBit.R.color.yellow_title));
        Rect outRect = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        setSupportActionBar(toolbar);
        //隐藏title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menu_RecyclerView.setLayoutManager(layoutManager);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, com.VitaBit.VitaBit.R.string.navigation_drawer_open, com.VitaBit.VitaBit.R.string.navigation_drawer_close);
        drawer.setDrawerShadow(com.VitaBit.VitaBit.R.drawable.drawer_shadow, GravityCompat.END);
        //无阴影
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        contentLayout.setBackgroundColor(getResources().getColor(com.VitaBit.VitaBit.R.color.yellow_title));
        StatusBarUtil.setTranslucentForDrawerLayout(this, drawer, 0);
        ScreenUtils.setMargins(toolbar, 0, ScreenUtils.getStatusHeight(this), 0, 0);

        //侧边栏适配器
        setAdapter();
        refreshHeadView();
    }

    private void initListener() {
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //刷新同步数据
                String s = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id();
                if (CommonUtils.isStringEmpty(s)) {
                    //
//                    showBundDialog();
                    mScrollView.onRefreshComplete();
                } else {
                    // 启动超时处理handler
                    // 进入扫描和连接处理过程
                    provider.setCurrentDeviceMac(s);
                    //开始同步
                    BleService.getInstance(PortalActivity.this).syncAllDeviceInfoAuto(PortalActivity.this, false, null);
                    // 启动超时处理handler
//                    mScrollView.post(new Runnable() {
//                        @Override
//                        public void run()
//                            Message ms = new Message();
//                            mScrollView.sendMessageDelayed(ms, 10000);
//                        }
//                    });
                }
            }
        });

        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                点击按钮 时间往后一天  刷新界面
                timeNow = ToolKits.getSpecifiedDayBefore(timeNow);
                time.setText(timeNow);
                Date date = ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
                startDateString = TimeUtils.getstartDateTime(0, date);
                endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                //查询数据
                getInfoFromDB();
            }
        });

        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮  弹框提示明天还未来临
                if (timeNow.equals(sdf.format(new Date()))) {
                    ToolKits.showCommonTosat(PortalActivity.this, false, ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.ranking_wait_tomorrow), Toast.LENGTH_SHORT);
//                    Toast.makeText(PortalActivity.this, getString(R.string.ranking_wait_tomorrow), Toast.LENGTH_LONG).show();
                } else {
                    timeNow = ToolKits.getSpecifiedDayAfter(timeNow);
                    time.setText(timeNow);
                    Date date = ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
                    startDateString = TimeUtils.getstartDateTime(0, date);
                    endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                    //查询数据
                    getInfoFromDB();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar;// 用来装日期的
                calendar = Calendar.getInstance();
                DatePickerDialog dialog1;
                dialog1 = new DatePickerDialog(PortalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = new Date(calendar.getTimeInMillis());
                        endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                        if (ToolKits.compareDate(date, new Date())) {
                            ToolKits.showCommonTosat(PortalActivity.this, false, ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.ranking_wait_tomorrow), Toast.LENGTH_SHORT);
                        } else {
                            time.setText(endDateString);
                            timeNow = endDateString;
                            MyLog.e(TAG, "riqi" + startDateString + "   " + endDateString);
                            getInfoFromDB();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog1.show();
            }
        });

        user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人信息.详细页面
                Intent intent = IntentFactory.create_PersonalInfo_Activity_Intent(PortalActivity.this);
                startActivityForResult(intent, REQUSET_FOR_PERSONAL);
            }
        });
    }

    //获取目标值
    private void initGoal() {
        MyLog.i(TAG, "========正在初始化目标=======");
        String date8before = TimeUtils.getstartDateTime(-8, new Date());
        String DateNow = TimeUtils.getstartDateTime(-1, new Date());
        ArrayList<DaySynopic> mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(PortalActivity.this, userEntity.getUser_id() + "", date8before, DateNow, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
        if (mDaySynopicArrayList == null || mDaySynopicArrayList.size() == 0) {
            MyLog.e(TAG, "mDaySynopicArrayList都是null");
            step_goal = 0;
            sit_goal = 0;
            stand_goal = 0;
        } else {
            int daySynopicSize = mDaySynopicArrayList.size();
            MyLog.e(TAG, "daySynopicSize:" + daySynopicSize);
            for (DaySynopic mDaySynopic : mDaySynopicArrayList) {
                MyLog.e(TAG, "mDaySynopic:" + mDaySynopic);
            }
            double workSum = 0, sitSum = 0, standSum = 0;
            for (int i = 0; i < mDaySynopicArrayList.size(); i++) {
                //走路 分钟
                double walktime, runtime;
                if (CommonUtils.isStringEmpty(mDaySynopicArrayList.get(i).getWork_duration())) {
                    walktime = 0;
                } else {
                    walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopicArrayList.get(i).getWork_duration()), 1);
                }

                if (CommonUtils.isStringEmpty(mDaySynopicArrayList.get(i).getRun_duration())) {
                    runtime = 0;
                } else {
                    runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopicArrayList.get(i).getRun_duration()), 1);
                }
                double worktime = walktime + runtime;

                workSum += worktime;

                double sittime;
                if (CommonUtils.isStringEmpty(mDaySynopicArrayList.get(i).getSitTime())) {
                    sittime = 0;
                } else {
                    sittime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopicArrayList.get(i).getSitTime()), 1);
                }
                sitSum += sittime;

                double standtime;
                if (CommonUtils.isStringEmpty(mDaySynopicArrayList.get(i).getStandTime())) {
                    standtime = 0;
                } else {
                    standtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopicArrayList.get(i).getStandTime()), 1);
                }
                standSum += standtime;
            }
            step_goal = (float) (workSum / daySynopicSize);
            sit_goal = (float) (sitSum / daySynopicSize);
            stand_goal = (float) (standSum / daySynopicSize);

            MyLog.e(TAG, "step_goal:" + step_goal);
            MyLog.e(TAG, "sit_goal:" + sit_goal);
            MyLog.e(TAG, "stand_goal:" + stand_goal);
        }
    }

    private void refreshVISIBLE() {
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        linear_Sleep.setVisibility(View.GONE); //荷兰版本暂时没有睡眠
        if (CommonUtils.isStringEmpty(userEntity.getUserBase().getThirdparty_access_token())) {
            nav_headView.setVisibility(View.GONE);
            //侧边栏适配器
            changeAdaper();
        } else {
            nav_headView.setVisibility(View.VISIBLE);
            //token不是null就去验证
            //侧边栏适配器
            changeAdaper();
        }

        if (CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            linear_unbund.setVisibility(View.VISIBLE); //未绑定提示出现
            layout_bund.setVisibility(View.GONE);
            BleService.getInstance(this).releaseBLE();
        } else {
            //刷新电量
            refreshBatteryUI();
            //绑定时候
            linear_unbund.setVisibility(View.GONE);   //未绑定提示消失
            layout_bund.setVisibility(View.VISIBLE);
            //手环的时候显示手环 手表显示手表
            MyLog.e(TAG, "获取modelName：" + userEntity.getDeviceEntity().getModel_name());
        }
    }




    @OnClick(com.VitaBit.VitaBit.R.id.add_device)
    void unBund(View view) {
        if (MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider() == null ||
                CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            Intent intent = new Intent(PortalActivity.this, bundbandstep1.class);
            startActivityForResult(intent,CommParams.REQUEST_CODE_BOUND_BAND);
//            startActivityForResult(IntentFactory.startActivityBandList(PortalActivity.this), CommParams.REQUEST_CODE_BOUND_BAND);
//            如果要两种方式绑定,就把下面这个打开判断modelname.
//      chooseBundDevice();
        }
    }

    private void chooseBundDevice() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PortalActivity.this);
        builder.setItems(getResources().getStringArray(R.array.ItemArray), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(PortalActivity.this, BandListActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(PortalActivity.this, BandListActivity3.class);
                        startActivity(intent1);
                        break;
                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.stepNext)
    void setStepnext(View view){
        toStep(view);
    }


    @OnClick(com.VitaBit.VitaBit.R.id.linear_step)
    void toStep(View view) {
        Intent intent1 = IntentFactory.cteate_StepDataActivityIntent(PortalActivity.this);
        intent1.putExtra("time", time.getText());
        startActivity(intent1);
    }
    @OnClick(R.id.sitNext)
    void setSitnext(View view){
        toDistance(view);
    }

    @OnClick(com.VitaBit.VitaBit.R.id.linear_sit)
    void toDistance(View view) {
        Intent intent = IntentFactory.cteate_SitActivityIntent(PortalActivity.this);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }
    @OnClick(R.id.standNext)
    void setStandnext(View view){
        toCal(view);
    }

    @OnClick(com.VitaBit.VitaBit.R.id.linear_stand)
    void toCal(View view) {
        Intent intent = IntentFactory.cteate_StandActivityIntent(PortalActivity.this);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }

    @OnClick(com.VitaBit.VitaBit.R.id.linear_sleep)
    void toSleep(View view) {
//        Intent intent = IntentFactory.cteate_SleepDataActivityIntent(PortalActivity.this);
//        intent.putExtra("time", time.getText());
//        startActivity(intent);
    }


    @OnClick(com.VitaBit.VitaBit.R.id.linear_battery)
    void toBattery(View view) {
        userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (userEntity == null || CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            showBundDialog();
        } else {
            startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, 0));
        }
    }

    /**
     * 刷新用户头像和昵称
     */
    private void refreshHeadView() {
        MyLog.i(TAG, "刷新头像和昵称,等数据");
        //图像以后设置
        UserEntity u = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (u == null) {
            MyLog.i(TAG, "获得的UserEntity是空的");
            return;
        }
        MyLog.e(TAG, "获得的UserEntity的名字=" + u.getUserBase().getFirst_name() + u.getUserBase().getLast_name());
        MyLog.e(TAG, "获得的UserEntity的颜色=" + u.getUserBase().getAvatar_color());
        user_name.setText(u.getUserBase().getNickname());
//        Bitmap bitmap = Bitmap.createBitmap(220,220,Bitmap.Config.ARGB_8888);
        user_head_layout.setM_borderColor(android.graphics.Color.parseColor(u.getUserBase().getAvatar_color()));
        user_head.setBackgroundColor(android.graphics.Color.parseColor(u.getUserBase().getAvatar_color()));
        String first_name = u.getUserBase().getFirst_name().substring(0, 1);
        String last_name = u.getUserBase().getLast_name().substring(0, 1);
        user_head_name.setText(first_name + last_name);
    }

    /**
     * 侧滑栏适配器
     */
    private void setAdapter() {
        List<MenuVO> list = new ArrayList<MenuVO>();
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (CommonUtils.isStringEmpty(userEntity.getUserBase().getThirdparty_access_token())) {
            for (int i = 0; i < Left_viewVO.noLogin_menuIcon.length; i++) {
                MenuVO vo = new MenuVO();
                vo.setImgID(Left_viewVO.noLogin_menuIcon[i]);
                vo.setTextID(Left_viewVO.noLogin_menuText[i]);
                list.add(vo);
            }

        } else {
            for (int i = 0; i < Left_viewVO.menuIcon.length; i++) {
                MenuVO vo = new MenuVO();
                vo.setImgID(Left_viewVO.menuIcon[i]);
                vo.setTextID(Left_viewVO.menuText[i]);
                list.add(vo);
            }
        }
        menuAdapter = new MenuNewAdapter(this, list);
        menuAdapter.setOnRecyclerViewListener(this);
        menu_RecyclerView.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
    }

    private void changeAdaper() {
        List<MenuVO> list = new ArrayList<MenuVO>();
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (CommonUtils.isStringEmpty(userEntity.getUserBase().getThirdparty_access_token())) {
            for (int i = 0; i < Left_viewVO.noLogin_menuIcon.length; i++) {
                MenuVO vo = new MenuVO();
                vo.setImgID(Left_viewVO.noLogin_menuIcon[i]);
                vo.setTextID(Left_viewVO.noLogin_menuText[i]);
                list.add(vo);
            }

        } else {
            for (int i = 0; i < Left_viewVO.menuIcon.length; i++) {
                MenuVO vo = new MenuVO();
                vo.setImgID(Left_viewVO.menuIcon[i]);
                vo.setTextID(Left_viewVO.menuText[i]);
                list.add(vo);
            }
        }
        menuAdapter.setList(list);
        menu_RecyclerView.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.VitaBit.VitaBit.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MoreActivity.ExitApp(this);
        }
    }


    //侧边栏点击事件 可以在这里复写 暂时没用到
    @Override
    public void onItemClick(int position) {
//        if (position==0){  //只负责去连接Vitabit
//            UserEntity userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
//            if(CommonUtils.isStringEmpty(userEntity.getUserBase().getThirdparty_access_token())){
//                /**去获取code*/
//                CallServer.getRequestInstance().add(PortalActivity.this, true, CommParams.HTTP_BOUND, HttpHelper.getCode(userEntity.getUser_id()+""), new HttpCallback<String>() {
//                    @Override
//                    public void onSucceed(int what, Response<String> response) {
//                        MyLog.e(TAG, "CODE response:"+response.get());
//                        JSONObject json = JSON.parseObject(response.get());
//                        final String code = json.getString("code");
//                        MyLog.e(TAG, "CODE response:"+code);
//                        //获取完code让用户去输入
//                        LayoutInflater factory = LayoutInflater.from(PortalActivity.this);//提示框
//                        final View view = factory.inflate(R.layout.edittext_pin,null);//这里必须是final的
//                        final EditText edit=(EditText)view.findViewById(R.id.editText);//获得输入框对象
//                        //默认输入pin
//                        edit.setText(code);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(PortalActivity.this);
//                        builder.setTitle(getString(R.string.pin_title))
//                                .setMessage(getString(R.string.pin_message))
//                                .setView(view)
//                                .setNegativeButton("Cancel", null)
//                                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        MyLog.e(TAG, "输入PIN："+edit.getText().toString()); //这后面去做处理连接Vitable的操作
//                                    }
//                                });
//                        builder.show();
//
//                    }
//                    @Override
//                    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
//                        MyLog.e(TAG, what+"CODE onFailed:"+message);
//                    }
//                });
//            }
//        }

    }

    //刷新手表电量  + 获取存在数据库的数据
    private void getInfoFromDB() {
        //子线程去计算汇总数据
        MyLog.e(TAG, "====================开始执行异步任务====================");
        AsyncTask<Object, Object, DaySynopic> dataasyncTask = new AsyncTask<Object, Object, DaySynopic>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                if (progressDialog != null && !progressDialog.isShowing())
//                    progressDialog.show();
            }

            @Override
            protected DaySynopic doInBackground(Object... params) {
                DaySynopic mDaySynopic = null;
                if (timeNow.equals(sdf.format(new Date()))) {
                    ArrayList<DaySynopic> mDaySynopicArrayList = new ArrayList<>();
                    MyLog.e(TAG, "endDateString:" + endDateString);
                    //今天的话 无条件去汇总查询
                    mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(PortalActivity.this, endDateString, endDateString);
                    if (mDaySynopic.getTime_zone() == null) {
                        return null;
                    }
                    MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                    mDaySynopicArrayList.add(mDaySynopic);
                    DaySynopicTable.saveToSqliteAsync(PortalActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                    /****************今天的步数给到 方便OAD完成后回填步数 的变量里面去****************/
                    //今天的步数给到 方便OAD完成后回填步数 的变量里面去
                    //走路 步数
                    int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_step()), 0));
                    //跑步 步数
                    int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_step()), 0));
                    int step = walkStep + runStep;
                    MyApplication.getInstance(PortalActivity.this).setOld_step(step);
                    /****************今天的步数给到 方便OAD完成后回填步数 的变量里面去****************/
                } else {
                    ArrayList<DaySynopic> mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(PortalActivity.this, userEntity.getUser_id() + "", endDateString, endDateString, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                    MyLog.e(TAG, "mDaySynopicArrayList:" + mDaySynopicArrayList.toString());
                    //在判断一次,如果得到集合是空,我就去明细表里去查询数据.进行汇总
                    if (mDaySynopicArrayList == null || mDaySynopicArrayList.size() == 0) {
                        mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(PortalActivity.this, endDateString, endDateString);
                        MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                        DaySynopicTable.saveToSqliteAsync(PortalActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                    } else {
                        mDaySynopic = mDaySynopicArrayList.get(0);
                    }
                }
                return mDaySynopic;
            }

            @Override
            protected void onPostExecute(DaySynopic mDaySynopic) {
                super.onPostExecute(mDaySynopic);
                //=============计算基础卡路里=====START========//
                int cal_base = 0;
                if (timeNow.equals(sdf.format(new Date()))) {
                    int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
                    int minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
                    cal_base = (int) ((hour * 60 + minute) * 1.15);//当前时间今天的卡路里
                } else {
                    cal_base = 1656;
                }
//              //=============计算基础卡路里=====OVER========//
                if (mDaySynopic == null) {
                    MyLog.e(TAG, "mDaySynopic空的");
                    refreshView(0, 0, 0);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                    return;
                }
                //走路 步数
                int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_step()), 0));
                //跑步 步数
                int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_step()), 0));
                stepTotal = walkStep + runStep;
                //daySynopic:[data_date=2016-04-14,data_date2=null,time_zone=480,record_id=null,user_id=null,run_duration=1.0,run_step=68.0,run_distance=98.0
                // ,create_time=null,work_duration=178.0,work_step=6965.0,work_distance=5074.0,sleepMinute=2.0916666984558105,deepSleepMiute=1.25 gotoBedTime=1460645100 getupTime=1460657160]
                //走路 里程
                int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance = walkDistance + runDistance;

                //浅睡 小时
                double qianleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getSleepMinute()), 1);
                //深睡 小时
                double deepleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getDeepSleepMiute()), 1);


                double sleeptime = CommonUtils.getScaledDoubleValue(qianleephour + deepleephour, 1);
                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);

                double worktime = CommonUtils.getScaledDoubleValue(walktime + runtime, 1);
                double sittime;
                if (CommonUtils.isStringEmpty(mDaySynopic.getSitTime())) {
                    sittime = 0;
                } else {
                    sittime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getSitTime()), 1);
                }

                double standtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getStandTime()), 1);


                int runcal = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());

                int walkcal = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());

                MyLog.e(TAG, "runcal:" + runcal);
                MyLog.e(TAG, "walkcal:" + walkcal);
                int calValue = runcal + walkcal;
                // 计算卡路里

//                Log.i(TAG,"卡路里"+calValue+"体重="+userEntity.getUserBase().getUser_weight()+"count="+count);
//                //将数据显示在控件上
                refreshView(worktime, sittime, standtime);

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
            }
        };
        // 确保当前只有一个AsyncTask在运行，否则用户恶心切换会OOM
        if (currentDataAsync != null)
            AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(currentDataAsync, true);

        AsyncTaskManger.getAsyncTaskManger().addAsyncTask(currentDataAsync = dataasyncTask);
        dataasyncTask.execute();
    }

    /**
     * 刷新条目明细 （数值，进度条，百分比）
     *
     * @param step
     * @param sittime
     * @param standtime
     */
    private void refreshView(double step, double sittime, double standtime) {
        MyLog.e(TAG, step + " " + sittime + " " + standtime);
        text_Step.setText(step + getResources().getString(com.VitaBit.VitaBit.R.string.space) + getResources().getString(com.VitaBit.VitaBit.R.string.unit_min)+"/ "+stepTotal+" steps");
        text_Sit.setText(sittime + getResources().getString(com.VitaBit.VitaBit.R.string.space) + getResources().getString(com.VitaBit.VitaBit.R.string.unit_min));
        //  ext_Cal= (TextView) findViewById(R.id.text_cal);
        text_stand.setText(standtime + getResources().getString(com.VitaBit.VitaBit.R.string.space) + getResources().getString(com.VitaBit.VitaBit.R.string.unit_min));
        int step_percent;
        if (step_goal == 0) {
            step_percent = 0;
        } else {
            step_percent = (int) Math.floor(step * 100 * 1.0f / step_goal);
        }
        //进度条
        Step_ProgressBar.setCurProgress(step_percent);
        if (step_percent > PROGRESS_DEFULT) { //进度超过90%就显示绿色
            step_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_steps_full);
//            text_Step.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
            Step_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
        } else {
            step_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_step);
            text_Step.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
            Step_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
        }
        int sit_percent;
        if (sit_goal == 0) {
            sit_percent = 0;
        } else {
            sit_percent = (int) Math.floor(sittime * 100 * 1.0f / sit_goal);
        }
        Sit_ProgressBar.setCurProgress(sit_percent);
        if (sit_percent == 100) {
            sit_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_sit_full);
//            text_Sit.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
            Sit_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.orangered));
        } else if (sit_percent > PROGRESS_DEFULT) {
            sit_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_sit_full);
//            text_Sit.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
            Sit_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
        } else {
            sit_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_sit);
            text_Sit.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
            Sit_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
        }
        int stand_percent;
        if (stand_goal == 0) {
            stand_percent = 0;
        } else {
            stand_percent = (int) Math.floor(standtime * 100 * 1.0f / stand_goal);
        }
        stand_ProgressBar.setCurProgress(stand_percent);
        if (stand_percent > PROGRESS_DEFULT) {
            stand_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_stand_full);
//            text_stand.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
            stand_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
        } else {
            stand_img.setImageResource(com.VitaBit.VitaBit.R.mipmap.main_stand);
            text_stand.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
            stand_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.add_device_blue));
        }
        //修改颜色和图片
        refreshBatteryUI();
    }


    /**
     * 单独刷新电量
     */
    private void refreshBatteryUI() {
        if (!CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            if (provider.isConnectedAndDiscovered()) {  //蓝牙连接上的
                LocalInfoVO LocalInfoVO = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                if (!LocalInfoVO.userId.equals("-1")) {
                    int battery = LocalInfoVO.getBattery();
                    MyLog.e(TAG, "LocalInfoVO电量:" + LocalInfoVO.getBattery());
                    if (battery < LOW_BATTERY) {
                        //电量低于30的时候 弹出低电量警告框
                        AlertDialog dialog_battery = new AlertDialog.Builder(PortalActivity.this)
                                .setTitle(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.portal_main_battery_low))
                                .setMessage(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.portal_main_battery_low_msg))
                                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog_battery.show();
                        device_img.setImageResource(R.mipmap.device_red);
                        text_Battery.setTextColor(Color.RED);
                        Battery_ProgressBar.setProgressColor(Color.RED);
                        text_Battery.setText(getString(com.VitaBit.VitaBit.R.string.portal_main_state_connected));//根据电量显示不同的文字提示
                    } else {
                        int battery_percent = (int) (Math.ceil(battery * 100 * 1.0f / 100));
                        device_img.setImageResource(R.mipmap.device_green);
                        text_Battery.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
                        Battery_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.battery_green));
                        text_Battery.setText(getString(com.VitaBit.VitaBit.R.string.portal_main_state_connected));//根据电量显示不同的文字提示
                        Battery_ProgressBar.setCurProgress(battery_percent);
                    }
//                    text_Battery_Progress.setText((int) (Math.ceil(battery * 100 * 1.0f / 100)) + "%");
                }
            } else if (provider.isConnecting()) { //正在连接
                refreshBattery(PortalActivity.this.getString(com.VitaBit.VitaBit.R.string.portal_main_state_connecting));

//                Battery_ProgressBar.setIndeterminate(true);
            } else {  //蓝牙未连接上
                refreshBattery(PortalActivity.this.getString(com.VitaBit.VitaBit.R.string.portal_main_state_unconnect));
//                Battery_ProgressBar.setIndeterminate(false);
            }
        } else {
            //未绑定
            refreshBattery(PortalActivity.this.getString(com.VitaBit.VitaBit.R.string.portal_main_state_connecting));
//            Battery_ProgressBar.setIndeterminate(true);
        }
    }

    /**
     * 单独刷新电量子方法
     *
     * @param msg
     */
    private void refreshBattery(String msg) {
        //提示词
        text_Battery.setText(msg + " ");//根据电量显示不同的文字提示
        LocalInfoVO LocalInfoVO = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
        if (!LocalInfoVO.userId.equals("-1")) {
            int battery = LocalInfoVO.getBattery();
            Battery_ProgressBar.setCurProgress((int) (Math.ceil(battery * 100 * 1.0f / 100)));
        } else {
            Battery_ProgressBar.setCurProgress((int) (Math.ceil(0 * 100 * 1.0f / 100)));
        }
        device_img.setImageResource(R.mipmap.device_gray);
        text_Battery.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.bg_gray));
        Battery_ProgressBar.setProgressColor(getResources().getColor(com.VitaBit.VitaBit.R.color.bg_gray));
    }

    /**
     * 网络请求的返回
     */
    HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            if (result == null) {
                return;
            }
            MyLog.e(TAG, "Profile response:" + response.get());
            Profile profile = new Gson().fromJson(response.get(), Profile.class);
            userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
            //设置昵称
            if (!CommonUtils.isStringEmpty(profile.getFirst_name()) || !CommonUtils.isStringEmpty(profile.getLast_name())) {
                userEntity.getUserBase().setFirst_name(profile.getFirst_name());
                userEntity.getUserBase().setLast_name(profile.getLast_name());
                userEntity.getUserBase().setNickname(profile.getFirst_name() + " " + profile.getLast_name());
                userEntity.getUserBase().setAvatar_color(profile.getAvatar_color());
            }

            //设置性别
            if (!CommonUtils.isStringEmpty(profile.getGender())) {
                if ("male".equals(profile.getGender())) {
                    userEntity.getUserBase().setUser_sex(1);
                } else {
                    userEntity.getUserBase().setUser_sex(0);
                }
            }
            if (!CommonUtils.isStringEmpty(profile.getHeight())) {
                userEntity.getUserBase().setUser_height((int) Float.parseFloat(profile.getHeight()));
            }

            if (!CommonUtils.isStringEmpty(profile.getDob())) {
                String[] date = profile.getDob().split("T");
                userEntity.getUserBase().setBirthdate(date[0]);
            }

            if (!CommonUtils.isStringEmpty(profile.getWeight())) {
                userEntity.getUserBase().setUser_weight((int) Float.parseFloat(profile.getWeight()));
            }
            refreshHeadView();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED: //用户取消打开蓝牙
                    break;

                case Activity.RESULT_OK:       //用户打开蓝牙
                    Log.e(TAG, "//用户打开蓝牙");
                    provider.scanForConnnecteAndDiscovery();
                    break;

                default:
                    break;
            }
            return;
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND && resultCode == Activity.RESULT_OK) {
            String type = data.getStringExtra(BundTypeActivity.KEY_TYPE);
            if (type.equals(BundTypeActivity.KEY_TYPE_WATCH)) {
                startActivityForResult(new Intent(PortalActivity.this, BoundActivity.class), CommParams.REQUEST_CODE_BOUND_WATCH);
            } else if (type.equals(BundTypeActivity.KEY_TYPE_BAND)) {
                startActivityForResult(IntentFactory.startActivityBundBand(PortalActivity.this), CommParams.REQUEST_CODE_BOUND_BAND);
            }
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND_BAND && resultCode == Activity.RESULT_OK) {
            MyLog.e(TAG, "手环绑定成功");
            BleService.getInstance(this).syncAllDeviceInfoAuto(this, false, null);
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND_WATCH && resultCode == Activity.RESULT_OK) {
        }
    }

    /**
     * 提示绑定的弹出框
     */
    private void showBundDialog() {
        AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                .setTitle(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.portal_main_unbound))
                .setMessage(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.portal_main_unbound_msg))
                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                IntentFactory.startBundTypeActivity(PortalActivity.this);
                                startActivityForResult(IntentFactory.startActivityBundBand(PortalActivity.this), CommParams.REQUEST_CODE_BOUND_BAND);
                            }
                        })
                .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_cancel), null)
                .create();
        dialog.show();
    }


    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {
        private int iObj = 0;
        private int firstInt = -1;

        @Override
        protected Activity getActivity() {
            return PortalActivity.this;
        }

        /**********
         * 用户没打开蓝牙
         *********/
        @Override
        public void updateFor_handleNotEnableMsg() {
            //用户未打开蓝牙
            Log.i(TAG, "updateFor_handleNotEnableMsg");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getActivity().startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleUserErrorMsg(int id) {
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }

        /**********
         * BLE连接中
         *********/
        @Override
        public void updateFor_handleConnecting() {
            //正在连接
            MyLog.e(TAG, "updateFor_handleConnecting");
            refreshBatteryUI();
        }

        /**********
         * 扫描BLE设备TimeOut
         *********/
        @Override
        public void updateFor_handleScanTimeOutMsg() {
            MyLog.e(TAG, "updateFor_handleScanTimeOutMsg");
            if (mScrollView.isRefreshing()) {
                mScrollView.onRefreshComplete();
            }
        }

        /**********
         * BLE连接失败
         *********/
        @Override
        public void updateFor_handleConnectFailedMsg() {
            //连接失败
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            if (mScrollView.isRefreshing()) {
                mScrollView.onRefreshComplete();
            }
        }

        /**********
         * BLE连接成功
         *********/
        @Override
        public void updateFor_handleConnectSuccessMsg() {
            //连接成功
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }

        /**********
         * BLE断开连接
         *********/
        @Override
        public void updateFor_handleConnectLostMsg() {
            MyLog.e(TAG, "updateFor_handleConnectLostMsg");
            //蓝牙断开的显示
            refreshBatteryUI();
            isReadCard = false;
            if (mScrollView.isRefreshing()) {
                mScrollView.onRefreshComplete();
            }
        }

        /**********
         * 0X13命令返回
         *********/
        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            MyLog.e(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            isReadCard = true;
            if (latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 5) {
                new android.app.AlertDialog.Builder(PortalActivity.this)
                        .setTitle(com.VitaBit.VitaBit.R.string.general_tip)
                        .setMessage(com.VitaBit.VitaBit.R.string.portal_main_mustbund)
                        .setPositiveButton(com.VitaBit.VitaBit.R.string.general_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BleService.getInstance(PortalActivity.this).releaseBLE();
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            MyLog.e(TAG, "updateFor_notifyForDeviceUnboundSucess_D");
        }

        /**********
         * 剩余同步运动条目
         *********/

        @Override
        public void updateFor_SportDataProcess(Integer obj) {
            super.updateFor_SportDataProcess(obj);
            MyLog.e(TAG, "updateFor_SportDataProcess");
            iObj++;
            int percent;
            if (iObj == 1) {
                firstInt = obj;
            }
            if (obj == 0) {
                percent = 100;
            } else {
                percent = (firstInt - obj) / obj;
            }
            MyLog.e(TAG, "fitstint=" + firstInt + "        obj=" + obj + "       int 1=" + iObj);
            if (mScrollView.isRefreshing()) {
                String second_txt = MessageFormat.format(getString(com.VitaBit.VitaBit.R.string.refresh_data), percent);
                MyLog.e(TAG, second_txt);
                mScrollView.getHeaderLayout().getmHeaderText().setText(second_txt);
            }
        }

        /**********
         * 运动记录读取完成
         *********/
        @Override
        public void updateFor_handleDataEnd() {
            MyLog.e(TAG, " updateFor_handleDataEnd ");
//            把读取运动数据条目初始化
            iObj = 0;
            firstInt = -1;
            //把数据库未同步到server的数据提交上去
            if (ToolKits.isNetworkConnected(PortalActivity.this)) {
                new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }
                    @Override
                    protected Object doInBackground(Object... params) {
                        // 看看数据库中有多少未同步（到服务端的数据）
                        final List<SportRecord> up_List = UserDeviceRecord.findHistoryWitchNoSync(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "");
                        MyLog.e(TAG, "【NEW离线数据同步】一共查询出" + up_List.size() + "条数据");
                        //有数据才去算
                        if (up_List.size() > 0) {
                            final ArrayList<SportRecord> dailyList = new ArrayList<>();
                              for (int i = 0; i < up_List.size(); i++) {
                                final String startTime = up_List.get(0).getStart_time();
                                Date parse = null;
                                try {
                                    parse = sdfHMS.parse(startTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
//                                指针数据的开始时间
                                final long time1 = parse.getTime();
                                String indexITime = up_List.get(i).getStart_time();
                                Date parse1 = null ;
                                try {
                                    parse1= sdfHMS.parse(indexITime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
//                                当前数据的开始时间
                                long time2 = parse1.getTime();
                                MyLog.e(TAG,"time1"+ startTime +"_________________"+"time2   "+indexITime);
                                //如果小于10分钟的话就去提交,不然就不提交.
//                                if ((time2-time1)<60000*10&&i!=up_List.size()-1) {
                                if ((time2-time1)<60000*10) {
                                    MyLog.e(TAG,"if方法执行了");
                                    MyLog.e(TAG,"i是"+i);
                                    dailyList.add(up_List.get(i));
//                                    如果是最后一条,直接把这些数据上传.
                                    if (i==up_List.size()-1){
                                        MyLog.e(TAG,"数据里面最后一条数据上传了");
                                        final String  endtime = up_List.get(i).getStart_time();
                                        CallServer.getRequestInstance().
                                                add(PortalActivity.this, false, CommParams.HTTP_SUBMIT_DATA, HttpHelper.updataSportDate(PortalActivity.this, provider.getCurrentDeviceMac(), dailyList, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUserBase().getThirdparty_access_token()), new HttpCallback<String>() {
                                                    @Override
                                                    public void onSucceed(int what, Response<String> response) {
                                                        PreferencesToolkits.setServerUpdateTime(PortalActivity.this);
                                                        MyLog.e(TAG, "【NEW离线数据同步】response:" + response.get());
                                                        MyLog.e(TAG,"starttime"+ startTime+"___________"+"endtime"+endtime);
                                                        long sychedNum = UserDeviceRecord.updateForSynced(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "", startTime, endtime);
                                                        MyLog.d(TAG, "【NEW离线数据同步】本次共有" + sychedNum + "条运动数据已被标识为\"已同步\"！[" + startTime + "~" + endtime + "]");
                                                        updateFor_handleDataEnd();
                                                    }
                                                    @Override
                                                    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                                                        MyLog.e(TAG, "【NEW离线数据同步】 onFailed responseCode:" + responseCode + "message:" + message);
                                                    }
                                                });
                                    }
                                }else {
                                    MyLog.e(TAG,"else方法执行了");
                                  final String  endtime = up_List.get(i).getStart_time();
                                    CallServer.getRequestInstance().
                                            add(PortalActivity.this, false, CommParams.HTTP_SUBMIT_DATA, HttpHelper.updataSportDate(PortalActivity.this, provider.getCurrentDeviceMac(), dailyList, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUserBase().getThirdparty_access_token()), new HttpCallback<String>() {
                                                @Override
                                                public void onSucceed(int what, Response<String> response) {
                                                    PreferencesToolkits.setServerUpdateTime(PortalActivity.this);
                                                    MyLog.e(TAG, "【NEW离线数据同步】response:" + response.get());
                                                    MyLog.e(TAG,"starttime"+ startTime+"___________"+"endtime"+endtime);
                                                    long sychedNum = UserDeviceRecord.updateForSynced(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "", up_List.get(0).getStart_time(), endtime);
                                                    MyLog.d(TAG, "【NEW离线数据同步】本次共有" + sychedNum + "条运动数据已被标识为\"已同步\"！[" + startTime + "~" + endtime + "]");
                                                    updateFor_handleDataEnd();
                                                }
                                                @Override
                                                public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                                                    MyLog.e(TAG, "【NEW离线数据同步】 onFailed responseCode:" + responseCode + "message:" + message);
                                                }
                                            });
                                }
                            }
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object Object) {
                        super.onPostExecute(Object);
                    }
                }.execute();
            }
          /*  ArrayList<SportRecord> historyChart = UserDeviceRecord.findHistoryChart(PortalActivity.this, String.valueOf(userEntity.getUser_id()), "2016-10-31 00:00:00", "2016-10-31 23:59:59", false);
            CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_SUBMIT_FEEDBACKDATA, HttpHelper.updataFeedbackDate("反馈","2016-10-31 00:00:00 +0000","2016-10-31 23:59:59 +0000",PortalActivity.this
            ,historyChart), new HttpCallback<String>() {
                @Override
                public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                        MyLog.e(TAG,what+"-------------feedback"+"failed");
                       MyLog.e(TAG,message.toString()+"--------------responseCode---"+responseCode);
                }
                @Override
                public void onSucceed(int what, Response<String> response) {
                    MyLog.e(TAG,what+"-------------feedback"+"success"+response.toString());
                    Logger.e(response.toString());
                }
            });*/

        }

        /**********
         * 消息提醒设置成功
         *********/
        @Override
        public void updateFor_notify() {
            super.updateFor_notify();
            MyLog.e(TAG, "消息提醒设置成功！");
        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyForModelName(latestDeviceInfo);
            if (latestDeviceInfo.modelName == null)
                latestDeviceInfo.modelName = "LW100";
            MyLog.e(TAG, "modelName:" + latestDeviceInfo.modelName);
            MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setModel_name(latestDeviceInfo.modelName);
            //去服务器获取显示页面的bean
//            CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_UPDATA_MODELNAME, NoHttpRuquestFactory.createModelRequest(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id(), latestDeviceInfo.modelName), httpCallback);
//            if((System.currentTimeMillis()/1000)-PreferencesToolkits.getOADUpdateTime(getActivity())>86400)
            {
                // 查询是否要更新固件
                final LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                FirmwareDTO firmwareDTO = new FirmwareDTO();
                int deviceType = 1;
                firmwareDTO.setDevice_type(deviceType);
                firmwareDTO.setFirmware_type(DeviceActivity.DEVICE_VERSION_TYPE);
                int version_int = ToolKits.makeShort(vo.version_byte[1], vo.version_byte[0]);
                firmwareDTO.setVersion_int(version_int + "");
                firmwareDTO.setModel_name(latestDeviceInfo.modelName);
                if (MyApplication.getInstance(PortalActivity.this).isLocalDeviceNetworkOk()) {
                    //请求网络
                    CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_OAD, NoHttpRuquestFactory.create_OAD_Request(firmwareDTO), new HttpCallback<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                            String value = dataFromServer.getReturnValue().toString();
                            if (!CommonUtils.isStringEmpty(response.get())) {
                                MyLog.e(TAG, "更新固件:" + response.get());
                                if (dataFromServer.getErrorCode() != 10020) {
                                    JSONObject object = JSON.parseObject(value);
                                    String version_code = object.getString("version_code");
                                    int priority = object.getIntValue("priority");
                                    if (Integer.parseInt(version_code, 16) > Integer.parseInt(vo.version, 16)) {
                                        if (priority == 1) {
                                            AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                                                    .setTitle(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_tip))
                                                    .setMessage(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.bracelet_oad_Portal))
                                                    .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_ok),
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, DeviceActivity.DEVICE_UPDATE));
                                                                }
                                                            })
                                                    .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, com.VitaBit.VitaBit.R.string.general_cancel), null)
                                                    .create();
                                            if (System.currentTimeMillis() / 1000 - PreferencesToolkits.getOADUpdateTime(PortalActivity.this) > 24 * 3600) {
                                                PreferencesToolkits.setOADUpdateTime(PortalActivity.this);
                                                dialog.show();
                                            }
                                        }
                                        if (priority == 2) {
                                            startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, DeviceActivity.DEVICE_UPDATE));
                                        }

                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                        }
                    });
                }

                if (mScrollView.isRefreshing()) {
                    mScrollView.onRefreshComplete();
                }
            }

        }

        /**********
         * 闹钟提醒设置成功
         *********/
        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
            MyLog.e(TAG, "updateFor_notifyForSetClockSucess！");
        }

        /**********
         * 久坐提醒设置成功
         *********/
        @Override
        public void updateFor_notifyForLongSitSucess() {
            super.updateFor_notifyForLongSitSucess();
            MyLog.e(TAG, "updateFor_notifyForLongSitSucess！");
        }

        /**********
         * 身体信息(激活设备)设置成功
         *********/
        @Override
        public void updateFor_notifyForSetBodySucess() {
            MyLog.e(TAG, "updateFor_notifyForSetBodySucess");
            refreshBatteryUI();
        }

        /**********
         * 设置时间失败
         *********/
        @Override
        public void updateFor_handleSetTimeFail() {
            MyLog.e(TAG, "updateFor_handleSetTimeFail");
            super.updateFor_handleSetTimeFail();
        }

        /**********
         * 设置时间成功
         *********/
        @Override
        public void updateFor_handleSetTime() {
            MyLog.e(TAG, "updateFor_handleSetTime");
            mScrollView.getHeaderLayout().getmHeaderText().setText(getString(com.VitaBit.VitaBit.R.string.refresh_time));
            getInfoFromDB();
        }
//        @Override
//        public void updateFor_notifyForDeviceFullSyncSucess_D(LPDeviceInfo deviceInfo) {
//            PreferencesToolkits.updateLocalDeviceInfo(PortalActivity.this, deviceInfo);
//            MyLog.e(TAG, "updateFor_notifyForDeviceFullSyncSucess_D");
//        }

        /**********
         * 获取设备ID
         *********/
        @Override
        public void updateFor_getDeviceId(String obj) {
            super.updateFor_getDeviceId(obj);
            MyLog.e(TAG, "读到的deviceid:" + obj);
        }

        /**********
         * 卡号读取成功
         *********/
        @Override
        public void updateFor_CardNumber(String cardId) {
            MyLog.e(TAG, "updateFor_CardNumber：" + cardId);
            super.updateFor_CardNumber(cardId);
            if (mScrollView.isRefreshing()) {
                mScrollView.onRefreshComplete();
            }
        }
    }

    private void setSyncTimeFromLocaVo() {
        LocalInfoVO localInfoVO = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
        if (localInfoVO.getSyncTime() != 0) {
            Date date = new Date(localInfoVO.getSyncTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD_HH_MM_SS);
            String synctime = simpleDateFormat.format(date);
            mScrollView.getHeaderLayout().setPullLabel(getResources().getString(com.VitaBit.VitaBit.R.string.portal_device_updatetime) + synctime);
        } else {
            String synctime = "";
            mScrollView.getHeaderLayout().setPullLabel(getResources().getString(com.VitaBit.VitaBit.R.string.portal_device_updatetime) + synctime);
        }

        long serverTime = PreferencesToolkits.getServerUpdateTime(PortalActivity.this);
        if (serverTime != 0) {
            Date date = new Date(serverTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD_HH_MM_SS);
            String synctime = simpleDateFormat.format(date);
            mScrollView.getHeaderLayout().setReleaseLabel(getResources().getString(com.VitaBit.VitaBit.R.string.portal_server_updatetime) + synctime);
        } else {
            String synctime = "";
            mScrollView.getHeaderLayout().setReleaseLabel(getResources().getString(com.VitaBit.VitaBit.R.string.portal_server_updatetime) + synctime);
        }


    }

    private void requestPermissions(final String permission, final int requestcode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(com.VitaBit.VitaBit.R.id.drawer_layout),
                    "you should open the LOCATION Permission if you want to use Bluetooth",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(PortalActivity.this, new String[]{permission}, requestcode);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(PortalActivity.this, new String[]{permission}, requestcode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.e("checkBLEPermission", "权限已经打开：");
                } else {
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;
        }
    }

}
