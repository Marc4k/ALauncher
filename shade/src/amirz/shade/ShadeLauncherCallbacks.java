package amirz.shade;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import amirz.shade.animations.TransitionManager;
import amirz.shade.customization.DockSearch;
import amirz.shade.hidden.HiddenAppsDrawerState;
import amirz.shade.search.AllAppsQsb;
import amirz.unread.UnreadSession;

import static amirz.shade.ShadeFont.DEFAULT_FONT;
import static amirz.shade.ShadeFont.KEY_FONT;
import static amirz.shade.customization.DockSearch.KEY_DOCK_SEARCH;
import static amirz.shade.customization.ShadeStyle.KEY_THEME;
import static android.content.Context.SEARCH_SERVICE;
import static com.android.launcher3.LauncherState.ALL_APPS;
import static com.android.launcher3.util.Themes.KEY_DEVICE_THEME;

public class ShadeLauncherCallbacks implements LauncherCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private final ShadeLauncher mLauncher;
    private final Handler mHandler = new Handler();

    ShadeLauncherCallbacks(ShadeLauncher launcher) {
        mLauncher = launcher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = Utilities.getPrefs(mLauncher);
        prefs.edit()
                .remove("pref_icon_pack")
                .remove("pref_grid_options")
                .remove("idp_grid_name")
                .apply();
        if (Utilities.ATLEAST_NOUGAT) {
            mLauncher.deleteSharedPreferences(
                    mLauncher.getPackageName() + ".ICON_DATABASE");
        } else {
            mLauncher.getSharedPreferences(
                    mLauncher.getPackageName() + ".ICON_DATABASE", 0).edit().clear().apply();
        }
        setDefaultValues(prefs);
        prefs.registerOnSharedPreferenceChangeListener(this);
        UnreadSession.getInstance(mLauncher).onCreate();
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
        if (KEY_DEVICE_THEME.equals(key) || KEY_THEME.equals(key) || KEY_FONT.equals(key)) {
            mLauncher.recreate();
        } else if (KEY_DOCK_SEARCH.equals(key)) {
            mLauncher.kill();
        }
    }

    private void setDefaultValues(SharedPreferences prefs) {
        prefs.edit().putString(KEY_FONT, prefs.getString(KEY_FONT, DEFAULT_FONT))
                .putString(KEY_DOCK_SEARCH, prefs.getString(KEY_DOCK_SEARCH,
                        getRecommendedSearchProvider()))
                .apply();

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
        transitions.applyWindowAnimations(mLauncher);
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onPause() {
        UnreadSession.getInstance(mLauncher).onPause(mLauncher);
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
