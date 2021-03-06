package com.VitaBit.VitaBit.logic.UI.main.bundband;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VitaBit.VitaBit.R;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.utils.MyToast;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.VitaBit.VitaBit.BleService;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.logic.UI.main.materialmenu.CommonAdapter;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.example.android.bluetoothlegatt.utils.ToastUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.VitaBit.VitaBit.R.string.portal_main_bound_failed_nocharge;

/**
 * Created by zkx on 2016/4/13.
 */
public class BandListActivity extends ToolBarActivity {

    public static final String TAG = BandListActivity.class.getSimpleName();
    private BLEListProvider listProvider;
    private BLEProvider provider;
    private BLEListHandler handler;

    public  String modelName = null;

    public static final int sendcount_MAX = 15;
    private int sendcount = 0;
    public static final int sendcount_time = 2000;

    private ListView mListView;
    private List<DeviceVO> macList = new ArrayList<DeviceVO>();
    private macListAdapter mAdapter;

    private AlertDialog dialog_bound;
    private ProgressDialog progressDialog;
    private Button boundBtn;
    String BoundFailMSG_SHOUHUAN;
    public static final int RESULT_OTHER = 1000;
    public static final int RESULT_BACK = 999;
    public static final int RESULT_FAIL = 998;
    public static final int RESULT_NOCHARGE = 997;
    public static final int RESULT_DISCONNECT = 996;

    public static final int REFRESH_BUTTON = 0x123;

    private int button_txt_count = 40;
    private Object[] button_txt = {button_txt_count};
    private Timer timer;
    private BLEHandler.BLEProviderObserverAdapter observerAdapter = null;
    private String getName  ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listProvider.stopScan();
        progressDialog = null;
        // 及时清除此Observer，以便在重新登陆时，正在运行中的蓝牙处理不会因此activity的回收而使回调产生空指针等异常
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        if (provider != null)
            provider.setBleProviderObserver(null);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider != null) {
            if (provider.getBleProviderObserver() == null) {
                provider.setBleProviderObserver(observerAdapter);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_blelist);
        progressDialog = new ProgressDialog(this);

        observerAdapter = new BLEProviderObserver();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        handler = new BLEListHandler(BandListActivity.this) {
            @Override
            protected void handleData(BluetoothDevice device) {
                for (DeviceVO v : macList) {
                    if (v.mac.equals(device.getAddress()))
                        return;
                }

                DeviceVO vo = new DeviceVO();
                vo.mac = device.getAddress();
                vo.name = device.getName();
//                MyLog.e(TAG,"Modlename是+++++"+vo.name);
                vo.bledevice = device;
                macList.add(vo);
                mAdapter.notifyDataSetChanged();
            }
        };
        listProvider = new BLEListProvider(this, handler);
        mAdapter = new macListAdapter(this, macList);
        initView();
        listProvider.scanDeviceList();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.bound_ble_list));
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(com.VitaBit.VitaBit.R.string.bound_ble_refresh));
        btn.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macList.clear();
                mAdapter.notifyDataSetChanged();
                listProvider.scanDeviceList();
            }
        });
        mListView = (ListView) findViewById(com.VitaBit.VitaBit.R.id.ble_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                provider.setCurrentDeviceMac(macList.get(index).mac);
                provider.setmBluetoothDevice(macList.get(index).bledevice);
                provider.connect_mac(macList.get(index).mac);
                MyLog.e(TAG,"点击的Modlename是+++++"+macList.get(index).name);
                if (progressDialog != null && !progressDialog.isShowing()){
                    progressDialog.setMessage(getString(com.VitaBit.VitaBit.R.string.portal_main_state_connecting));
                    progressDialog.show();
                    getName = macList.get(index).name;
                    MyLog.e(TAG,"点击的Modlename是+++++"+macList.get(index).name);
                }

            }
        });

        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(com.VitaBit.VitaBit.R.layout.activity_bound_band3, (LinearLayout) findViewById(com.VitaBit.VitaBit.R.id.layout_bundband));
        boundBtn = (Button) layout.findViewById(com.VitaBit.VitaBit.R.id.btncancle);
        boundBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_bound.dismiss();
                if (timer != null)
                    timer.cancel();
                provider.clearProess();
                BleService.getInstance(BandListActivity.this).releaseBLE();
                setResult(RESULT_BACK);
                finish();
            }
        });
        button_txt[0] = button_txt_count;
//        倒计时

            dialog_bound = new AlertDialog.Builder(BandListActivity.this)
                    .setView(layout)
                    .setTitle(com.VitaBit.VitaBit.R.string.portal_main_isbounding)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    })
                    .setCancelable(false).create();

    }

    @Override
    protected void initListeners() {

    }

    Runnable butttonRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = REFRESH_BUTTON;
            boundhandler.sendMessage(msg);
        }

        ;
    };


    Runnable boundRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            boundhandler.sendMessage(msg);
        }

        ;
    };

    Handler boundhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x333:
                    provider.requestbound_recy(BandListActivity.this);
                    break;
                case REFRESH_BUTTON:
                    button_txt[0] = button_txt_count;
                    Log.e(TAG, button_txt_count + "");
                    String second_txt = MessageFormat.format(getString(com.VitaBit.VitaBit.R.string.bound_scan_sqr), button_txt);
                    boundBtn.setText(second_txt);
                    if (button_txt_count == 0) {
                        if (dialog_bound != null && dialog_bound.isShowing()) {
                            if (timer != null)
                                timer.cancel();
                            dialog_bound.dismiss();
                        }
                        BleService.getInstance(BandListActivity.this).releaseBLE();
                        setResult(RESULT_FAIL);
                        finish();
                    }
                    break;
            }
        }

        ;
    };

    private class BLEProviderObserver extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return BandListActivity.this;
        }

        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BandListActivity.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            Log.i("BandListActivity", "断开连接");

            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }

            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();

            setResult(RESULT_DISCONNECT);
            finish();
        }

        @Override
        public void updateFor_handleConnectFailedMsg() {
            super.updateFor_handleConnectFailedMsg();

            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            provider.release();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            Log.i("BandListActivity", "连接成功");
            try {
                new Thread().sleep(BleService.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            provider.requestbound_fit(BandListActivity.this);
        }

        @Override
        public void updateFor_BoundNoCharge() {
            super.updateFor_BoundNoCharge();
            Log.e("BLEListActivity", "updateFor_BoundNoCharge");
            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            ToastUtil.showMyToast(BandListActivity.this,getString(R.string.portal_main_bound_failed_nocharge));
            setResult(RESULT_NOCHARGE);
            finish();
        }

        @Override
        public void updateFor_BoundContinue() {
            super.updateFor_BoundContinue();
            if(progressDialog!=null && progressDialog.isShowing() )
                progressDialog.dismiss();
//            if (getName.equals("B100A0")){
                if(dialog_bound!=null && !dialog_bound.isShowing() )
                    dialog_bound.show();
//            }
            if (dialog_bound != null && dialog_bound.isShowing()) {
                if(timer==null){
                    timer = new Timer(); // 每1s更新一下
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            boundhandler.post(butttonRunnable);
                            button_txt_count--;
                            MyLog.e(TAG, "Timer开始了");
                            if (button_txt_count < 0) {
                                timer.cancel();
                            }
                        }
                    }, 0, 1000);
                }
            }

            if (sendcount < sendcount_MAX) {
                boundhandler.postDelayed(boundRunnable, sendcount_time);
                sendcount++;
            } else {
                Log.e("BandListActivity", "已经发送超出15次");
                provider.clearProess();
                BleService.getInstance(BandListActivity.this).releaseBLE();
                setResult(RESULT_FAIL);
                finish();
            }
        }

        @Override
        public void updateFor_BoundSucess() {
            provider.SetDeviceTime(BandListActivity.this);
            BleService.getInstance(BandListActivity.this).syncAllDeviceInfo(BandListActivity.this);
        }

        @Override
        public void updateFor_handleSetTime() {
            provider.getModelName(BandListActivity.this);
        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            if(latestDeviceInfo==null){
                //未获取成功  重新获取
                provider.getModelName(BandListActivity.this);
            }else{
                modelName = latestDeviceInfo.modelName;
                MyLog.e(TAG,"设备的modelName是："+modelName);
                if (dialog_bound != null && dialog_bound.isShowing()){
                    if(timer!=null)
                        timer.cancel();
                    dialog_bound.dismiss();
                }
                MyApplication.getInstance(BandListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setModel_name(modelName);
                //获取成功
                startBound();
            }
        }

        /**
         * 通知：设备绑定信息同步到服务端完成
         */
        @Override
        public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {
            if (resultFromServer != null) {
                if (((String) resultFromServer).equals("1")) {
                    Log.e(TAG, "绑定成功！");
                    MyApplication.getInstance(BandListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                    MyApplication.getInstance(BandListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToolKits.showCommonTosat(BandListActivity.this, true, ToolKits.getStringbyId(BandListActivity.this, com.VitaBit.VitaBit.R.string.portal_main_bound_success), Toast.LENGTH_LONG);
                    setResult(RESULT_OK);
                    finish();
                } else if (((String) resultFromServer).equals("10024")) {
                    MyLog.e(TAG, "========绑定失败！========");
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    new android.support.v7.app.AlertDialog.Builder(BandListActivity.this)
                            .setTitle(getActivity().getResources().getString(com.VitaBit.VitaBit.R.string.general_prompt))
                            .setMessage(MessageFormat.format(ToolKits.getStringbyId(getActivity(), com.VitaBit.VitaBit.R.string.portal_main_has_bound_other), BoundFailMSG_SHOUHUAN))
                            .setPositiveButton(getActivity().getResources().getString(com.VitaBit.VitaBit.R.string.general_ok), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog_, int which) {
                                    dialog_.dismiss();
                                    BleService.getInstance(getActivity()).releaseBLE();
                                    setResult(RESULT_FAIL);
                                    finish();
                                }
                            })
                            .show();
                }
            } else {

                Log.e(TAG, "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                BleService.getInstance(getActivity()).releaseBLE();
            }
        }


        @Override
        public void updateFor_BoundFail() {
            // BandListActivity.this.notifyAll();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            provider.clearProess();
            provider.unBoundDevice(BandListActivity.this);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            setResult(RESULT_FAIL);
            finish();
        }

    }

    private void startBound() {
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
        MyLog.e(TAG, "startBound()");
        if(progressDialog!=null && !progressDialog.isShowing()){
            progressDialog.setMessage(getString(com.VitaBit.VitaBit.R.string.general_submitting));
            progressDialog.show();
        }
        if (observerAdapter != null)
            observerAdapter.updateFor_boundInfoSyncToServerFinish("1");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.REQUEST_ENABLE_BT) {
            switch (resultCode) {

                case Activity.RESULT_CANCELED: //用户取消打开蓝牙
                    Log.e("BLEListActivity", "用户取消打开蓝牙");
                    break;

                case Activity.RESULT_OK:       //用户打开蓝牙
                    listProvider.scanDeviceList();
                    Log.e("BLEListActivity", "/用户打开蓝牙");

                    break;

                default:
                    break;
            }
            return;
        }

    }

    class DeviceVO {
        public String mac;
        public String name;
        public int device_type ;
        public BluetoothDevice bledevice;

    }

    class macListAdapter extends CommonAdapter<DeviceVO> {
        public class ViewHolder {
            public TextView mac;
        }

        ViewHolder holder;

        public macListAdapter(Context context, List<DeviceVO> list) {
            super(context, list);
        }

        @Override
        protected View noConvertView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(com.VitaBit.VitaBit.R.layout.list_item_ble_list, parent, false);
            holder = new ViewHolder();
            holder.mac = (TextView) convertView.findViewById(com.VitaBit.VitaBit.R.id.activity_sport_data_detail_sleepSumView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        protected View hasConvertView(int position, View convertView, ViewGroup parent) {
            holder = (ViewHolder) convertView.getTag();
            return convertView;
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            String mac = list.get(position).mac.substring(list.get(position).mac.length() - 5, list.get(position).mac.length());
            holder.mac.setText("ID:   " + removeCharAt(mac, 2));
            return convertView;
        }

    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
