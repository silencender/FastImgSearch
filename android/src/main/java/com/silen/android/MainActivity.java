package com.silen.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.silen.android.preferences.MyPreferencesActivity;

import java.io.File;

public class MainActivity extends TakePhotoActivity {
    private String old_url = null;
    private String url = null;
    private String puturl = null;
    private String picname = null;
    private String picurl = null;
    private boolean weballow = false;
    private int imgsize;
    private Bundle imgdata = new Bundle();    // The button to select an image
    private SharedPreferences SP;
    private String searchEngine;
    private String site;
    private String imgsite;
    private String weburl = null;
    public static MainActivity mainActivity;
    private String sDir = Environment.getExternalStorageDirectory().getPath() + "/FastImgSearch/temp/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = MainActivity.this;
        //状态栏透明
        Window window = mainActivity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ViewGroup mContentView = (ViewGroup) mainActivity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
        //创建图片目录
        File destDir = new File(sDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //获取偏好设置
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        getPreference();
        //接收隐式intent并交由picCrop处理
        Intent intent = getIntent();
        try{
            if (intent.getType().indexOf("image/") != -1) {
                Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                setUri();
                imgdata.putString("imgurl", url);
                getTakePhoto().picCrop(imgdata, data);
            }
        }
        catch (Exception e){}
    }

    @Override
    public void onResume(){
        super.onResume();
        getPreference();
        if(fileIsExists(url)) old_url = url;
    }

    private void getPreference(){
        imgsize = Integer.parseInt(SP.getString("size","100"));
        if(Integer.parseInt(SP.getString("selectEngine","1")) == 1) searchEngine = getString(R.string.googleEngine);
        else if(Integer.parseInt(SP.getString("selectEngine","1")) == 2) searchEngine = getString(R.string.baiduEngine);
        if(Integer.parseInt(SP.getString("selectServer","1")) == 1){
            site = getString(R.string.shanghaisite);
            imgsite = getString(R.string.shanghaiimgsite);
        }
        else if(Integer.parseInt(SP.getString("selectServer","1")) == 2){
            site = getString(R.string.LAsite);
            imgsite = getString(R.string.LAimgsite);
        }

        imgdata.putInt("imgsize",imgsize);
        imgdata.putString("imgsite",site);
    }

    public void openSettings(View view){
        Intent i = new Intent(this, MyPreferencesActivity.class);
        startActivity(i);
    }

    public void changepicurl(){
        picurl = imgsite + picname;
        puturl = url;
        weballow = true;
    }

    public void openwebview(View view){
        if(weballow) {
            Intent intent = new Intent(MainActivity.this, Webview.class);
            Bundle data = new Bundle();
            data.putString("searchEngine", searchEngine);
            data.putString("picurl", picurl);
            intent.putExtra("data",data);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.mainActivity, "请先上传图像~", Toast.LENGTH_SHORT).show();
        }

    public void opendescribe(View view){
        if(weballow) weburl=searchEngine+picurl;
        if(weburl != null) {
            Intent intent = new Intent(MainActivity.this, Describe.class);
            intent.putExtra("weburl", weburl);
            intent.putExtra("picurl", picurl);
            intent.putExtra("url", puturl);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.mainActivity, "请先上传图像~", Toast.LENGTH_SHORT).show();
    }

    private boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(f == null||!f.exists()||!f.isFile())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private void setUri(){
        picname = System.currentTimeMillis() + ".jpg";
        url=sDir+picname;
    }

    public void cropPic(View view) {

        switch (view.getId()) {
            case R.id.galleryButton://从相册选择照片进行裁剪
                {
                    setUri();
                    imgdata.putString("imgurl",url);
                    getTakePhoto().picSelectCrop(imgdata);
                }
            break;
            case R.id.cameraButton://从相机拍取照片进行裁剪
                {
                    setUri();
                    imgdata.putString("imgurl",url);
                    getTakePhoto().picTakeCrop(imgdata);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(String msg) {
        super.takeFail(msg);
    }

    @Override
    public void takeSuccess(Uri uri,int imgsize, String site) {
        super.takeSuccess(uri,imgsize,site);
        //showImg(uri);
        compressPic(uri.getPath(),imgsize,site);
    }
}