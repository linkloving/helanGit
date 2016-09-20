package com.VitaBit.VitaBit.logic.UI.device.movedevice;

import android.os.Bundle;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;

public class MoveDeviceActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_move_device);
    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((com.VitaBit.VitaBit.R.string.move_device_title)));
    }

    @Override
    protected void initListeners() {

    }

}
