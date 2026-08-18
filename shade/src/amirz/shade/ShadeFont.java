package amirz.shade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ShadeFont {
    private static final String GOOGLE_SANS = "google_sans";

    private static Map<String, Typeface> sDeviceMap;

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("InflateParams")
    public static void override(Context context) {
        try {
            final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
            staticField.setAccessible(true);
            if (sDeviceMap == null) {
                //noinspection unchecked
                sDeviceMap = (Map<String, Typeface>) staticField.get(null);
            }

            Map<String, Typeface> newMap = new HashMap<>(sDeviceMap);

            AssetManager assets = context.getAssets();
            Typeface regular = Typeface.createFromAsset(assets, GOOGLE_SANS + "_regular.ttf");
            Typeface medium = Typeface.createFromAsset(assets, GOOGLE_SANS + "_medium.ttf");
            Typeface bold = Typeface.createFromAsset(assets, GOOGLE_SANS + "_bold.ttf");

            newMap.put("sans-serif", regular);
            newMap.put("sans-serif-medium", medium);
            newMap.put("sans-serif-bold", bold);
            staticField.set(null, newMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), GOOGLE_SANS + "_regular.ttf");
    }
}
