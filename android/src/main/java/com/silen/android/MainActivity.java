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


/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 上传图片到服务器，进行描述与相关查询
 * @author JPH Silen
 * @Date:2016.4.28
 */
public class MainActivity extends TakePhotoActivity {
    private String old_url = null;
    private String url = null;
    private String puturl = null;
    private String picname = null;
    private String picurl = null;
    private boolean weballow = false;
    private int imgsize;
    private Bundle imgdata = new Bundle();    // The button to select an image
    private ImageButton preButton;
    private ImageButton desButton;
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
        setTitle("Searcher");
        setContentView(R.layout.activity_main);
        mainActivity = MainActivity.this;
        Window window = mainActivity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ViewGroup mContentView = (ViewGroup) mainActivity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        preButton = (ImageButton) findViewById(R.id.preButton);
        desButton = (ImageButton) findViewById(R.id.desButton);

        File destDir = new File(sDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
        preButton.setBackgroundResource(R.drawable.imagename);

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
        desButton.setEnabled(true);
        imgsize = Integer.parseInt(SP.getString("size","150"));

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
        if(fileIsExists(url)) old_url = url;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_clearcache) {
            File temp = new File(sDir);
            File[] tempfiles = temp.listFiles();
            if(tempfiles.length != 0){
                for(int i = 0; i<tempfiles.length; i++) tempfiles[i].delete();
                Toast.makeText(MainActivity.mainActivity, "缓存清除完毕！", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(MainActivity.mainActivity, "无缓存文件哦~", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
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

    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(f==null||!f.exists()||!f.isFile())
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

    public void setUri(){
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