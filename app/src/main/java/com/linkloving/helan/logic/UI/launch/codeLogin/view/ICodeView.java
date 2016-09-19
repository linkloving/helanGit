package com.linkloving.helan.logic.UI.launch.codeLogin.view;

/**
 * Created by Administrator on 2016/7/28.
 */
public interface ICodeView {
    //显示dialog
    void showLoading();

    //隐藏Loading
    void dismissLoading();

    //跳转到主界面
    void toPortaActivity();

    //登录失败提示
    void showFailMessage();

    void showCode(String code);
}
