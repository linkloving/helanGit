package com.VitaBit.VitaBit.logic.UI.launch;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;

/**
 * Created by Administrator on 2016/8/8.
 */
public class LoginWebActivity extends ToolBarActivity {
    private static final String TAG = "LoginWebActivity";
    WebView webView;
    String weburl = "https://app.vitabit.software/Endpoints/Authorize/Init/8C9BEFAE-E735-4ADE-85D9-A22D1056F927";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_common_web);

    }

    @Override
    protected void getIntentforActivity() {
    }

    @Override
    protected void initView() {
        SetBarTitleText("Get your code");

        HideButtonRight(false);
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(com.VitaBit.VitaBit.R.string.general_next));
        btn.setTextColor(getResources().getColor(com.VitaBit.VitaBit.R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(IntentFactory.createCodeActivityIntent(LoginWebActivity.this));
                LoginWebActivity.this.finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        webView = (WebView) findViewById(com.VitaBit.VitaBit.R.id.fuwu_web_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webSettings.setBuiltInZoomControls(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setSaveFormData(false);
//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.postUrl(url,null);
        MyLog.i(TAG, "传进来的url是："+weburl);
        webView.setWebViewClient(new mWebViewClient());
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
                //这里将textView换成你的progress来设置进度
                if (newProgress == 0)
                {
                    MyLog.e("TAG", "开始加载...");
                }
                if (newProgress == 100)
                {
                    MyLog.e("TAG", "加载完成...");
                    progressDialog.dismiss();
                }
            }
        });
//        String htmlString = "<h1>Title</h1><p>This is HTML text<br /><i>Formatted in italics</i><br />Anothor Line</p>";
//// 载入这个html页面
//        webView.loadData(htmlString, "text/html", "utf-8");
        webView.loadUrl(weburl);
    }
    private class mWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            //忽略证书的错误继续Load页面内容
            handler.proceed();
            //handler.cancel(); // Android默认的<strong>处理</strong>方式
            //handleMessage(Message msg); // 进行其他<strong>处理</strong>
        }
    }
    @Override
    protected void initListeners() {

    }

    /**
     * 捕获back键
     */
    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack())
            webView.goBack(); //goBack()表示返回WebView的上一页面
        else
            super.onBackPressed();
    }

}

