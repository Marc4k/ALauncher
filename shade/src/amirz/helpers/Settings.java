package amirz.helpers;

import static android.content.Intent.ACTION_SENDTO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.util.Themes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amirz.shade.util.Snackbar;

public class Settings {
    private final SharedPreferences mSharedPreferences;

    public static final String SUPPORT_EMAIL = "support@dworks.io";

    public Settings(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static Settings instance(Context context) {
        return new Settings(context);
    }

    public static void showSnackBar(Activity context, int resId) {
        Snackbar.show(context, resId);
    }

    public static void showSnackBar(Activity context, String text) {
        Snackbar.show(context, text);
    }

    public static void showSnackBar(Activity context, String text, String action, Runnable runnable) {
        Snackbar.show(context, text, action, runnable);
    }

    public static int getNotificationColor(Context context) {
        return IconPalette.getMutedColor(Themes.getShadeColorAccent(context), 0.2f);
    }

    public static boolean isTransparentTone(Context context) {
        int overlayEndScrim = Themes.getAttrColor(context, R.attr.shadeColorAllAppsOverlay);
        boolean isDark = Themes.getAttrBoolean(context, R.attr.isMainColorDark);
        int alpha = Color.alpha(overlayEndScrim);
        return !isDark && alpha == 0;
    }

    public static int getAllAppsTextColor(Context context) {
        return Themes.getAttrColor(context, R.attr.workspaceTextColor);
    }

    public static void setAllAppsTextColor(BubbleTextView icon) {
        Context context = icon.getContext();
        if(isTransparentTone(context)) {
            int textColor = Themes.getAttrColor(icon.getContext(), R.attr.workspaceTextColor);
            icon.setTextColor(textColor);
        }
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static String getDeviceDetails(Activity activity){
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceModelName = "";
        if (model.startsWith(manufacturer)) {
            deviceModelName =  model;
        } else {
            deviceModelName = manufacturer + " " + model;
        }
        Locale locale = null;
        if(Utilities.ATLEAST_NOUGAT){
            locale = activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = activity.getResources().getConfiguration().locale;
        }
        String versionName = BuildConfig.VERSION_NAME;
        String deviceDetails = "";
        deviceDetails += "App package: " + BuildConfig.APPLICATION_ID + " \n";
        deviceDetails += "App version: " + versionName + " \n";
        deviceDetails += "Current date: " + dateFormat.format(currentDate) + " \n";
        deviceDetails += "Device: " + deviceModelName + " \n";
        deviceDetails += "OS version: Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ") \n";
        if(null != locale) {
            deviceDetails += "Country: " + locale.getDisplayCountry() + " \n";
            deviceDetails += "Language: " + locale.getDisplayLanguage() + " \n";
        }
        return deviceDetails;
    }

    public static void openFeedback(Activity activity){
        sendEmail(activity, "Send Feedback", "ALauncher Feedback", getDeviceDetails(activity));
    }

    public static void sendError(Activity activity, String details){
        sendEmail(activity, "Report Error", "ALauncher Error", getDeviceDetails(activity) + details);
    }

    public static void sendEmail(Activity activity, String title, String subject, String details){
        final Intent result = new Intent(ACTION_SENDTO);
        result.setData(Uri.parse("mailto:"));
        result.putExtra(Intent.EXTRA_EMAIL, new String[]{SUPPORT_EMAIL});
        result.putExtra(Intent.EXTRA_SUBJECT, subject);
        String text = subject + " v" + BuildConfig.VERSION_NAME;
        if(!TextUtils.isEmpty(details)){
            text = details;
        }
        text += "\n\nFeedback: \n";
        result.putExtra(Intent.EXTRA_TEXT, text);

        activity.startActivity(Intent.createChooser(result, title));
    }

    public static boolean isActivityAlive(Activity activity) {
        return !(null == activity || activity.isDestroyed());
    }

    public static final String GOOGLE_SHORT_URL = "market://details?id=";
    public static final String GOOGLE_APP_URL = "https://play.google.com/store/apps/details?id=";

    public static Uri getAppUri(){
        return Uri.parse(getAppShortUrl());
    }

    public static String getAppShortUrl(){
        return GOOGLE_SHORT_URL + BuildConfig.APPLICATION_ID;
    }

    public static String getAppLongUrl(){
        return GOOGLE_APP_URL + BuildConfig.APPLICATION_ID;
    }

    public static void openPlaystore(Context çontext){
        Intent intent = new Intent(Intent.ACTION_VIEW, getAppUri());
        if(isIntentAvailable(çontext, intent)) {
            çontext.startActivity(intent);
        }
    }

}
