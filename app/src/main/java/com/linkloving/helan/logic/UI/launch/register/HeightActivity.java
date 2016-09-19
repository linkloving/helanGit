package com.linkloving.helan.logic.UI.launch.register;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.linkloving.helan.IntentFactory;
import com.linkloving.helan.MyApplication;
import com.linkloving.helan.R;
import com.linkloving.helan.basic.toolbar.ToolBarActivity;
import com.linkloving.helan.logic.UI.launch.view.ScaleHeightRulerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HeightActivity extends ToolBarActivity {

    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.scaleWheelView_height)
    ScaleHeightRulerView mHeightWheelView;
    @InjectView(R.id.tv_user_height_value)
    TextView height;


    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        ButterKnife.inject(this);

        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleHeightRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeight = value;
                Log.e("身高",mHeight+"");
                height.setText(mHeight+"");
                MyApplication.getInstance(HeightActivity.this).getLocalUserInfoProvider().getUserBase().setUser_height((int)mHeight);
            }
        });
    }
    @OnClick(R.id.next)
    void next(){
        IntentFactory.startWeightActivityIntent(HeightActivity.this,1); //1是注册
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
}
