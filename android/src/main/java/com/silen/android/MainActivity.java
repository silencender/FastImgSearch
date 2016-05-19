package com.silen.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.silen.android.TakePhotoActivity;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 上传图片到服务器，进行描述与相关查询
 * @author JPH Silen
 * @Date:2016.4.28
 */
public class MainActivity extends TakePhotoActivity {
    private ImageView imgShow;
    private String old_url = null;
    private String url = null;
    private String picname = null;
    private String picurl = null;
    private boolean weballow = false;
    private int imgsize;
    private Bundle imgdata = new Bundle();    // The button to select an image
    private ImageButton preButton;
    private ImageButton desButton;
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
        Window window = mainActivity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ViewGroup mContentView = (ViewGroup) mainActivity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        preButton = (ImageButton) findViewById(R.id.preButton);
        desButton = (ImageButton) findViewById(R.id.desButton);
        mEditText = (EditText)findViewById(R.id.editTextResult);
        //imgShow = (ImageView) findViewById(R.id.imgShow);
        mEditText.setText("");
        mEditText.setKeyListener(null);
        mEditText.setTypeface(Typeface.SERIF);
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

        /*
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
                    else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~1", Toast.LENGTH_SHORT).show();
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
                    else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~2", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.mainActivity, "当前没有可用于上传的图片哦~3"+url, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public void onResume(){
        super.onResume();
        desButton.setEnabled(true);
        imgsize = Integer.parseInt(SP.getString("size","150"));
        mEditText.setText("");

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
        weballow = true;
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
        desButton.setEnabled(false);
        mEditText.setText("Please wait a moment, I'm thinking...");

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
                //mEditText.setText("Error: " + e.getMessage());
                mEditText.setText("Oops! Maybe the image is not yet uploaded, or the Internet connection needs to be reset :-)");
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                //mEditText.append("Image format: " + result.metadata.format + "\n");
                //mEditText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                //mEditText.append("\n");

                for (Caption caption: result.description.captions) {
                    //mEditText.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
                    //mEditText.append(caption.text + ".\nConfidence: " + Math.round(caption.confidence*1000)/10.0 + "%\n");
                    if(caption.confidence>0.7) mEditText.append("I'm sure that it is "+caption.text +"! :-)\n");
                    else if(caption.confidence>0.3) mEditText.append("I'm not very sure, but is it "+caption.text +"?\n");
                    else mEditText.append("So hard to recognize this picture:-( But it looks like "+caption.text +".\n");
                }
                mEditText.append("\nAnd I found following keywords related to this picture: ");

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
/*
    private void showImg(Uri uri) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), option);
        imgShow.setImageBitmap(bitmap);
    }
    */
}