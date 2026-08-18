package amirz.shade;

import android.content.Context;
import android.text.TextUtils;

import androidx.core.graphics.PathParser;

import com.android.launcher3.MainProcessInitializer;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.AdaptiveIconCompat;

import amirz.shade.customization.DockSearch;


@SuppressWarnings("unused")
public class ShadeProcessInitializer extends MainProcessInitializer {
    private static final String CIRCLE_PATH =
            "M50 0A50 50,0,1,1,50 100A50 50,0,1,1,50 0";

    public ShadeProcessInitializer(Context context) {
        FeatureFlags.QSB_ON_FIRST_SCREEN = false;
        FeatureFlags.HOTSEAT_WIDGET =
                !TextUtils.isEmpty(DockSearch.getDockSearch(context));
        Utilities.getPrefs(context).edit().remove("pref_override_icon_shape").apply();
        if (Utilities.ATLEAST_OREO) {
            AdaptiveIconCompat.setMask(PathParser.createPathFromPathData(CIRCLE_PATH));
        }
    }
}
