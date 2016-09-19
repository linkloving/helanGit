package com.linkloving.helan.logic.UI.main.bundband;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.helan.BleService;
import com.linkloving.helan.MyApplication;
import com.linkloving.helan.R;
import com.linkloving.helan.basic.toolbar.ToolBarActivity;
import com.linkloving.helan.logic.UI.main.materialmenu.CommonAdapter;
import com.linkloving.helan.utils.ToolKits;
import com.linkloving.helan.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class BundBandActivity extends ToolBarActivity {

    public static final String TAG = BundBandActivity.class.getSimpleName();
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
    private BLEHandler.BLEProviderObserverAdapter observerAdapter = null;

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
        setContentView(R.layout.activity_blelist);
        progressDialog = new ProgressDialog(this);

        observerAdapter = new BLEProviderObserver();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        handler = new BLEListHandler(BundBandActivity.this) {
            @Override
            protected void handleData(BluetoothDevice device) {
                for (DeviceVO v : macList) {
                    if (v.mac.equals(device.getAddress()))
                        return;
                }
                DeviceVO vo = new DeviceVO();
                vo.mac = device.getAddress();
                vo.name = device.getName();
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
        SetBarTitleText(getString(R.string.bound_ble_list));
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(R.string.bound_ble_refresh));
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macList.clear();
                mAdapter.notifyDataSetChanged();
                listProvider.scanDeviceList();
            }
        });
        mListView = (ListView) findViewById(R.id.ble_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                provider.setCurrentDeviceMac(macList.get(index).mac);
                provider.setmBluetoothDevice(macList.get(index).bledevice);
                provider.connect_mac(macList.get(index).mac);
                if (progressDialog != null && !progressDialog.isShowing()){
                    progressDialog.setMessage(getString(R.string.portal_main_state_connecting));
                    progressDialog.show();
                }
            }
        });
    }

    @Override
    protected void initListeners() {

    }


    private class BLEProviderObserver extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return BundBandActivity.this;
        }

        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BundBandActivity.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            Log.i("BundBandActivity", "断开连接");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void updateFor_handleConnectFailedMsg() {
            super.updateFor_handleConnectFailedMsg();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            Log.i("BundBandActivity", "连接成功");
            listProvider.stopScan();
            try {
                new Thread().sleep(BleService.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BleService.getInstance(BundBandActivity.this).syncAllDeviceInfo(BundBandActivity.this);
        }

        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
            if(latestDeviceInfo!=null && latestDeviceInfo.recoderStatus==5){
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.i("BundBandActivity", "用户非法");
                new AlertDialog.Builder(BundBandActivity.this)
                        .setTitle(R.string.portal_main_gobound)
                        .setMessage(R.string.portal_main_mustbund)
                        .setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                provider.release();
                                provider.setCurrentDeviceMac(null);
                                provider.setmBluetoothDevice(null);
                                provider.resetDefaultState();
                                dialog.dismiss();
                            }
                        }).create().show();
            }else{
                provider.getModelName(BundBandActivity.this);
            }

        }

        @Override
        public void updateFor_handleSetTime() {

        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            if(latestDeviceInfo==null){
                //未获取成功  重新获取
                provider.getModelName(BundBandActivity.this);
            }else{
                modelName = latestDeviceInfo.modelName;
                MyLog.e(TAG,"modelName是："+modelName);
                MyApplication.getInstance(BundBandActivity.this).getLocalUserInfoProvider().getDeviceEntity().setModel_name(modelName);
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
                    MyApplication.getInstance(BundBandActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                    MyApplication.getInstance(BundBandActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND_VERSION3);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToolKits.showCommonTosat(BundBandActivity.this, true, ToolKits.getStringbyId(BundBandActivity.this, R.string.portal_main_bound_success), Toast.LENGTH_LONG);
                    setResult(RESULT_OK);
                    finish();
                }
            } else {
                Log.e(TAG, "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                BleService.getInstance(getActivity()).releaseBLE();
            }
        }

    }

    private void startBound() {
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
        MyLog.e(TAG, "startBound()");
        if(progressDialog!=null && !progressDialog.isShowing()){
            progressDialog.setMessage(getString(R.string.general_submitting));
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
            convertView = inflater.inflate(R.layout.list_item_ble_list, parent, false);
            holder = new ViewHolder();
            holder.mac = (TextView) convertView.findViewById(R.id.activity_sport_data_detail_sleepSumView);
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

