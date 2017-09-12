package io.github.chaitya62.tripsharr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by ankit on 30/8/17.
 */

public class SharedPrefs {
    private static Context context;
    private static SharedPreferences pref;
    private static Editor editor;

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public SharedPrefs(Context context) {
        SharedPrefs.context = context;
        pref = context.getSharedPreferences("MetaData", 0);
        editor = pref.edit();
    }

    public static SharedPreferences getPrefs() {
        return pref;
    }

    public static Editor getEditor() {
        return editor;
    }
}
