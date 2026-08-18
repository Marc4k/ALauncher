package amirz.shade;

import android.content.ComponentName;
import android.os.UserHandle;

import com.android.launcher3.AppFilter;
import com.android.launcher3.BuildConfig;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ShadeAppFilter extends AppFilter {
    private final Set<ComponentName> mFilter = new HashSet<>();

    public ShadeAppFilter(android.content.Context context) {
        mFilter.add(new ComponentName(BuildConfig.APPLICATION_ID, ShadeLauncher.class.getName()));
    }

    @Override
    public boolean shouldShowApp(ComponentName app, UserHandle user) {
        return !mFilter.contains(app);
    }
}
