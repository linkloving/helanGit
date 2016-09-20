package com.VitaBit.VitaBit.logic.UI.personal;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.http.data.DataFromServer;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.utils.MyToast;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.utils.CommonUtils;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yolanda.nohttp.rest.Response;

import java.text.MessageFormat;

public class EntInfoActivity extends ToolBarActivity implements View.OnClickListener {

    ImageView ent_head;

    TextView ent_name,ent_address,exit_ent;

    UserEntity userEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_ent_info);
        userEntity= MyApplication.getInstance(this).getLocalUserInfoProvider();
        if(userEntity!=null){
            MyLog.i("EntInfoActivity",userEntity.getEntEntity().toString());
        }
        loadImage();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.main_more_exit_group));
        ent_name= (TextView) findViewById(com.VitaBit.VitaBit.R.id.ent_name);
        ent_address= (TextView) findViewById(com.VitaBit.VitaBit.R.id.ent_address);
        ent_head= (ImageView) findViewById(com.VitaBit.VitaBit.R.id.ent_head);
        exit_ent= (TextView) findViewById(com.VitaBit.VitaBit.R.id.exit_ent);
        if(!CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
            ent_name.setText(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getEnt_name());
            if(!CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getEnt_portal_url())){
                ent_address.setText(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getEnt_portal_url());
            }else {
                ent_address.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initListeners() {
        exit_ent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
          case com.VitaBit.VitaBit.R.id.exit_ent:
              if(MyApplication.getInstance(EntInfoActivity.this).isLocalDeviceNetworkOk()){
                  new AlertDialog.Builder(EntInfoActivity.this)  //
                          .setTitle(getString(com.VitaBit.VitaBit.R.string.main_more_exit_ent))
                          .setMessage(MessageFormat.format(getString(com.VitaBit.VitaBit.R.string.main_more_exit_ent_message), userEntity.getEntEntity().getEnt_name()))
                          .setPositiveButton(getString(com.VitaBit.VitaBit.R.string.general_yes), new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  //请求退出群组
                                  CallServer.getRequestInstance().add(EntInfoActivity.this,false, CommParams.REQUEST_GET_GROUP, NoHttpRuquestFactory.create_FindQun_QuitGroup_Request(userEntity.getUser_id() + ""),httpcallbackQuitGroup);
                              }
                          }).setNegativeButton(getString(com.VitaBit.VitaBit.R.string.general_cancel), null)
                          .show();
              }else{
                  ToolKits.showCommonTosat(EntInfoActivity.this, false, getString(com.VitaBit.VitaBit.R.string.general_network_faild), Toast.LENGTH_SHORT);
              }

              break;
        }

    }


    private void loadImage() {
        if(userEntity==null){
            return;
        }
        String url= NoHttpRuquestFactory.getEntAvatarDownloadURL(userEntity.getEntEntity().getPortal_logo_file_name());

        MyLog.i("下载群组头像的uil=",url);

        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
        ImageLoader.getInstance().displayImage(url, ent_head, options, new ImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                ImageView mhead=  (ImageView)view;
                mhead.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ImageView  mhead=  (ImageView)view;

                mhead.setImageBitmap(loadedImage);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                ImageView  mhead=  (ImageView)view;
                mhead.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar);
            }
        });

    }


    private HttpCallback<String> httpcallbackQuitGroup = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (dataFromServer.getErrorCode()==1&&!CommonUtils.isStringEmpty(value)) {
                UserEntity userEntity =new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                MyApplication.getInstance(EntInfoActivity.this).setLocalUserInfoProvider(userEntity);
                MyToast.show(EntInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.main_more_exit_ent_success), Toast.LENGTH_SHORT);
                finish();
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            if(what==CommParams.REQUEST_GET_GROUP){
                MyLog.e("EntInfoActivity", "退出群组失败了");

            }
        }
    };
}
