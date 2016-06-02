package com.silen.android.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ProgressBarWebView extends WebView {
    private ProgressBar progressBar;
    private Handler handler;
    private WebView _this;
    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setVisibility(GONE);
        addView(progressBar);
        handler = new Handler();
        _this = this;
        setWebChromeClient(new MyWebChromeClient());
        setWebViewClient(new MyWebClient());

    }

   private class MyWebChromeClient extends WebChromeClient {
       @Override
       public void onProgressChanged(WebView view, int newProgress) {
           if(newProgress == 100){
               progressBar.setProgress(100);
               handler.postDelayed(runnable,200);
           }else if(progressBar.getVisibility() == GONE){
               progressBar.setVisibility(VISIBLE);
           }
           progressBar.setProgress(newProgress);
           super.onProgressChanged(view, newProgress);
       }
   }
   private class MyWebClient extends WebViewClient {
       @Override
       public boolean shouldOverrideUrlLoading(WebView view, String url) {
           _this.getSettings().setJavaScriptEnabled(true);
           _this.loadUrl(url);
           return true;
       }
       /*
       @Override
       public void onPageFinished(WebView view, String url) {
           super.onPageFinished(view, url);
           view.clearCache(true);
       }
       */
   }

   private Runnable runnable = new Runnable() {
       @Override
       public void run() {
           progressBar.setVisibility(View.GONE);
       }
   };
}
