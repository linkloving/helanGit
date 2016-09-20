package com.VitaBit.VitaBit.logic.UI.launch.codeLogin.model;

import android.content.Context;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.logic.dto.Profile;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.http.HttpHelper;
import com.VitaBit.VitaBit.utils.CommonUtils;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.yolanda.nohttp.rest.Response;

/**
 * Created by Administrator on 2016/7/28.
 */
public class CodeBinder implements ICodeBinder{
    private static final String TAG = CodeBinder.class.getSimpleName();

    @Override
    public void LoginByCode(final Context context, String code , final CodeLoginLintener loginLintener) {
        /**通过code去获取token*/
        CallServer.getRequestInstance().add(context, false, CommParams.HTTP_BOUND, HttpHelper.getToken(code), new HttpCallback<String>() {

            @Override
            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                MyLog.e(TAG, what + "errorMessage:"+message+"responseCode:"+responseCode);
                loginLintener.loginFail();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                MyLog.e(TAG, "TOKEN response:"+response.get());
                JSONObject json = JSON.parseObject(response.get());
                String token = json.getString("token");
                MyApplication.getInstance(context).getLocalUserInfoProvider().getUserBase().setThirdparty_access_token(token);
                /**通过token去获取个人信息*/
                CallServer.getRequestInstance().add(context, false, CommParams.HTTP_BOUND, HttpHelper.getProfile(token), new HttpCallback<String>() {

                    @Override
                    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                        MyLog.e(TAG, "Profile errorMessage:"+message );
                        loginLintener.loginFail();
                    }

                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        MyLog.e(TAG, "Profile response:"+response.get());
                        Profile profile = new Gson().fromJson(response.get(), Profile.class);
                        UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
                        //设置昵称
                        if(!CommonUtils.isStringEmpty(profile.getFirst_name()) || !CommonUtils.isStringEmpty(profile.getLast_name())){
                            userEntity.getUserBase().setFirst_name(profile.getFirst_name());
                            userEntity.getUserBase().setLast_name(profile.getLast_name());
                            userEntity.getUserBase().setNickname(profile.getFirst_name()+" "+profile.getLast_name() );
                            userEntity.getUserBase().setAvatar_color(profile.getAvatar_color());
                        }

                        //设置性别
                        if(!CommonUtils.isStringEmpty(profile.getGender())){
                            if("male".equals(profile.getGender())){
                                userEntity.getUserBase().setUser_sex(1);
                            }else{
                                userEntity.getUserBase().setUser_sex(0);
                            }
                        }
                        if(!CommonUtils.isStringEmpty(profile.getHeight())){
                            userEntity.getUserBase().setUser_height((int)Float.parseFloat(profile.getHeight()));
                        }

                        if(!CommonUtils.isStringEmpty(profile.getDob())){
                            String[] date=profile.getDob().split("T");
                            userEntity.getUserBase().setBirthdate(date[0]);
                        }

                        if(!CommonUtils.isStringEmpty(profile.getWeight())){
                            userEntity.getUserBase().setUser_weight((int)Float.parseFloat(profile.getWeight()));
                        }
                        loginLintener.loginSuccess();
                    }
                });
            }
        });
    }

    @Override
    public void getCode(Context context, final getCodeListner codeListner) {
        UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
        CallServer.getRequestInstance().add(context, true, CommParams.HTTP_BOUND, HttpHelper.getCode(userEntity.getUser_id()+""), new HttpCallback<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                MyLog.e(TAG, "CODE response:"+response.get());
                JSONObject json = JSON.parseObject(response.get());
                final String code = json.getString("code");
                MyLog.e(TAG, "CODE response:"+code);
                codeListner.getCodeSuccess(code);
            }
            @Override
            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                MyLog.e(TAG, what+"CODE onFailed:"+message);
                codeListner.getCodeFail();
            }
        });
    }
}
