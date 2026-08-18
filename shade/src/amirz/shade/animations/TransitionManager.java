package amirz.shade.animations;

import android.app.ActivityOptions;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppTransitionManager;
import com.android.launcher3.R;

@SuppressWarnings("unused")
public class TransitionManager extends LauncherAppTransitionManager {
    public TransitionManager(Context context) {
    }

    public ActivityOptions getActivityLaunchOptions(Launcher launcher, View v) {
        return ActivityOptions.makeCustomAnimation(
                launcher, R.anim.enter_app, R.anim.exit_launcher);
    }

    public void applyWindowAnimations(Launcher launcher) {
        Window window = launcher.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.windowAnimations = R.style.ShadeAnimations;
        window.setAttributes(attributes);
    }
}
