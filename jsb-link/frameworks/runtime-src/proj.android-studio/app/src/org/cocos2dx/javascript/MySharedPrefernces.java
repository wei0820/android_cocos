package org.cocos2dx.javascript;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
public class MySharedPrefernces {
    public static final String KEY_IS_TOKEN= "isToken";
    public static final String  NAME = "MySharedPrefernces";

    public static void saveIsToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
        sp.edit().putString(KEY_IS_TOKEN, token).apply();
    }

    public static String getIsToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
        return sp.getString(KEY_IS_TOKEN, "");
    }
}
