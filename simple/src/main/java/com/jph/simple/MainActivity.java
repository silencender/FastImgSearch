package com.jph.simple;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import com.jph.takephoto.TakePhotoActivity;

import java.io.File;
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
    private static final String TAG = "error";
    private ImageView imgShow;
    public String url = null;
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
                String[] datas = {url, "http://android.silenceender.com/upload.php"};
                fileuploadtask.execute(datas);
            }
        });
    }

    public void cropPic(View view) {
        //Uri imageUri = Uri.fromFile(new File(Environment.getDataDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg"));
        url=sDir+System.currentTimeMillis() + ".jpg";
        Uri imageUri = Uri.fromFile(new File(url));
        final Button uploadButton = (Button) findViewById(R.id.buttonUpload);
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                {
                    getTakePhoto().picSelectCrop(imageUri);
                    uploadButton.setVisibility(Button.VISIBLE);
                }
            break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                {
                    getTakePhoto().picTakeCrop(imageUri);
                    uploadButton.setVisibility(Button.VISIBLE);
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
