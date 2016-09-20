package com.linkloving.helan.logic.UI.launch.codeLogin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.linkloving.helan.IntentFactory;
import com.linkloving.helan.R;
import com.linkloving.helan.basic.AppManager;
import com.linkloving.helan.logic.UI.launch.codeLogin.presenter.CodePresenter;
import com.linkloving.helan.logic.UI.launch.codeLogin.view.ICodeView;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CodeActivity extends AutoLayoutActivity implements ICodeView{

    @InjectView(R.id.editCode)  AppCompatEditText editCode;

    @InjectView(R.id.cancelbtn) AppCompatButton cancel;

    @InjectView(R.id.sendbtn) AppCompatButton send;

    @InjectView(R.id.getcode_tv) AppCompatTextView getcode;

    ProgressDialog dialog;

    CodePresenter codePresenter = new CodePresenter(this);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.general_loading));

        getcode.setText(Html.fromHtml("<u>"+this.getString(R.string.code_get_code)+"</u>"));
        //从server去获取我们需要的code //并且展示到edittext上
//        codePresenter.createCode(this);
//        https://app.vitabit.software/Endpoints/Authorize/Init/8C9BEFAE-E735-4ADE-85D9-A22D1056F927

    }

    @OnClick(R.id.cancelbtn)
    void Cancel(View view){
        this.finish();
    }

    @OnClick(R.id.sendbtn)
    void Send(View view){
        codePresenter.LoginByCode(CodeActivity.this,editCode.getText().toString());
    }

    @OnClick(R.id.getcode_tv)
    void GetCode(View view){
        startActivity(IntentFactory.createLoginWebActivityIntent(CodeActivity.this));
    }

    @Override
    public void showLoading() {
        dialog.show();
    }

    @Override
    public void dismissLoading() {
        dialog.dismiss();
    }

    @Override
    public void toPortaActivity() {
        startActivity(IntentFactory.createPortalActivityIntent(CodeActivity.this));
        AppManager.getAppManager().finishAllActivity();
    }

    @Override
    public void showFailMessage() {
        Toast.makeText(CodeActivity.this,R.string.codeisincorrect, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCode(String code) {
        editCode.setText(code);
    }
}
