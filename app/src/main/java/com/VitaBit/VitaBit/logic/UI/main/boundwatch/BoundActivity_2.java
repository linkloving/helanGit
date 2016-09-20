package com.VitaBit.VitaBit.logic.UI.main.boundwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.VitaBit.VitaBit.utils.LanguageHelper;

public class BoundActivity_2 extends ToolBarActivity {
    private ImageView bound_step_img;
    private Button next;
    private Button skipBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_bound_activity_2);
    }

    @Override
    protected void getIntentforActivity() {

    }
    @Override
    protected void initView()
    {
        HideButtonRight(false);
        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.bound_title));
        next = (Button) findViewById(com.VitaBit.VitaBit.R.id.scan);
        skipBtn = getRightButton();
        ViewGroup.LayoutParams layoutParams = skipBtn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        skipBtn.setLayoutParams(layoutParams);
        skipBtn.setText(getString(com.VitaBit.VitaBit.R.string.bound_skip));
        skipBtn.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.white));
        bound_step_img = (ImageView) findViewById(com.VitaBit.VitaBit.R.id.bound_step_img);
        if(LanguageHelper.isChinese_SimplifiedChinese()){
            bound_step_img.setBackgroundResource(com.VitaBit.VitaBit.R.mipmap.bound_step_two_phone);
        }else{
            bound_step_img.setBackgroundResource(com.VitaBit.VitaBit.R.mipmap.bound_step_two_phone_en);

        }

    }

    @Override
    protected void initListeners() {
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
                if(ToolKits.isNetworkConnected(BoundActivity_2.this))
                {
                    startActivityForResult(new Intent(BoundActivity_2.this, BLEListActivity.class),BoundActivity.REQUEST_CODE_BLE_LIST);
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(BoundActivity_2.this)
                            .setTitle(ToolKits.getStringbyId(BoundActivity_2.this, com.VitaBit.VitaBit.R.string.bound_failed))
                            .setMessage(ToolKits.getStringbyId(BoundActivity_2.this, com.VitaBit.VitaBit.R.string.bound_failed_msg))
                            .setPositiveButton(ToolKits.getStringbyId(BoundActivity_2.this, com.VitaBit.VitaBit.R.string.general_ok),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BoundActivity.REQUEST_CODE_BLE_LIST){
            setResult(resultCode, data);
            finish();
        }
    }
}
