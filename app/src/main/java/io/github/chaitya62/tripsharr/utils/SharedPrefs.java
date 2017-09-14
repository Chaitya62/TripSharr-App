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
    private static SharedPreferences pref;
    private static Editor editor;

    public SharedPrefs(Context context) {
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
