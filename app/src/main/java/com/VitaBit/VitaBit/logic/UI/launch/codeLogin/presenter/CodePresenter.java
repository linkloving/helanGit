package com.VitaBit.VitaBit.logic.UI.launch.codeLogin.presenter;

import android.content.Context;
import android.os.Handler;

import com.VitaBit.VitaBit.logic.UI.launch.codeLogin.model.CodeBinder;
import com.VitaBit.VitaBit.logic.UI.launch.codeLogin.model.CodeLoginLintener;
import com.VitaBit.VitaBit.logic.UI.launch.codeLogin.model.getCodeListner;
import com.VitaBit.VitaBit.logic.UI.launch.codeLogin.view.ICodeView;

/**
 * Created by zkx on 2016/7/28.
 */
public class CodePresenter {
    CodeBinder codeBinder;
    ICodeView codeView;



    private Handler handler = new Handler();

    public CodePresenter(ICodeView codeView) {
        this.codeBinder = new CodeBinder();
        this.codeView = codeView;
    }

    public void createCode(final Context context){
        codeView.showLoading(); //第一步出现dialog
        codeBinder.getCode(context, new getCodeListner() {
            @Override
            public void getCodeSuccess(String code) {
                codeView.dismissLoading(); //获取成功后loading消失
                codeView.showCode(code); //让页面显示code
            }

            @Override
            public void getCodeFail() {
                codeView.dismissLoading(); //获取成功后loading消失
                codeView.showFailMessage();//让页面显示失败的信息
            }
        });
    }


    public void LoginByCode(final Context context,String code){
        codeView.showLoading();
        codeBinder.LoginByCode(context, code, new CodeLoginLintener() {
            @Override
            public void loginSuccess() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        codeView.dismissLoading();
                        codeView.toPortaActivity();
                    }
                });
            }

            @Override
            public void loginFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        codeView.dismissLoading();
                        codeView.showFailMessage();
                    }
                });
            }
        });
    }
}
