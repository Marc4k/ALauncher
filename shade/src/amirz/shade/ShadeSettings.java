package amirz.shade;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.settings.SettingsActivity;
import com.android.launcher3.util.SystemUiController;

import amirz.shade.customization.ShadeStyle;
import amirz.shade.settings.DockSearchPrefSetter;
import amirz.shade.settings.ReloadingListPreference;

import static amirz.shade.customization.DockSearch.KEY_DOCK_SEARCH;

public class ShadeSettings extends SettingsActivity {

    public interface OnResumePreferenceCallback {
        void onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShadeFont.override(this);
        ShadeStyle.override(this);
        ShadeStyle.overrideTheme(this);
        super.onCreate(savedInstanceState);

        if (Utilities.ATLEAST_OREO && !Utilities.ATLEAST_P) {
            new SystemUiController(getWindow())
                    .updateUiState(SystemUiController.UI_STATE_BASE_WINDOW, true);
        }
    }

    @SuppressWarnings("unused")
    public static class ShadeSettingsFragment extends SettingsActivity.LauncherSettingsFragment {
        private static final String CATEGORY_SEARCH = "pref_screen_search";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            super.onCreatePreferences(savedInstanceState, rootKey);

            if(null == rootKey) {
                return;
            }
            if(rootKey.equals(CATEGORY_SEARCH)) {
                ReloadingListPreference search =
                        (ReloadingListPreference) findPreference(KEY_DOCK_SEARCH);
                if (null != search) {
                    search.setOnReloadListener(DockSearchPrefSetter::new);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            PreferenceScreen screen = getPreferenceScreen();
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference preference = screen.getPreference(i);
                if (null !=  preference && preference instanceof PreferenceCategory) {
                    PreferenceCategory cat = (PreferenceCategory) preference;
                    for (int j = 0; j < cat.getPreferenceCount(); j++) {
                        Preference preference2 = cat.getPreference(j);
                        if (null !=  preference2 && preference2 instanceof OnResumePreferenceCallback) {
                            ((OnResumePreferenceCallback) preference2).onResume();
                        }
                    }
                }
            }
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            View listView = getListView();
            applyInsets(listView);
        }

    }
}
