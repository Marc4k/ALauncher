package amirz.shade.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import com.android.launcher3.BuildConfig;

import static amirz.shade.services.Services.PERM;

@TargetApi(28)
public class GlobalActionService extends AccessibilityService {
    public static final String RECENTS = BuildConfig.APPLICATION_ID + ".RECENTS";

    private static boolean sRunning;

    public static boolean isRunning() {
        return sRunning;
    }

    private final IntentFilter mActionFilter = new IntentFilter();
    private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            performGlobalAction(GLOBAL_ACTION_RECENTS);
        }
    };

    public GlobalActionService() {
        super();
        mActionFilter.addAction(RECENTS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(mActionReceiver, mActionFilter, PERM, new Handler());
        sRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mActionReceiver);
        sRunning = false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
