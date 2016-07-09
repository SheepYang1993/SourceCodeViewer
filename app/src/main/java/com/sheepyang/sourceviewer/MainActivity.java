package com.sheepyang.sourceviewer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sheepyang.sourceviewer.util.StreamUtil;
import com.sheepyang.sourceviewer.util.ToastUtil;
import com.sheepyang.sourceviewer.webview.MyWebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ERROR = 0;
    private static final int REQUEST_FAILED = -1;
    private static final int REQUEST_SUCCESS = 1;
    private EditText edtURL;
    private TextView tvSource;
    private WebView webView;
    private Button btnSourceView;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEven();
    }

    private void initView() {
        edtURL = (EditText) findViewById(R.id.edtURL);
        tvSource = (TextView) findViewById(R.id.tvSource);
        btnSourceView = (Button) findViewById(R.id.btnSourceView);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        //启用支持javascript
       webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //隐藏Zoom缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUserAgentString("电脑");
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        progress = new ProgressDialog(this);
    }

    private void initEven() {
        btnSourceView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSourceView:
                String strUrl = edtURL.getText().toString().trim();
                if (TextUtils.isEmpty(strUrl)) {
                    ToastUtil.show(this, "您还没有填写URL路径!");
                    tvSource.setText("请填写URL路径!");
                } else {
                    if (!strUrl.contains("http://")) {
                        strUrl = "http://" + strUrl;
                    }
                    watchSourceCode(strUrl);
                }
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REQUEST_SUCCESS) {
                tvSource.setText((String) msg.obj);
                progress.dismiss();
                ToastUtil.show(MainActivity.this, "请求成功!");
            } else if (msg.what == REQUEST_FAILED) {
                tvSource.setText("请求失败!");
                progress.dismiss();
                ToastUtil.show(MainActivity.this, "请求失败!");
            } else if (msg.what == REQUEST_ERROR) {
                String strMsg = ((Exception)msg.obj).toString();
                tvSource.setText(strMsg);
                progress.dismiss();
                ToastUtil.show(MainActivity.this, "请求失败!");
            }
        }
    };

    private void watchSourceCode(final String strUrl) {
        progress.setMessage("正在加载网页...");
        progress.show();
        webView.loadUrl(strUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strSourceCode = "";
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream is = connection.getInputStream();
                        strSourceCode = StreamUtil.streamToString(is);
                        Message msg = Message.obtain();
                        msg.what = REQUEST_SUCCESS;
                        msg.obj = strSourceCode;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = REQUEST_FAILED;
                        mHandler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = REQUEST_ERROR;
                    msg.obj = e;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = REQUEST_ERROR;
                    msg.obj = e;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }
}
