package com.silen.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.silen.android.CompressImageUtil.CompressListener;

public class TakePhotoActivity extends AppCompatActivity implements TakePhoto.TakeResultListener,CompressListener {
    private TakePhoto takePhoto;
    protected ProgressDialog wailLoadDialog;
    protected TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto=new TakePhoto(this,this);
        }
        return takePhoto;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        getTakePhoto().onResult(requestCode,resultCode, data);
        super.onActivityResult(requestCode,resultCode, data);
    }
    @Override
    public void takeSuccess(Uri uri,int imgsize, String site) {
        Log.i("info", "takeSuccess：" + uri);
    }
    @Override
    public void takeFail(String msg) {
        Log.w("info", "takeFail:" + msg);
    }
    @Override
    public void takeCancel() {
        Log.w("info", "用户取消");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (takePhoto!=null)outState.putParcelable("imageUri", takePhoto.getImageUri());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        getTakePhoto().setImageUri((Uri)savedInstanceState.getParcelable("imageUri"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void compressPic(String path,int imgsize, String site) {
        wailLoadDialog = Utils.showProgressDialog(TakePhotoActivity.this,"正在压缩照片...");// 提交数据
        new CompressImageUtil().compressImageByPixel(path,imgsize,site,this);
    }
    @Override
    public void onCompressSuccessed(String imgPath) {
        if (wailLoadDialog!=null)wailLoadDialog.dismiss();
    }
    @Override
    public void onCompressFailed(String msg) {
        if (wailLoadDialog!=null)wailLoadDialog.dismiss();
    }
}
