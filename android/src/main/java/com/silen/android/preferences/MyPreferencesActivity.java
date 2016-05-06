package com.silen.android.preferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.silen.android.MainActivity;
import com.silen.android.R;

import java.io.File;

/**
 * Created by Silen on 4/26/2016.
 */
public class MyPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference button = (Preference)findPreference("clearCache");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String sDir = Environment.getExternalStorageDirectory().getPath() + "/FastImgSearch/temp/";
                    File temp = new File(sDir);
                    File[] tempfiles = temp.listFiles();
                    if(tempfiles.length != 0){
                        for(int i = 0; i<tempfiles.length; i++) tempfiles[i].delete();
                        Toast.makeText(MainActivity.mainActivity, "缓存清除完毕！", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(MainActivity.mainActivity, "无缓存文件哦~", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

}

