package com.my.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by c_ljf on 17-1-17.
 */
public class SharePreferrenceUtil   {
    public static void putString(Context context,String key,String value){
        SharedPreferences preferences=context.getSharedPreferences(MyConstant.SPFILENAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public static String getString(Context context,String key,String defValue){
        SharedPreferences preferences = context.getSharedPreferences(MyConstant.SPFILENAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }
}
