package com.silen.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.IOException;

public class Describe extends Activity {

    private ImageView imgShow;
    private EditText mEditText;
    private String picurl = null;
    private VisionServiceClient client;
    private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_describe);
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        try {
            params.width = (int) ((d.getWidth()) * 0.8);
            params.height = (int) ((d.getHeight()) * 0.6);
        }
        catch (Exception e){}
        this.getWindow().setAttributes(params);

        /*
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
        */
        /*
        rootView = findViewById(R.id.root);

        if (savedInstanceState == null) {
            rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startRootAnimation();
                    return true;
                }
            });
        }
        */
        Intent intent = getIntent();
        String weburl = intent.getStringExtra("weburl");
        picurl = intent.getStringExtra("picurl");
        String url = intent.getStringExtra("url");
        mEditText = (EditText)findViewById(R.id.editTextResult);
        mEditText.setText("");
        mEditText.setKeyListener(null);
        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }
        imgShow= (ImageView) findViewById(R.id.imgShow);
        showImg(url);
        doDescribe();
    }

    private void startRootAnimation() {
        rootView.setScaleY(0.1f);
        rootView.setScaleX(0.1f);
        rootView.setPivotY(rootView.getY() + rootView.getHeight() / 2);
        rootView.setPivotX(rootView.getX() + rootView.getWidth() / 2);

        rootView.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

    private void showImg(String url){
        BitmapFactory.Options option=new BitmapFactory.Options();
        option.inSampleSize=2;
        Bitmap bitmap=BitmapFactory.decodeFile(url,option);
        Bitmap b = null;
        try{
            b = toRoundCorner(bitmap, 10);
        }
        catch (Exception e) {
            b =bitmap;
        }
        imgShow.setImageBitmap(b);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }


    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        AnalysisResult v = this.client.describe(picurl, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    public void doDescribe() {
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

                for (Caption caption: result.description.captions) {
                    //mEditText.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
                    //mEditText.append(caption.text + ".\nConfidence: " + Math.round(caption.confidence*1000)/10.0 + "%\n");
                    if(caption.confidence>0.7) mEditText.append("I'm sure that it is "+caption.text +"! :-)\n");
                    else if(caption.confidence>0.3) mEditText.append("I'm not very sure, but is it "+caption.text +"?\n");
                    else mEditText.append("So hard to recognize this picture:-( But it looks like "+caption.text +".\n");
                }
                /*
                mEditText.append("\nAnd I found following keywords related to this picture: ");

                boolean n = false;
                for (String tag: result.description.tags) {
                    if(n)  mEditText.append("; ");
                    else n = true;
                    mEditText.append(tag);
                }
                mEditText.append(".");
                */
            }
        }
    }
}
