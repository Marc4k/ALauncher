package amirz.shade.icons;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.android.launcher3.util.ComponentKey;

import amirz.shade.icons.calendar.DynamicCalendar;
import amirz.shade.icons.clock.DynamicClock;

class ThirdPartyIconUtils {
    static Drawable getByKey(Context context, ComponentKey key, int iconDpi) {
        if (key.componentName.equals(DynamicClock.DESK_CLOCK)) {
            return DynamicClock.getClock(context, iconDpi);
        }

        if (key.componentName.getPackageName().equals(DynamicCalendar.CALENDAR)) {
            return DynamicCalendar.load(context, key.componentName, iconDpi);
        }

        return null;
    }
}
