package amirz.shade.customization;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;

public class ShadeStyle {
    private static final String MIDNIGHT_ACCENT = "#0F0F0F";

    public static void override(Activity activity) {
        activity.getTheme().applyStyle(R.style.ShadeOverride_Midnight, true);
    }

    public static void overrideShape(Activity activity) {
        if (Utilities.ATLEAST_Q) {
            activity.getTheme().applyStyle(R.style.Curvature_Circle, true);
        }
    }

    public static void overrideTheme(Activity activity) {
        int themeRes = Themes.getSettingActivityThemeRes(activity);
        activity.setTheme(themeRes);
    }

    public static int getPrimaryColor(Context context){
        return Color.parseColor(MIDNIGHT_ACCENT);
    }
}
