package com.VitaBit.VitaBit.logic.UI.main.bundband;

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
import com.VitaBit.VitaBit.logic.UI.main.boundwatch.BoundActivity;
import com.VitaBit.VitaBit.utils.LanguageHelper;

/**
 * Created by Administrator on 2016/4/13.
 */
public class bundbandstep2 extends ToolBarActivity {
    private Button next;
    private Button skipBtn;
    private ImageView img_view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_bound_band2);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.bound_title_band));

        HideButtonRight(false);
        skipBtn = getRightButton();
        ViewGroup.LayoutParams layoutParams = skipBtn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        skipBtn.setLayoutParams(layoutParams);
        skipBtn.setText(getString(com.VitaBit.VitaBit.R.string.bound_skip));
        skipBtn.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.white));

        next = (Button) findViewById(com.VitaBit.VitaBit.R.id.next);
        img_view = (ImageView) findViewById(com.VitaBit.VitaBit.R.id.img_view);
        if(LanguageHelper.isChinese_SimplifiedChinese()){
            img_view.setBackgroundResource(com.VitaBit.VitaBit.R.mipmap.bounded_step2_image_cn);
        }else{
            img_view.setBackgroundResource(com.VitaBit.VitaBit.R.mipmap.bounded_step2_image_en);
        }
    }

    @Override
    protected void initListeners() {
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
                if(ToolKits.isNetworkConnected(bundbandstep2.this))
                {
                    startActivityForResult(new Intent(bundbandstep2.this, BandListActivity.class), BoundActivity.REQUEST_CODE_BLE_LIST);
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(bundbandstep2.this)
                            .setTitle(ToolKits.getStringbyId(bundbandstep2.this, com.VitaBit.VitaBit.R.string.bound_failed))
                            .setMessage(ToolKits.getStringbyId(bundbandstep2.this, com.VitaBit.VitaBit.R.string.bound_failed_msg))
                            .setPositiveButton(ToolKits.getStringbyId(bundbandstep2.this, com.VitaBit.VitaBit.R.string.general_ok),new DialogInterface.OnClickListener() {
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
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == bundbandstep1.REQUEST_CODE_BLE_LIST){
            setResult(resultCode, data);
            finish();
        }
    }
}
