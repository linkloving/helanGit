package com.VitaBit.VitaBit.logic.UI.launch.register;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.http.data.DataFromServer;
import com.VitaBit.VitaBit.logic.UI.launch.view.DatePicker;
import com.VitaBit.VitaBit.logic.dto.UserBase;
import com.VitaBit.VitaBit.prefrences.PreferencesToolkits;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.alibaba.fastjson.JSON;
import com.VitaBit.VitaBit.IntentFactory;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.basic.AppManager;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.yolanda.nohttp.rest.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BrithActivity extends ToolBarActivity {
    @InjectView(com.VitaBit.VitaBit.R.id.next)
    Button next;
    @InjectView(com.VitaBit.VitaBit.R.id.date_picker)
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_brith);
        ButterKnife.inject(this);
    }

    @Override
    protected void getIntentforActivity() {
        int tag = getIntent().getIntExtra("tag",0);
    }

    @OnClick(com.VitaBit.VitaBit.R.id.next)
    void next(){
        String[] date = datePicker.getDate();
        String dateString = TextUtils.join("-", date);

        UserBase userbase = MyApplication.getInstance(BrithActivity.this).getLocalUserInfoProvider().getUserBase();
        userbase.setBirthdate(dateString);
        //网络交互：提交目标数据
        CallServer.getRequestInstance().add(BrithActivity.this, getString(com.VitaBit.VitaBit.R.string.general_submitting), CommParams.HTTP_SUBMIT_BODYDATA, NoHttpRuquestFactory.submit_RegisterationToServer_Modify(userbase), httpCallback);
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case CommParams.HTTP_SUBMIT_BODYDATA:

                    DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                    MyLog.e("注册", "=====onSucceed====Body返回结果：" + dataFromServer.getReturnValue().toString() + "===getErrorCode====" + dataFromServer.getErrorCode() + "====getErrMsg====" + dataFromServer.getErrMsg());
                    if (dataFromServer.getErrorCode() == 1) {

                        PreferencesToolkits.setAppStartFitst(BrithActivity.this);
                        //跳转到主页面
                        startActivity(IntentFactory.createPortalActivityIntent(BrithActivity.this));
                        //销毁前面的avtivity
                        AppManager.getAppManager().finishAllActivity();
                        finish();
                        break;
                    }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e("注册", "Body返回结果：what=" +what+",message="+ message+",url"+url+",tag="+tag+",responseCode="+responseCode);
            ToolKits.showCommonTosat(BrithActivity.this, false, getString(com.VitaBit.VitaBit.R.string.login_form_error), Toast.LENGTH_SHORT);
        }
    };
    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
