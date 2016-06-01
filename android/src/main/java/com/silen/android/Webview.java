package com.silen.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.silen.android.view.ProgressBarWebView;

public class Webview extends AppCompatActivity {
    private ProgressBarWebView webView;
    private String picurl = null;
    private String searchEngine = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
        searchEngine = data.getString("searchEngine");
        picurl = data.getString("picurl");
        setContentView(R.layout.activity_webview);
        //setTitle("Browser");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Browser");
        setSupportActionBar(myToolbar);
        webView = (ProgressBarWebView) findViewById(R.id.webView);
        webView.loadUrl(searchEngine+picurl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_engine) {
            if(searchEngine == getString(R.string.googleEngine)) {
                searchEngine = getString(R.string.baiduEngine);
                webView.loadUrl(searchEngine+picurl);
            }
            else {
                searchEngine = getString(R.string.googleEngine);
                webView.loadUrl(searchEngine+picurl);
            }
            return true;
        }

        if (id == R.id.action_clearwebcache) {
            webView.clearCache(true);
            Toast.makeText(MainActivity.mainActivity, "缓存清除完毕！", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

