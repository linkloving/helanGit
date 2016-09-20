package com.VitaBit.VitaBit.logic.UI.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.VitaBit.VitaBit.logic.dto.UserEntity;
import com.VitaBit.VitaBit.prefrences.PreferencesToolkits;
import com.VitaBit.VitaBit.IntentFactory;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.basic.AppManager;
import com.VitaBit.VitaBit.utils.CommonUtils;

public class AppStartActivity extends AppCompatActivity {

    private final static String TAG = AppStartActivity.class.getSimpleName();
    public static String SHAREDPREFERENCES_NAME = "first_pref";
    private RelativeLayout startLL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        // @see https://code.google.com/p/android/issues/detail?id=52247
        // @see https://code.google.com/p/android/issues/detail?id=2373
        // @see https://code.google.com/p/android/issues/detail?id=26658
        // @see https://github.com/cleverua/android_startup_activity
        // @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
        // 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
        if (!isTaskRoot())
        {// FIX START
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }// FIX END
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View view = View.inflate(this, com.VitaBit.VitaBit.R.layout.activity_app_start, null);
        setContentView(view);

        startLL = (RelativeLayout) findViewById(com.VitaBit.VitaBit.R.id.start_LL);

        UserEntity user = PreferencesToolkits.getLocalUserInfo(this);

        AppManager.getAppManager().addActivity(this);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }


    /**
     * 跳转到...
     */
    private void redirectTo()
    {
//        SharedPreferences preferences = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
//        boolean isFrist = (!preferences.contains("isFirstIn")) || (preferences.getBoolean("isFirstIn", true));
//        if (isFrist)
//        {
//            //如果是第一次登陆的话,本来是出现引导页的,这里直接简化,让它跳到登陆页面
//            MyLog.i(TAG,"第一次登陆>>>>>>>>>>>>>>>>>>");
//            //跳到登陆界面
//            startActivity(IntentFactory.createLoginPageActivity(AppStartActivity.this));
////            MyApplication.getInstance(this).setFirstIn(true);
//            finish();
//
//            //startActivity(IntentFactory.createHelpActivityIntent(AppStart.this, HelpActivity.FININSH_VIEWPAGE_GO_TAB_HOST, false));
////            finish();
//        }
//        else
        {
            UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(this);
            if( userAuthedInfo != null)
            {
                MyApplication.getInstance(this).setLocalUserInfoProvider(userAuthedInfo);
                if(CommonUtils.isStringEmpty(userAuthedInfo.getUserBase().getThirdparty_access_token())){
//                    startActivity(IntentFactory.createCodeActivityIntent(AppStartActivity.this));
                    startActivity(new Intent(AppStartActivity.this,GoActivity.class));
                    finish();
                }else{
                    startActivity(IntentFactory.createPortalActivityIntent(AppStartActivity.this));
                }
                AppStartActivity.this.finish();
            }
            else
            {
                startActivity(new Intent(AppStartActivity.this,GoActivity.class));
                finish();
            }
        }
    }


}
