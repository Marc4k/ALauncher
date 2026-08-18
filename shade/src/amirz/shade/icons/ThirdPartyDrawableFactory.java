package amirz.shade.icons;

import android.content.Context;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.util.ComponentKey;

import amirz.shade.hidden.HiddenAppsDatabase;
import amirz.shade.icons.calendar.DateChangeReceiver;
import amirz.shade.icons.calendar.DynamicCalendar;
import amirz.shade.icons.clock.DynamicClock;

import static com.android.launcher3.LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;

@SuppressWarnings("unused")
public class ThirdPartyDrawableFactory extends DrawableFactory {
    private final DynamicClock mDynamicClockDrawer;
    private final DateChangeReceiver mCalendars;

    public ThirdPartyDrawableFactory(Context context) {
        if (Utilities.ATLEAST_OREO) {
            mDynamicClockDrawer = new DynamicClock(context);
        } else {
            mDynamicClockDrawer = null;
        }
        mCalendars = new DateChangeReceiver(context);
    }

    @Override
    public FastBitmapDrawable newIcon(Context context, ItemInfoWithIcon info) {
        if (info != null && info.getTargetComponent() != null
                && info.itemType == ITEM_TYPE_APPLICATION) {
            ComponentKey key = new ComponentKey(info.getTargetComponent(), info.user);

            mCalendars.setIsDynamic(key,
                    info.getTargetComponent().getPackageName().equals(DynamicCalendar.CALENDAR));

            if (Utilities.ATLEAST_OREO
                    && !HiddenAppsDatabase.isHidden(context, key.componentName, key.user)
                    && info.getTargetComponent().equals(DynamicClock.DESK_CLOCK)) {
                    return mDynamicClockDrawer.drawIcon(info);
            }
        }

        return super.newIcon(context, info);
    }
}
