package com.agora.iotlink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppStorageUtil {

    ////////////////////////////////////////////////////////////////////////
    //////////////////////// Constant Definition ///////////////////////////
    ////////////////////////////////////////////////////////////////////////

    //
    // 应用存储参数键值
    //
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_IDENTITYID = "KEY_IDENTITYID";
    public static final String KEY_ENDPOINT = "KEY_ENDPOINT";
    public static final String KEY_POOLTOKEN = "KEY_POOLTOKEN";
    public static final String KEY_IDENTIFIER = "KEY_IDENTIFIER";
    public static final String KEY_IDENTIFIERPOOLID = "KEY_IDENTIFIERPOOLID";
    public static final String KEY_REGION = "KEY_REGION";


    //////////////////////////////////////////////////////////////////
    ////////////////////// Public Methods ///////////////////////////
    //////////////////////////////////////////////////////////////////

    private static SharedPreferences sharedPreferences;
    private volatile static AppStorageUtil instance;

    public static AppStorageUtil init(Context context) {
        if (instance == null) {
            synchronized (AppStorageUtil.class) {
                if (instance == null) {
                    instance = new AppStorageUtil(context);
                }
            }
        }
        return instance;
    }

    private AppStorageUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("IoTDemo", Context.MODE_PRIVATE);
    }

    public static void keepShared(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void keepShared(String key, Integer value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void keepShared(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void keepShared(String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void keepShared(String key, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String queryValue(String key, String defvalue) {
        String value = sharedPreferences.getString(key, defvalue);
        // if ("".equals(value)) {
        // return "";
        // }
        return value;
    }

    public static String queryValue(String key) {
        String value = sharedPreferences.getString(key, "");
        if ("".equals(value)) {
            return "";
        }

        return value;
    }

    public static Integer queryIntValue(String key) {
        int value = sharedPreferences.getInt(key, 0);
        return value;
    }

    public static Integer queryIntValue(String key, int defalut) {
        int value = sharedPreferences.getInt(key, defalut);
        return value;
    }

    public static boolean queryBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static long queryLongValue(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public static boolean deleteAllValue() {

        return sharedPreferences.edit().clear().commit();
    }

    public static void deleteValue(String key) {
        sharedPreferences.edit().remove(key).commit();
    }
}
