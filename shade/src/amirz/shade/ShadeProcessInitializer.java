package amirz.shade;

import android.content.Context;
import android.text.TextUtils;

import com.android.launcher3.MainProcessInitializer;
import com.android.launcher3.config.FeatureFlags;

import amirz.shade.customization.DockSearch;
import amirz.shade.customization.IconShapeOverride;


@SuppressWarnings("unused")
public class ShadeProcessInitializer extends MainProcessInitializer {
    public ShadeProcessInitializer(Context context) {
        FeatureFlags.QSB_ON_FIRST_SCREEN = false;
        FeatureFlags.HOTSEAT_WIDGET =
                !TextUtils.isEmpty(DockSearch.getDockSearch(context));
        IconShapeOverride.apply(context);
    }
}
