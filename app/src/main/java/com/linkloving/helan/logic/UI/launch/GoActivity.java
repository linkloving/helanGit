package com.linkloving.helan.logic.UI.launch;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.helan.IntentFactory;
import com.linkloving.helan.MyApplication;
import com.linkloving.helan.R;
import com.linkloving.helan.http.HttpHelper;
import com.linkloving.helan.logic.dto.EntEntity;
import com.linkloving.helan.logic.dto.SportDeviceEntity;
import com.linkloving.helan.logic.dto.UserBase;
import com.linkloving.helan.logic.dto.UserEntity;
import com.linkloving.helan.prefrences.PreferencesToolkits;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoActivity extends AutoLayoutActivity {
    @InjectView(R.id.go)
    ImageView go;
    @InjectView(R.id.register_form_agreeLisenseCb_1) TextView cbAgreeLisence_1;
    @InjectView(R.id.register_form_agreeLisenseCb_2) TextView cbAgreeLisence_2;
    @InjectView(R.id.register_form_agreeLisenseCb_3) TextView cbAgreeLisence_3;
    @InjectView(R.id.register_form_agreeLisenseCb_4) TextView cbAgreeLisence_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go);
        ButterKnife.inject(this);
        /***
         * 隐私协议和条款区域
         */
        cbAgreeLisence_1 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_1);
        cbAgreeLisence_2 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_2);
        cbAgreeLisence_3 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_3);
        cbAgreeLisence_4 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_4);
        cbAgreeLisence_1.setText(this.getString(R.string.register_agreelisenc_1));
        cbAgreeLisence_2.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_2)+"</u>"));
        cbAgreeLisence_3.setText(this.getString(R.string.register_agreelisenc_3));
        cbAgreeLisence_4.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_4)+"</u>"));

        cbAgreeLisence_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentFactory.createCommonWebActivityIntent(GoActivity.this, HttpHelper.Private_Policy));
            }
        });
        cbAgreeLisence_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentFactory.createCommonWebActivityIntent(GoActivity.this,  HttpHelper.Private_Policy));
            }
        });
        /***
         * go按钮
         */
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(GoActivity.this);
                if( userAuthedInfo == null) {
                    userAuthedInfo = new UserEntity();
                    int ran = (int)((10000000)*Math.random()+10000);
                    userAuthedInfo.setUser_id(ran);
                    userAuthedInfo.setDeviceEntity(new SportDeviceEntity());
                    userAuthedInfo.setUserBase(new UserBase("1980-01-01"));
                    userAuthedInfo.setEntEntity(new EntEntity());
                    userAuthedInfo.getUserBase().setUser_status(1);
                    userAuthedInfo.getUserBase().setUser_weight(60);
                    userAuthedInfo.getUserBase().setUser_height(175);
                    MyApplication.getInstance(GoActivity.this).setLocalUserInfoProvider(userAuthedInfo);
                }
                startActivity(IntentFactory.createCodeActivityIntent(GoActivity.this));
//                startActivity(IntentFactory.createLoginWebActivityIntent(GoActivity.this));
                GoActivity.this.finish();
            }
        });
    }
}
