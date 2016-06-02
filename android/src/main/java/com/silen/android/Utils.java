package com.silen.android;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Author: JPH
 * Date: 2015/8/26 0026 16:23
 */
public class Utils {

    public static ProgressDialog showProgressDialog(final Activity activity,
                                                    String... progressTitle) {
        if(activity==null||activity.isFinishing())return null;
        String title = "提示";
        if (progressTitle != null && progressTitle.length > 0)
            title = progressTitle[0];
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
