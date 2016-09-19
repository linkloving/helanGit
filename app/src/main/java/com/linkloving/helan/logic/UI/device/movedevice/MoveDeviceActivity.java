package com.linkloving.helan.logic.UI.device.movedevice;

import android.os.Bundle;

import com.linkloving.helan.R;
import com.linkloving.helan.basic.toolbar.ToolBarActivity;

public class MoveDeviceActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_device);
    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.move_device_title)));
    }

    @Override
    protected void initListeners() {

    }

}
