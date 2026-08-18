package amirz.shade;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import amirz.shade.settings.ColorListPreference;
import amirz.shade.settings.DockSearchPrefSetter;
import amirz.shade.settings.ReloadingListPreference;

import static amirz.shade.ShadeFont.KEY_FONT;
import static amirz.shade.customization.DockSearch.KEY_DOCK_SEARCH;
import static amirz.shade.customization.ShadeStyle.KEY_THEME;
import static com.android.launcher3.util.Themes.KEY_DEVICE_THEME;

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
    public static class ShadeSettingsFragment extends SettingsActivity.LauncherSettingsFragment
            implements Preference.OnPreferenceChangeListener {
        private static final String CATEGORY_STYLE = "pref_screen_style";
        private static final String CATEGORY_SEARCH = "pref_screen_search";
        private Activity context;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            super.onCreatePreferences(savedInstanceState, rootKey);

            context = getActivity();

            if(null == rootKey) {
                return;
            }
            if(rootKey.equals(CATEGORY_STYLE)) {
                // Style
                Preference theme = findPreference(KEY_THEME);
                if (null !=  theme){
                    theme.setOnPreferenceChangeListener(this);
                }
                Preference tone = findPreference(KEY_DEVICE_THEME);
                if (null !=  tone){
                    tone.setOnPreferenceChangeListener(this);
                }
                Preference font = findPreference(KEY_FONT);
                if (null !=  font){
                    font.setOnPreferenceChangeListener(this);
                }
            } else if(rootKey.equals(CATEGORY_SEARCH)) {
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

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case KEY_THEME:
                case KEY_DEVICE_THEME:
                case KEY_FONT:
                    startActivity(getActivity().getIntent()
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        ActivityOptions.makeCustomAnimation(
                                context, R.anim.fade_in, R.anim.fade_out).toBundle());
                    getActivity().recreate();
                    break;
            }
            return true;
        }

        private static final String DIALOG_FRAGMENT_TAG =
                "androidx.preference.PreferenceFragment.DIALOG";
        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            if (preference instanceof ColorListPreference) {
                final DialogFragment f = ColorListPreference.ColorPreferenceFragment.newInstance(preference.getKey());
                f.setTargetFragment(this, 0);
                f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }
}
