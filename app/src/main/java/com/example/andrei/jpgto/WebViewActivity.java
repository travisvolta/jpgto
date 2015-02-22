package com.example.andrei.jpgto;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        WebView webView = (WebView)findViewById(R.id.webview);
        Intent intent = this.getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        webView.loadUrl(imageUrl);
        String title = intent.getStringExtra("title");

       //setActionBarTitle(title);


        //JS needed to set background color
        webView.getSettings().getJavaScriptEnabled();

        webView.setBackgroundColor(Color.LTGRAY);

        //fixes scaling issue
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //fixes issue with opening image in browser window
        webView.setWebViewClient(new MyWebViewClient());
    }
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setActionBarTitle(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
        ActionBar ab = getActionBar();
        ab.setTitle(title);}
    }

}

//this class fixes issue with opening image in browser instead of this webView

 class MyWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        view.loadUrl(url);

        return false;
    }
}
