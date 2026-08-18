package amirz.shade;

import android.app.SearchManager;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.AppInfo;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.compat.UserManagerCompat;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import amirz.helpers.Settings;
import amirz.shade.animations.TransitionManager;
import amirz.shade.customization.DockSearch;
import amirz.shade.hidden.HiddenAppsDrawerState;
import amirz.shade.icons.pack.IconPackManager;
import amirz.shade.search.AllAppsQsb;
import amirz.shade.sleep.WorkspaceSleepListener;
import amirz.unread.UnreadSession;

import static amirz.shade.ShadeFont.DEFAULT_FONT;
import static amirz.shade.ShadeFont.KEY_FONT;
import static amirz.shade.animations.TransitionManager.KEY_FADING_TRANSITION;
import static amirz.shade.customization.DockSearch.KEY_DOCK_SEARCH;
import static amirz.shade.customization.ShadeStyle.KEY_THEME;
import static android.content.Context.SEARCH_SERVICE;
import static com.android.launcher3.LauncherState.ALL_APPS;
import static com.android.launcher3.LauncherState.NORMAL;
import static com.android.launcher3.allapps.PersonalWorkSlidingTabStrip.KEY_SHOWED_PEEK_WORK_TAB;
import static com.android.launcher3.settings.SettingsActivity.GRID_OPTIONS_PREFERENCE_KEY;
import static com.android.launcher3.util.Themes.KEY_DEVICE_THEME;

public class ShadeLauncherCallbacks implements LauncherCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_IDP_GRID_NAME = "idp_grid_name";

    private final ShadeLauncher mLauncher;
    private final Handler mHandler = new Handler();
    private boolean mNoFloatingView;

    ShadeLauncherCallbacks(ShadeLauncher launcher) {
        mLauncher = launcher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = Utilities.getPrefs(mLauncher);
        setDefaultValues(prefs);
        prefs.registerOnSharedPreferenceChangeListener(this);
        UnreadSession.getInstance(mLauncher).onCreate();
        WorkspaceSleepListener.override(mLauncher);
    }

    private String getRecommendedSearchProvider() {
        String recommended = DockSearch.getRecommendedProvider(mLauncher);
        String override = Utilities.getPrefs(mLauncher).getString(KEY_DOCK_SEARCH, recommended);
        return TextUtils.isEmpty(override)
                ? recommended
                : override;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (KEY_FADING_TRANSITION.equals(key)) {
            TransitionManager transitions = (TransitionManager) mLauncher.getAppTransitionManager();
            transitions.applyWindowPreference(mLauncher);
        } else if (KEY_DEVICE_THEME.equals(key) || KEY_THEME.equals(key) || KEY_FONT.equals(key)) {
            mLauncher.recreate();
        } else if (KEY_IDP_GRID_NAME.equals(key) || KEY_DOCK_SEARCH.equals(key)) {
            mLauncher.kill();
        }
    }

    private void setDefaultValues(SharedPreferences prefs) {
        prefs.edit().putString(KEY_FONT, prefs.getString(KEY_FONT, DEFAULT_FONT))
                .putString(KEY_DOCK_SEARCH, prefs.getString(KEY_DOCK_SEARCH,
                        getRecommendedSearchProvider()))
                .putString(KEY_IDP_GRID_NAME, prefs.getString(KEY_IDP_GRID_NAME, null))
                .putBoolean(GRID_OPTIONS_PREFERENCE_KEY, true)
                .apply();

        // Removes the permanent bounce when there is a work profile but no work apps.
        if (UserManagerCompat.getInstance(mLauncher).hasWorkProfile() && !hasWorkApp()) {
            prefs.edit().putBoolean(KEY_SHOWED_PEEK_WORK_TAB, true).apply();
        }
    }

    private boolean hasWorkApp() {
        AllAppsStore store = mLauncher.getAppsView().getAppsStore();
        UserHandle myUser = Process.myUserHandle();
        for (AppInfo info : store.getApps()) {
            if (!info.user.equals(myUser)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        UnreadSession.getInstance(mLauncher).onResume(mLauncher);
        if (!mLauncher.getAppsView().getApps().hasFilter()) {
            HiddenAppsDrawerState.getInstance(mLauncher).setRevealed(false);
        }
    }

    @Override
    public void onStart() {
        TransitionManager transitions = (TransitionManager) mLauncher.getAppTransitionManager();
        transitions.applyWindowPreference(mLauncher);
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onPause() {
        UnreadSession.getInstance(mLauncher).onPause(mLauncher);
        mNoFloatingView = AbstractFloatingView.getTopOpenView(mLauncher) == null;
    }

    @Override
    public void onDestroy() {
        Utilities.getPrefs(mLauncher).unregisterOnSharedPreferenceChangeListener(this);
        UnreadSession.getInstance(mLauncher).onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onAttachedToWindow() {
    }

    @Override
    public void onDetachedFromWindow() {
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter w, String[] args) {
    }

    @Override
    public void onHomeIntent(boolean internalStateHandled) {
        if(mLauncher.isInState(NORMAL) && !Settings.getHomeAction(mLauncher).equals("nothing")) {
            Settings.handleHomeAction(mLauncher);
        } else if (mLauncher.hasWindowFocus()
                && mLauncher.isInState(NORMAL)
                && mLauncher.getWorkspace().getNextPage() == 0
                && mNoFloatingView) {
            AllAppsQsb search =
                    (AllAppsQsb) mLauncher.getAppsView().getSearchView();
            search.requestSearch();
            mLauncher.getStateManager().goToState(ALL_APPS, true);
        }
    }

    @Override
    public boolean handleBackPressed() {
        if (!mLauncher.getDragController().isDragging()) {
            AbstractFloatingView topView = AbstractFloatingView.getTopOpenView(mLauncher);
            if (topView != null && topView.onBackPressed()) {
                // Override base because we do not want to call onBackPressed twice.
                return true;
            } else if (mLauncher.isInState(ALL_APPS)) {
                AllAppsQsb search = (AllAppsQsb) mLauncher.getAppsView().getSearchUiManager();
                return search.tryClearSearch();
            }
        }
        return false;
    }

    @Override
    public void onTrimMemory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            IconPackManager.get(mLauncher).trimMemory();
        }
    }

    @Override
    public void onStateChanged() {

    }

    @Override
    public void onLauncherProviderChange() {

    }

    @Override
    public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData) {
        SearchManager sm = (SearchManager) mLauncher.getSystemService(SEARCH_SERVICE);
        if (sm == null || sm.getGlobalSearchActivity() == null) {
            AllAppsQsb search = (AllAppsQsb) mLauncher.getAppsView().getSearchView();
            search.requestSearch();
            mHandler.post(() -> mLauncher.getStateManager().goToState(ALL_APPS, true));
            return true;
        }
        return false;
    }

}
