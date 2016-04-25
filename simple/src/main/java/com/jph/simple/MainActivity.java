package com.jph.simple;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import com.jph.takephoto.TakePhotoActivity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * @author JPH
 * @Date:2014.10.09
 */
public class MainActivity extends TakePhotoActivity {
    private ImageView imgShow;
    public String old_url = null;
    public String url = null;
    public String picname = null;
    public Uri imageUri = null;
    //public final String site = "http://android.silenceender.com/upload.php";
    //public final String imgsite = "http://android.silenceender.com/image/";
    public final String site = "http://182.254.214.148/upload.php";
    public final String imgsite = "http://182.254.214.148/image/";
    public final String googleEngine = "https://www.google.com/searchbyimage?&image_url=";
    public final String baiduEngine = "http://image.baidu.com/n/pc_search?queryImageUrl=";
    public String weburl = null;
    public static MainActivity mainActivity;
    public String sDir = Environment.getExternalStorageDirectory().getPath() + "/FastImgSearch/temp/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));
        mainActivity = MainActivity.this;
        imgShow = (ImageView) findViewById(R.id.imgShow);
        File destDir = new File(sDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        final Button uploadButton = (Button) findViewById(R.id.buttonUpload);

        uploadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileIsExists(url)) {
                    FileUploadTask fileuploadtask = new FileUploadTask();
                    String[] datas = {url, site};
                    fileuploadtask.execute(datas);
                    weburl=googleEngine+imgsite+picname;
                }
                else if(old_url != null)  {
                    FileUploadTask fileuploadtask = new FileUploadTask();
                    String[] datas = {old_url, site};
                    fileuploadtask.execute(datas);
                    weburl=googleEngine+imgsite+old_url.substring(old_url.length()-17,old_url.length());
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        final Button uploadButton = (Button) findViewById(R.id.buttonUpload);
        final Button searchButton = (Button) findViewById(R.id.Search);
        if(fileIsExists(url)){
            uploadButton.setVisibility(Button.VISIBLE);
            old_url = url;
        }
        if(old_url != null || fileIsExists(url)) searchButton.setVisibility(Button.VISIBLE);
    }

    public void sendMessage(View view){
        if(weburl != null) {
            Intent intent = new Intent(MainActivity.this, Webview.class);
            intent.putExtra("url", weburl);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.mainActivity, "请先上传图像~", Toast.LENGTH_LONG).show();
        }

    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
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
    /*
            ProgressDialog dialog = new ProgressDialog(MainActivity.mainActivity);
            dialog.setMessage(storeurl);
            dialog.setIndeterminate(false);
            dialog.show();
*/
/*
    public static int getResponseCode(String urlString) throws IOException {
        URL u = new URL(urlString);
        HttpURLConnection web =  (HttpURLConnection)  u.openConnection();
        web.setRequestMethod("HEAD");
        web.connect();
        return web.getResponseCode();
    }
*/
    public void setUri(){
        picname = System.currentTimeMillis() + ".jpg";
        url=sDir+picname;
        imageUri = Uri.fromFile(new File(url));
    }

    public void cropPic(View view) {
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                {
                    setUri();
                    getTakePhoto().picSelectCrop(imageUri);
                }
            break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                {
                    setUri();
                    getTakePhoto().picTakeCrop(imageUri);
                }
                break;
            /*
            case R.id.btnOriginal://从相册选择照片不裁切
                getTakePhoto().picSelectOriginal(imageUri);
                break;
            case R.id.btnTakeOriginal://从相机拍取照片不裁剪
                getTakePhoto().picTakeOriginal(imageUri);
                break;
                */
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
    public void takeSuccess(Uri uri) {
        super.takeSuccess(uri);
        showImg(uri);
        compressPic(uri.getPath());
    }

    private void showImg(Uri uri) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), option);
        imgShow.setImageBitmap(bitmap);
    }
}