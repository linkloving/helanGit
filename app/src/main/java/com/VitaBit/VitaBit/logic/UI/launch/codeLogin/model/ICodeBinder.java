package com.VitaBit.VitaBit.logic.UI.launch.codeLogin.model;

import android.content.Context;

/**
 * Created by Administrator on 2016/7/28.
 */
public interface ICodeBinder {
    void LoginByCode(Context context,String code,CodeLoginLintener loginLintener);
    void getCode(Context context,getCodeListner loginLintener);
}
