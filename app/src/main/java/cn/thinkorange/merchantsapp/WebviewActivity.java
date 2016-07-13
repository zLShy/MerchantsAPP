package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebviewActivity extends Activity {

    private WebView web;
    private Dialog pd;
    private String url;
    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        tv = (TextView) findViewById(R.id.title_name_tv);
        tv.setVisibility(View.INVISIBLE);

        iv = (ImageView) findViewById(R.id.getback);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.web = (WebView) findViewById(R.id.web);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        pd = new Dialog(WebviewActivity.this, R.style.new_circle_progress);
        pd.setContentView(R.layout.layout_progressbar);
        pd.setCancelable(true);

        this.web.getSettings().setJavaScriptEnabled(true);

        this.web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        this.web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.cancel();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                web.loadUrl(url);
            }
        });

        web.loadUrl(url);
    }


}