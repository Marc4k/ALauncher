package amirz.shade.customization;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.popup.SystemShortcutFactory;
import com.android.launcher3.util.PackageManagerHelper;

import java.net.URISyntaxException;

import static android.content.Context.USER_SERVICE;

@SuppressWarnings("unused")
public class ShadeShortcutFactory extends SystemShortcutFactory {
    private static final String TAG = "ShadeShortcutFactory";

    public ShadeShortcutFactory(Context context) {
        super(new SystemShortcut.AppInfo(),
                new SystemShortcut.Widgets(),
                new UnInstall());
    }


    public static class UnInstall extends SystemShortcut<Launcher> {
        public UnInstall() {
            super(R.drawable.ic_uninstall_no_shadow, R.string.uninstall_drop_target_label);
        }

        @Override
        public View.OnClickListener getOnClickListener(
                Launcher launcher, ItemInfo itemInfo) {
            UserManager userManager =
                    (UserManager) launcher.getSystemService(USER_SERVICE);
            Bundle restrictions = userManager.getUserRestrictions(itemInfo.user);
            boolean uninstallDisabled = restrictions.getBoolean(UserManager.DISALLOW_APPS_CONTROL, false)
                    || restrictions.getBoolean(UserManager.DISALLOW_UNINSTALL_APPS, false);
            boolean isSystemApp = PackageManagerHelper.isSystemApp(launcher, itemInfo.getIntent());
            if (isSystemApp || uninstallDisabled) {
                return null;
            }
            return createOnClickListener(launcher, itemInfo);
        }

        public View.OnClickListener createOnClickListener(
                Launcher launcher, ItemInfo itemInfo) {
            return view -> {
                try {
                    ComponentName cn = itemInfo.getTargetComponent();
                    Intent intent = Intent.parseUri(launcher.getString(R.string.delete_package_intent), 0)
                            .setData(Uri.fromParts("package", cn.getPackageName(), cn.getClassName()))
                            .putExtra(Intent.EXTRA_USER, itemInfo.user);
                    launcher.startActivity(intent);
                    AbstractFloatingView.closeAllOpenViews(launcher);
                } catch (URISyntaxException e) {
                    Log.e(TAG, "Failed to parse intent to start uninstall activity for item=" + itemInfo);
                }
            };
        }
    }

}
