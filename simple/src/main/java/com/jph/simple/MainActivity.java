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



/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * @author JPH
 * @Date:2014.10.09
 */
public class MainActivity extends TakePhotoActivity {
    private ImageView imgShow;
    public String url = null;
    public String picname = null;
    public Uri imageUri = null;
    public final String site = "http://android.silenceender.com/upload.php";
    public final String imgsite = "http://android.silenceender.com/image/";
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
                FileUploadTask fileuploadtask = new FileUploadTask();
                String[] datas = {url, site};
                fileuploadtask.execute(datas);
            }
        });
    }

    public void sendMessage(View view){
        String storeurl = imgsite+picname;
        Intent intent = new Intent(MainActivity.this, Webview.class);
        intent.putExtra("url", storeurl);
        startActivity(intent);
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
        //Uri imageUri = Uri.fromFile(new File(Environment.getDataDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg"));
        final Button uploadButton = (Button) findViewById(R.id.buttonUpload);
        final Button searchButton = (Button) findViewById(R.id.Search);
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                {
                    setUri();
                    getTakePhoto().picSelectCrop(imageUri);
                    uploadButton.setVisibility(Button.VISIBLE);
                    searchButton.setVisibility(Button.VISIBLE);
                }
            break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                {
                    setUri();
                    getTakePhoto().picTakeCrop(imageUri);
                    uploadButton.setVisibility(Button.VISIBLE);
                    searchButton.setVisibility(Button.VISIBLE);
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
