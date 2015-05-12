package tn.opendata.tainan311.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;

public class EasyUtil {
    public static  <T extends View> T findView(View v,int id){
        return  (v != null) ? (T) v.findViewById(id) : null;
    }

    public static  <T extends View> T findView(Activity v,int id){
        return (v != null) ? (T) v.findViewById(id) : null;
    }

    public static  void close(Closeable c){
        if(c != null){
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** If the argument is non-null, close the cursor. */
    public static void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public static String defaultText(String text,String defaultText){
        return TextUtils.isEmpty(text) ? defaultText : text;
    }

    public static boolean isNotEmpty(String str){
        return !TextUtils.isEmpty(str);
    }

    public static void NOT_IMPLELENT(Context c){
        Toast.makeText(c,"需要有人實作...",Toast.LENGTH_SHORT).show();
    }
}
