package com.silen.android;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.silen.android.preferences.MyPreferencesActivity;
import com.silen.takephoto.TakePhotoActivity;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import android.view.View.OnClickListener;
import android.widget.Toast;


/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 上传图片到服务器，进行描述与相关查询
 * @author JPH Silen
 * @Date:2016.4.28
 */
public class MainActivity extends TakePhotoActivity {
    private ImageView imgShow;
    public String old_url = null;
    public String url = null;
    public String picname = null;
    private String picurl = null;
    private boolean weballow = false;
    private int imgsize;
    private Bundle imgdata = new Bundle();    // The button to select an image
    private ImageButton uploadButton;
    private ImageButton desbutton;
    private EditText mEditText;
    private VisionServiceClient client;
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
        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        uploadButton = (ImageButton) findViewById(R.id.uploadButton);
        desbutton = (ImageButton) findViewById(R.id.desButton);
        mEditText = (EditText)findViewById(R.id.editTextResult);
        imgShow = (ImageView) findViewById(R.id.imgShow);
        mEditText.setText("");
        mEditText.setKeyListener(null);
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
        uploadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                imgsize = Integer.parseInt(SP.getString("size","150"));
                if(fileIsExists(url)) {
                    File img = new File(url);
                    if(img.length()/1024<imgsize*1.5){
                        FileUploadTask fileuploadtask = new FileUploadTask();
                        String[] datas = {url, site};
                        fileuploadtask.execute(datas);
                        picurl = imgsite+picname;
                        weballow = true;
                    }
                    else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~", Toast.LENGTH_SHORT).show();
                }
                else if(old_url != null)  {
                    File old_img = new File(old_url);
                    if(old_img.length()/1024<imgsize*1.5){
                        FileUploadTask fileuploadtask = new FileUploadTask();
                        String[] datas = {old_url, site};
                        fileuploadtask.execute(datas);
                        picurl = imgsite+old_url.substring(old_url.length()-17,old_url.length());
                        weballow = true;
                    }
                    else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        desbutton.setEnabled(true);
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

    public void sendMessage(View view){
        if(weballow) weburl=searchEngine+picurl;
        if(weburl != null) {
            Intent intent = new Intent(MainActivity.this, Webview.class);
            intent.putExtra("url", weburl);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.mainActivity, "请先上传图像~", Toast.LENGTH_SHORT).show();
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

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        AnalysisResult v = this.client.describe(picurl, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    public void doDescribe(View view) {
        desbutton.setEnabled(false);
        mEditText.setText("Describing...");

        try {
            new doRequest().execute();
        } catch (Exception e)
        {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    public class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            mEditText.setText("");
            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                //mEditText.append("Image format: " + result.metadata.format + "\n");
                //mEditText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                //mEditText.append("\n");

                for (Caption caption: result.description.captions) {
                    //mEditText.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
                    mEditText.append(caption.text + ".\nConfidence: " + Math.round(caption.confidence*1000)/10.0 + "%\n");
                }
                mEditText.append("Tags:");

                for (String tag: result.description.tags) {
                    mEditText.append(tag + "; ");
                }
                /*
                mEditText.append("\n");

                mEditText.append("\n--- Raw Data ---\n\n");
                mEditText.append(data);
                mEditText.setSelection(0);
                */
            }

            uploadButton.setEnabled(true);
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
    public void takeSuccess(Uri uri,int imgsize) {
        super.takeSuccess(uri,imgsize);
        showImg(uri);
        compressPic(uri.getPath(),imgsize);
    }

    private void showImg(Uri uri) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), option);
        imgShow.setImageBitmap(bitmap);
    }
}