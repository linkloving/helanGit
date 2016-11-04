package com.VitaBit.VitaBit.logic.UI.main.datachatactivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.R;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.db.sport.UserDeviceRecord;
import com.VitaBit.VitaBit.http.HttpHelper;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.logic.UI.main.PortalActivity;
import com.VitaBit.VitaBit.logic.UI.main.datachatactivity.ViewUtils.widget.PickTimeView;
import com.VitaBit.VitaBit.logic.dto.UserBase;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.linkloving.band.dto.SportRecord;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.rest.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/11/2.
 */

public class FeedbackActivity extends ToolBarActivity implements PickTimeView.onSelectedChangeListener {
    private static final String TAG = FeedbackActivity.class.getSimpleName();
    @InjectView(R.id.starttime)
    TextView starttime;
    @InjectView(R.id.endtime)
    TextView endtime;
    @InjectView(R.id.feedbacktext)
    EditText feedbacktext;
    @InjectView(R.id.send)
    Button send;
    private int type = 0;
    SimpleDateFormat sdfTime;
    private View viewParent;
    private SimpleDateFormat simpleDateFormat;
    private UserEntity userEntity;
    private String startTimeString ;
    private  String endTimeString  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedbackactivity);
        viewParent = LayoutInflater.from(this).inflate(R.layout.feedbackactivity, null);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(FeedbackActivity.this).getLocalUserInfoProvider();
        sdfTime = new SimpleDateFormat("MM-dd EEE HH:mm");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @OnClick(R.id.starttime)
    void setStarttime(View view) {
        type = 1;
        View inflateView = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.feedbacktime, null);
        final PickTimeView picktime = (PickTimeView) inflateView.findViewById(R.id.pickTime);
        picktime.setOnSelectedChangeListener(new PickTimeView.onSelectedChangeListener() {
            @Override
            public void onSelected(PickTimeView view, long timeMillis) {
                Date date = new Date(timeMillis);
                String format = simpleDateFormat.format(date);
                startTimeString = format ;
                starttime.setText(format);
            }
        });
        PopupWindow popupWindow = new PopupWindow(inflateView,LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(viewParent,Gravity.CENTER,0,0);
        MyLog.e("feedback","startzhixing l ");
    }

    @OnClick(R.id.endtime)
    void setEndtime(View view) {
        type = 2;
        View inflateView = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.feedbacktime, null);
        final PickTimeView picktime = (PickTimeView) inflateView.findViewById(R.id.pickTime);
        picktime.setOnSelectedChangeListener(new PickTimeView.onSelectedChangeListener() {
            @Override
            public void onSelected(PickTimeView view, long timeMillis) {
                Date date = new Date(timeMillis);
                String format = simpleDateFormat.format(date);
                endTimeString = format ;
                endtime.setText(format);
            }
        });
        PopupWindow popupWindow = new PopupWindow(inflateView,LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(viewParent,Gravity.CENTER,0,0);
        MyLog.e("feedback","startzhixing l ");

    }

    @OnClick(R.id.send)
    void setSend(View view){
        MyLog.e(TAG,"send执行了");
        ArrayList<SportRecord> historyChart = UserDeviceRecord.findHistoryChart(FeedbackActivity.this, String.valueOf(userEntity.getUser_id()), startTimeString, endTimeString, false);
        CallServer.getRequestInstance().add(FeedbackActivity.this, false, CommParams.HTTP_SUBMIT_FEEDBACKDATA, HttpHelper.updataFeedbackDate(feedbacktext.getText().toString(),startTimeString,endTimeString,FeedbackActivity.this
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
        });

    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void onSelected(PickTimeView view, long timeMillis) {
        String str = sdfTime.format(timeMillis);
        switch (type) {
            case 1:
                starttime.setText(str);
                break;
            case 2:
                endtime.setText(str);
                break;
        }

    }
}
