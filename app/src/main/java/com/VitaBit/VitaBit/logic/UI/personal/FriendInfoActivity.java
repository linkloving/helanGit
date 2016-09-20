package com.VitaBit.VitaBit.logic.UI.personal;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.http.data.DataFromServer;
import com.VitaBit.VitaBit.logic.UI.friend.CommentActivity;
import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.utils.CommonUtils;
import com.VitaBit.VitaBit.utils.MyToast;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.alibaba.fastjson.JSONObject;
import com.VitaBit.VitaBit.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yolanda.nohttp.rest.Response;

public class FriendInfoActivity extends ToolBarActivity implements View.OnClickListener {
    public final static String TAG = FriendInfoActivity.class.getSimpleName();
    TextView name, sex, address, sign;
    Button attention, message;
    ImageView head;
    private UserEntity u;
    private String fromUserID;
    AttentionUserInfoDTO attentUserinfoDTO;
    private boolean mAttention = false;
    public final static int COMEIN_COMMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_friend_info);

    }

    @Override
    protected void getIntentforActivity() {
        Intent intent = getIntent();
        fromUserID = intent.getExtras().getString("__user_id__", "");
//        fromgUserAvatar = intent.getExtras().getString("__UserAvatar__", "");
        u = MyApplication.getInstance(this).getLocalUserInfoProvider();
        loadData();
    }

    private void loadData() {
        //创建请求获取数据
        if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
//            String user_time = new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
            CallServer.getRequestInstance().add(this, getString(com.VitaBit.VitaBit.R.string.general_submitting), CommParams.HTTP_LOAD_FRIENDINFO, NoHttpRuquestFactory.Load_FriendInfo_Request(u.getUser_id() + "", fromUserID), httpCallback);
        } else {
            MyToast.show(FriendInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();

            switch (what) {
                case CommParams.HTTP_LOAD_FRIENDINFO:
                    MyLog.e(TAG, "returnValue=" + response.get());
                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                        attentUserinfoDTO = JSONObject.parseObject(value, AttentionUserInfoDTO.class);
                        MyLog.e(TAG, "attentUserinfoDTO.getIsAttention()=" + attentUserinfoDTO.getIsAttention());
                        MyLog.e(TAG, "attentUserinfoDTO.getNickname()=" + attentUserinfoDTO.getNickname());
                        update(attentUserinfoDTO);
                    }
                    break;
                case CommParams.HTTP_ATTENTION_FRIEND:
                    //关注成功
                    MyLog.e(TAG, "response.get()=" + response.get());
                    mAttention = true;
                    attention.setText(getString(com.VitaBit.VitaBit.R.string.relationship_cancel_attention));
                    MyToast.showShort(FriendInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.relationship_attention_success));
                    break;

                case CommParams.HTTP_NO_ATTENTION_FRIEND:
                    mAttention = false;
                    attention.setText(getString(com.VitaBit.VitaBit.R.string.relationship_attention));
                    break;
//
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            switch (what) {
                case CommParams.HTTP_LOAD_FRIENDINFO:
                    //获取信息
                    break;
                case CommParams.HTTP_ATTENTION_FRIEND:
                    //关注
                    MyToast.showShort(FriendInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.relationship_attention_failed));
                    break;
                case CommParams.HTTP_NO_ATTENTION_FRIEND:
                    //取消关注
                    MyToast.showShort(FriendInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.relationship_cancel_attention_failed));
                    break;

            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public void update(AttentionUserInfoDTO profile) {
        //图像以后设置
        String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(FriendInfoActivity.this, fromUserID, profile.getUser_avatar_file_name(), true);
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
        ImageLoader.getInstance().displayImage(url, head, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                if(attentUserinfoDTO!=null){
                    if(attentUserinfoDTO.getUser_sex()==0){
                        head.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar_f);
                    }else {
                        head.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar_m);
                    }
                }


            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                head.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(attentUserinfoDTO!=null){
                    if(attentUserinfoDTO.getUser_sex()==0){
                        head.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar_f);
                    }else {
                        head.setImageResource(com.VitaBit.VitaBit.R.mipmap.default_avatar_m);
                    }
                }
            }
        });
        name.setText(profile.getNickname());
        sign.setText(profile.getWhat_s_up());

        if (profile.getUser_sex()==0) {
            sex.setText(getString(com.VitaBit.VitaBit.R.string.general_female));
        } else {
            sex.setText(getString(com.VitaBit.VitaBit.R.string.general_male));
        }
        if (!CommonUtils.isStringEmpty(profile.getLatitude()) && !CommonUtils.isStringEmpty(profile.getLongitude())) {

        }


        if (!String.valueOf(u.getUser_id()).equals(fromUserID)) {
            attention.setVisibility(View.VISIBLE);
            if (profile.getIsAttention()) {
                // 关注
                mAttention = true;
                attention.setText(getString(com.VitaBit.VitaBit.R.string.relationship_cancel_attention));

            } else {
//取消关注
                mAttention = false;
                attention.setText(getString(com.VitaBit.VitaBit.R.string.relationship_attention));
            }
        } else {
            attention.setVisibility(View.GONE);
        }

      /*  if(profile.isVirtual())
        {
            attention.setVisibility(View.GONE);
            commentsUIWrapper.isVirtual();
        }
        */
    }

    @Override
    protected void initView() {


        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.friendinfo_activity_title));
        head = (ImageView) findViewById(com.VitaBit.VitaBit.R.id.user_head);
        name = (TextView) findViewById(com.VitaBit.VitaBit.R.id.text_nick_name);
        sex = (TextView) findViewById(com.VitaBit.VitaBit.R.id.text_sex);
        address = (TextView) findViewById(com.VitaBit.VitaBit.R.id.text_address);
        sign = (TextView) findViewById(com.VitaBit.VitaBit.R.id.text_sign);
        attention = (Button) findViewById(com.VitaBit.VitaBit.R.id.text_attention);
        message = (Button) findViewById(com.VitaBit.VitaBit.R.id.text_message);

    }

    @Override
    protected void initListeners() {
        attention.setOnClickListener(this);
        message.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.VitaBit.VitaBit.R.id.text_attention:
                if (!ToolKits.isNetworkConnected(FriendInfoActivity.this)) {
                    MyToast.showLong(FriendInfoActivity.this, getString(com.VitaBit.VitaBit.R.string.general_network_faild));
                    return;
                }
                if (mAttention) {
                    if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {

                        //取消关注
                        // loadData(true, HttpSnsHelper.GenerateCancelConcernParams(u.getUser_id(), fromUserID), REQ_CANCEL_ATTENTION);
                        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_NO_ATTENTION_FRIEND, NoHttpRuquestFactory.NO_Attention_Friend_Request(u.getUser_id(), Integer.parseInt(fromUserID), false), httpCallback);
                    } else
                        MyToast.show(this, getString(com.VitaBit.VitaBit.R.string.main_more_sycn_fail), Toast.LENGTH_LONG);

                } else {
                    if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
                        //关注
                        CallServer.getRequestInstance().add(FriendInfoActivity.this, false, CommParams.HTTP_ATTENTION_FRIEND, NoHttpRuquestFactory.NO_Attention_Friend_Request(u.getUser_id(), Integer.parseInt(fromUserID), true), httpCallback);
                        MyLog.e(TAG, "u.getUser_id()====" + u.getUser_id() + "===fromUserID===" + fromUserID);
                    } else
                        MyToast.show(this, getString(com.VitaBit.VitaBit.R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }
                break;
            case com.VitaBit.VitaBit.R.id.text_message:
                //跳转到评论  跳转过去需要传的值：好友id、昵称 时间
//                startActivity(IntentFactory.createUserDetialActivityIntent(FriendInfoActivity.this, u.getUser_id(), Integer.parseInt(fromUserID),COMEIN_COMMENT));
                Intent intent = new Intent(FriendInfoActivity.this, CommentActivity.class);
                intent.putExtra("__user_id__", Integer.parseInt(fromUserID));
                intent.putExtra("_user_avatar_file_name_", attentUserinfoDTO.getUser_avatar_file_name()==null?"":attentUserinfoDTO.getUser_avatar_file_name());
                intent.putExtra("_nickname_", attentUserinfoDTO.getNickname());
                intent.putExtra("__check_tag_",COMEIN_COMMENT);
                MyLog.e(TAG,"传值到聊天界面：fromUserID"+fromUserID+",COMEIN_COMMENT="+COMEIN_COMMENT);
                startActivity(intent);
//                intent.putExtra(Integer.parseInt(fromUserID));
                break;

        }

    }
}
