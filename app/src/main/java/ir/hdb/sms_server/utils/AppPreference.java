package ir.hdb.sms_server.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {

    // declare context
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    // singleton
    @SuppressLint("StaticFieldLeak")
    private static AppPreference appPreference = null;

    // common
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public static AppPreference getInstance(Context context) {
        if (appPreference == null) {
            mContext = context;
            appPreference = new AppPreference();
        }
        return appPreference;
    }

    @SuppressLint("CommitPrefEdits")
    private AppPreference() {
        String APP_PREF_NAME = "SMS_SERVER";
        sharedPreferences = mContext.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }


    public String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    public float getFloat(String version, float v) {
        return sharedPreferences.getFloat(version, v);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public int getInteger(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void removeKey(String key) {
        editor.remove(key).commit();
    }

    public void clearAll() {
        editor.clear().commit();
    }

}
