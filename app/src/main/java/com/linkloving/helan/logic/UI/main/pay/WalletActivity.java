package com.linkloving.helan.logic.UI.main.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.helan.BleService;
import com.linkloving.helan.MyApplication;
import com.linkloving.helan.R;
import com.linkloving.helan.basic.toolbar.ToolBarActivity;
import com.linkloving.helan.logic.dto.UserEntity;
import com.linkloving.helan.prefrences.PreferencesToolkits;
import com.linkloving.helan.prefrences.devicebean.LocalInfoVO;
import com.linkloving.helan.prefrences.devicebean.ModelInfo;
import com.linkloving.helan.utils.ToolKits;
import com.linkloving.helan.utils.logUtils.MyLog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class WalletActivity extends ToolBarActivity {
    private static final String TAG = WalletActivity.class.getSimpleName();

    /**卡片显示的logo*/
    private ImageView img_card_city;
    /**显示卡号*/
    private TextView textViewcard;
    /**卡片类型*/
    private TextView cardtype;
    /**显示余额*/
    private TextView balanceResult;
    /**充值*/
    private Button rechargeBtn;

    private ProgressDialog dialog_pay = null;

    private RecyclerView record_RecyclerView;
    private WalletAdapter walletAdapeter;


    private UserEntity userEntity;
    private BLEProvider provider;
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    private LPDeviceInfo deviceInfo;


    /**
     * 钱包集合
     */
    LinkedList<LLTradeRecord> list_qianbao = new LinkedList<LLTradeRecord>()
    {
        public void addFirst(LLTradeRecord object)
        {
            super.addFirst(object);
            if(size() > 10)
                removeLast();
        }

        @Override
        public boolean add(LLTradeRecord object) {
            if(size() > 10)
                removeLast();
            return super.add(object);
        };
    };
    /**
     * 现金集合
     */
    LinkedList<LLXianJinCard> list_XJ = new LinkedList<LLXianJinCard>()
    {
        public void addFirst(LLXianJinCard object)
        {
            super.addFirst(object);
        };
    };


    @Override
    protected void onPause() {
        super.onPause();
        if(provider.isConnectedAndDiscovered()){
            provider.closeSmartCard(this);
        }else{
           Toast.makeText(WalletActivity.this ,getString(R.string.pay_no_connect),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(provider.getBleProviderObserver()==null)
            provider.setBleProviderObserver(bleProviderObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        initData();
    }

    private void initData() {
//        湖北数码要求不显示余额
//        if(MyApplication.getInstance(PayActivity.this).getLocalUserInfoProvider().getEid().equals(Company.HUBEI_SHUMA)){
//            balance_layout.setVisibility(View.GONE);
//        }
        ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(WalletActivity.this,MyApplication.getInstance(WalletActivity.this).getLocalUserInfoProvider().getDeviceEntity().getModel_name());
        if(modelInfo==null){
            return;
        }
        //从卡号里面获知卡地址城市
        String card =  userEntity.getDeviceEntity().getCard_number();
        deviceInfo = new LPDeviceInfo();
        if(card.startsWith(LPDeviceInfo.SUZHOU_)){
            deviceInfo.customer = LPDeviceInfo.SUZHOU_;   //苏州
            walletAdapeter = new WalletAdapter(this,list_qianbao,WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.szsmk_logo));
            img_card_city.setVisibility(View.VISIBLE);
            textViewcard.setText(card);
            cardtype.setText("苏州市民卡-B卡");
        }
        //柳州
        else if(card.startsWith(LPDeviceInfo.LIUZHOU_4) || card.startsWith(LPDeviceInfo.LIUZHOU_5) ){
            deviceInfo.customer = LPDeviceInfo.LIUZHOU_5;
            walletAdapeter = new WalletAdapter(this,list_qianbao,WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
            cardtype.setText("柳州市民卡");
        }
        //湖北数码
        else if(card.startsWith(LPDeviceInfo.HUBEI_SHUMA) )
        {
            deviceInfo.customer = LPDeviceInfo.HUBEI_SHUMA;
            walletAdapeter = new WalletAdapter(this,list_XJ,WalletAdapter.TYPE_XIANJIN);
            record_RecyclerView.setAdapter(walletAdapeter);
            cardtype.setText("湖北数码视讯");
        }
        else if(card.startsWith(LPDeviceInfo.LINGNANTONG) )
        {
            //岭南通充值
            deviceInfo.customer = LPDeviceInfo.LINGNANTONG;
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.yct_logo));
            img_card_city.setVisibility(View.VISIBLE);
            textViewcard.setText(card);
            cardtype.setText("岭南通·羊城通");
        }
        else if(card.startsWith(LPDeviceInfo.DATANG_TUOCHENG) )
        {
            deviceInfo.customer = LPDeviceInfo.DATANG_TUOCHENG;
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.tuocheng_logo));
            textViewcard.setText(card);
            cardtype.setText("驼城通");
        }
        else
        {
            deviceInfo.customer = LPDeviceInfo.UN_KNOW_;
            walletAdapeter = new WalletAdapter(this,list_qianbao,WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
        }
        if( modelInfo.getFiscard()==2){
            //这种卡片可以充值 所以打开充值按钮
            rechargeBtn.setVisibility(View.VISIBLE);
        }else{
            rechargeBtn.setVisibility(View.GONE);
        }



        //已经知道是哪个城市了
        if(!deviceInfo.customer.equals(LPDeviceInfo.UN_KNOW_))
        {
            //已经城市 && 蓝牙已经连接
            if(provider.isConnectedAndDiscovered()){
                    //开始去查询卡片信息了 弹出dialog
                    dialog_pay.show();
                    provider.closeSmartCard(WalletActivity.this);
                    // 首先清空集合
                    provider.openSmartCard(WalletActivity.this);
                    textViewcard.setText(getString(R.string.menu_pay_reading));
                    balanceResult.setText("0.00");

            }
            else
            {
                //蓝牙未连接
                Toast.makeText(WalletActivity.this ,getString(R.string.pay_no_connect),Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            //未知的卡号
            textViewcard.setText(getString(R.string.menu_pay_unknow));
            cardtype.setText(getString(R.string.menu_pay_unknow));
            balanceResult.setText("0.00");
            if(dialog_pay!=null && dialog_pay.isShowing()){
                dialog_pay.dismiss();
            }
        }
    }


    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getResources().getString(R.string.menu_pay));
        img_card_city = (ImageView) findViewById(R.id.img_card_city);
        img_card_city.setVisibility(View.INVISIBLE);
        textViewcard = (TextView) findViewById(R.id.tv_card_number);
        cardtype = (TextView) findViewById(R.id.cardtype);
        balanceResult = (TextView) findViewById(R.id.card_account);
        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        rechargeBtn.setVisibility(View.GONE);
        record_RecyclerView = (RecyclerView) findViewById(R.id.recycler_view_record);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        record_RecyclerView.setLayoutManager(layoutManager);
        String title = getResources().getString(R.string.menu_pay);
        dialog_pay =new ProgressDialog(this);
        dialog_pay.setMessage(getString(R.string.pay_loading));
    }

    @Override
    protected void initListeners() {
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter{

        @Override
        protected Activity getActivity() {
            return WalletActivity.this;
        }

        @Override
        public void updateFor_OpenSmc(boolean isSuccess) {
            super.updateFor_OpenSmc(isSuccess);
            MyLog.i(TAG, "开卡成功！");
            if(isSuccess)
            {
                textViewcard.setText(userEntity.getDeviceEntity().getCard_number());
                provider.AIDSmartCard(WalletActivity.this,deviceInfo);
            }
        }

        @Override
        public void updateFor_AIDSmc(boolean isSuccess) {
            super.updateFor_AIDSmc(isSuccess);
            if(isSuccess){
                //读余额
                provider.PINSmartCard(WalletActivity.this,deviceInfo);
            }
        }

        @Override
        public void updateFor_checkPINSucess_D() {
            super.updateFor_checkPINSucess_D();
            provider.readCardBalance(WalletActivity.this,deviceInfo);
        }

        //余额
        @Override
        public void updateFor_GetSmcBalance(Integer obj) {
            super.updateFor_GetSmcBalance(obj);
            String money = ToolKits.inttoStringMoney(obj);
            balanceResult.setText(money);
            //把余额保存到本地 方便主界面显示
            LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(WalletActivity.this);
            localvo.setMoney(money);
            PreferencesToolkits.setLocalDeviceInfoVo(WalletActivity.this,localvo);

            if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)){
                provider.readCardRecord_XJ(WalletActivity.this,deviceInfo);
            }else{
                provider.getSmartCardTradeRecord(WalletActivity.this,deviceInfo);
            }

        }

        //钱包单条
        @Override
        public void updateFor_GetSmcTradeRecordAsync(LLTradeRecord record)
        {
            super.updateFor_GetSmcTradeRecordAsync(record);
            if(list_qianbao.contains(record))
            {
                MyLog.d(TAG, "包含记录！！！"+record.toString());
            }
            else
            {

                MyLog.d(TAG, "新纪录："+record.toString());
                record.setTradeCard(textViewcard.getText().toString());
                record.setTradeBalance(balanceResult.getText().toString());
                list_qianbao.add(record);
                walletAdapeter.notifyDataSetChanged();
//              setListViewHeightBasedOnChildren(recordListview);
            }
        }
        //钱包集合
        @Override
        public void updateFor_GetSmcTradeRecord(List<LLTradeRecord> list)
        {
            super.updateFor_GetSmcTradeRecord(list);
            if(list.size() <= 0)
            {
                MyLog.e(TAG, "没有记录！");
                //recordResult.setText("没有记录！");
            }
            else
            {
                MyLog.d(TAG,"获取记录成功！");
//                SharedPreferencesUtil.saveSharedPreferences(PayActivity.this, "list", new Gson().toJson(list));
            }

            if(provider.isConnectedAndDiscovered()){

                provider.closeSmartCard(WalletActivity.this);
                if(dialog_pay!=null && dialog_pay.isShowing()){
                    dialog_pay.dismiss();
                }
            }else{
                Toast.makeText(WalletActivity.this ,getString(R.string.pay_no_connect),Toast.LENGTH_LONG).show();
            }
        }

        //单条 现金 交易记录
        @Override
        public void updateFor_GetXJTradeRecordAsync(LLXianJinCard record) {
            super.updateFor_GetXJTradeRecordAsync(record);
            if(list_XJ.contains(record))
            {
                MyLog.e(TAG, "包含记录！！！"+record.toString());
            }
            else
            {
                MyLog.e(TAG, "新纪录："+record.toString());
                record.setTradeCard(textViewcard.getText().toString());
                record.setTradeBalance(balanceResult.getText().toString());
                list_XJ.addFirst(record);
            }
        }

        //所有 现金 交易记录读取完毕
        @Override
        public void updateFor_GetXJTradeRecord(List<LLXianJinCard> list_) {
            super.updateFor_GetXJTradeRecord(list_);
            if(list_XJ.size() <= 0)
            {
                Log.e(TAG, "没有记录！");
            }
            else
            {
                Collections.sort(list_XJ,new Comparator<LLXianJinCard>()
                {
                    @Override
                    public int compare(LLXianJinCard lhs, LLXianJinCard rhs) {
                        return  (int) -(Long.parseLong(lhs.getData_3()+lhs.getTime_3()) - Long.parseLong(rhs.getData_3()+rhs.getTime_3())) ; ///?????
                    }
                });
            }
            if(dialog_pay!=null && dialog_pay.isShowing()){
                dialog_pay.dismiss();
            }
            provider.closeSmartCard(WalletActivity.this);

        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();

            provider.closeSmartCard(WalletActivity.this); //关卡

            if(dialog_pay!=null && dialog_pay.isShowing()){
                dialog_pay.dismiss();
            }
            textViewcard.setText(getString(R.string.menu_pay_read_fail));
            balanceResult.setText("0.0");
        }
    };


}
