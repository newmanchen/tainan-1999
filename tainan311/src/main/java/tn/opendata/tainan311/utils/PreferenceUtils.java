package tn.opendata.tainan311.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by newman on 5/11/15.
 */
public class PreferenceUtils {

    public static Set<String> getMyRequestIds(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
        return prefs.getStringSet(Constant.KEY_MY_REQUEST_ID, null);
    }

    public static void setMyRequestIds(Context context, HashSet<String> requestIds){
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constant.PREF_NAME, 0).edit();
        prefs.putStringSet(Constant.KEY_MY_REQUEST_ID, requestIds);
        prefs.commit();
    }

    public static boolean getIgnorePref(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
        return !prefs.getBoolean(Constant.KEY_SHOW_TIPS, true); // default show tip
    }

    public static void setIgnoreTipPref(Context context, boolean ignore){
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constant.PREF_NAME, 0).edit();
        prefs.putBoolean(Constant.KEY_SHOW_TIPS, !ignore);
        prefs.commit();
    }
}