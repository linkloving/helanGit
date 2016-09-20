package com.VitaBit.VitaBit.logic.UI.device.firmware;

import android.os.Bundle;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;

public class FirmwareUpdateActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_firmware_update);

    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((com.VitaBit.VitaBit.R.string.firmware_update_title)));
    }

    @Override
    protected void initListeners() {

    }

}
