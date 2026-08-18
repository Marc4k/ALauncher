package dev.dworks.apps.alauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import amirz.App;

public class PreferenceUtils {

    public static Boolean getBooleanPrefs(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static Boolean getBooleanPrefs(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static Boolean getBooleanPrefs(String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getBoolean(key, false);
    }

    public static Boolean getBooleanPrefs(String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getBoolean(key, defaultValue);
    }

    public static String getStringPrefs(String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getString(key, "");
    }

    public static String getStringPrefs(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static String getStringPrefs(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static int getIntegerPrefs(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static int getIntegerPrefs(String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getInt(key, 0);
    }

    public static int getIntegerPrefs(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    public static long getLongPrefs(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
    }

    public static long getLongPrefs(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    public static Long getLongPrefs(String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getLong(key, 0);
    }

    public static long getLongPrefs(String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getLong(key, defaultValue);
    }

    public static Set<String> getStrinSetPrefs(String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext())
                .getStringSet(key, new HashSet<>());
    }

    public static void set(String key, Object value) {
        set(PreferenceManager.getDefaultSharedPreferences(App.getInstance().getBaseContext()),
                key, value);
    }

    public static void set(Context context, String key, Object value) {
        set(PreferenceManager.getDefaultSharedPreferences(context), key, value);
    }

    @SuppressWarnings("unchecked")
    public static void set(SharedPreferences preferences, String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.apply();
    }

    public static void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }
}
