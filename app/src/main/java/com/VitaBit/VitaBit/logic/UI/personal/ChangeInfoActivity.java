package com.VitaBit.VitaBit.logic.UI.personal;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.utils.CommonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChangeInfoActivity extends ToolBarActivity {
    public static final int TYPE_NICKNAME = 1;
    public static final int TYPE_HEIGHT = 2;
    public static final int TYPE_SHUOSHUO = 3;
    public static final String KEY_TYPE = "key_type";
    private UserEntity userEntity;
    private int type;

    @InjectView(com.VitaBit.VitaBit.R.id.edit_view)
    EditText edit_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_change_info);
        ButterKnife.inject(this);
        HideButtonRight(false);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        /**按钮设置*/
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(com.VitaBit.VitaBit.R.string.general_save));
        btn.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonUtils.isStringEmpty(edit_view.getText().toString())){
                    edit_view.setError(getResources().getString(com.VitaBit.VitaBit.R.string.error_field_required));
                }
                if( type ==TYPE_NICKNAME || type ==TYPE_HEIGHT ||type ==TYPE_SHUOSHUO){
                    Intent intent=new Intent();
                    intent.putExtra(KEY_TYPE, edit_view.getText()+"");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        if( type ==TYPE_NICKNAME){
            SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.user_info_update_nick_name_title));
            String text = userEntity.getUserBase().getNickname()+"";
            edit_view.setText(text);
            edit_view.setSelection(text.length());
        }else if( type ==TYPE_HEIGHT){
            SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.body_info_height));
            edit_view.setInputType(InputType.TYPE_CLASS_NUMBER);
            String text = userEntity.getUserBase().getUser_height()+"";
            edit_view.setText(text);
            edit_view.setSelection(text.length());
        }else if( type ==TYPE_SHUOSHUO){
            SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.whats_up_history_edit_whats_up));
            String text = userEntity.getUserBase().getWhat_s_up()+"";
            edit_view.setText(text);
            edit_view.setSelection(text.length());
        }

    }

    @Override
    protected void getIntentforActivity() {
        type = getIntent().getIntExtra("type",0);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
