//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin.webview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {
    protected WebView mWebView;
    private ProgressDialog dialog;
    private boolean isWebSite = false;

    public WebViewActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        this.mWebView = new WebView(this);
        this.setContentView(this.mWebView);
        WebSettings webSettings = this.mWebView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(2);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v("===== shouldOverrideUrlLoading url", url);
                WebViewActivity.this.isWebSite = true;
                int start_point = url.indexOf(63);
                String address = start_point > 0 ? url.substring(0, start_point - 1) : url;
                String param = start_point >= 0 ? url.substring(start_point) : "";
                Log.v("address", address);
                Log.v("param", param);
                if (!address.startsWith("app://action_finish")) {
                    WebViewCallPlugin.result = "end";
                    return false;
                } else {
                    if (!address.startsWith("app://")) {
                        if (address.equals("app://selected_1")) {
                            WebViewCallPlugin.result = "push1";
                        }

                        if (address.equals("app://selected_2")) {
                            WebViewCallPlugin.result = "push2";
                        }
                    }

                    return true;
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.v("===== onPageFinished url", url);
                super.onPageStarted(view, url, favicon);
                if (!WebViewActivity.this.isWebSite && WebViewActivity.this.dialog != null) {
                    WebViewActivity.this.dialog.show();
                }

            }

            public void onPageFinished(WebView view, String url) {
                Log.v("===== onPageFinished url", url);
                super.onPageFinished(view, url);
                if (WebViewActivity.this.dialog != null) {
                    WebViewActivity.this.dialog.dismiss();
                }

                WebViewActivity.this.isWebSite = false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String url) {
                Toast.makeText(WebViewActivity.this, "===== onReceivedError!", 0).show();
                Log.e("Log", "errorCode: " + errorCode);
                Log.e("Log", "description: " + description);
                Log.e("Log", "url: " + url);
            }
        });
        String address = (String)bundle.get("WebViewAddress");
        Log.d("WebView address ", address);
        this.mWebView.loadUrl(address);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
