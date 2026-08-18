/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.states;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.android.launcher3.Launcher;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.UiThreadHelper;

/**
 * Utility class to manage launcher rotation
 */
public class RotationHelper {

    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_ROTATE = 1;
    public static final int REQUEST_LOCK = 2;

    private final Launcher mLauncher;

    /**
     * Rotation request made by {@link InternalStateHandler}. This supersedes any other request.
     */
    private int mStateHandlerRequest = REQUEST_NONE;
    /**
     * Rotation request made by an app transition
     */
    private int mCurrentTransitionRequest = REQUEST_NONE;
    /**
     * Rotation request made by a Launcher State
     */
    private int mCurrentStateRequest = REQUEST_NONE;

    // This is used to defer setting rotation flags until the activity is being created
    private boolean mInitialized;
    private boolean mDestroyed;
    private boolean mRotationHasDifferentUI;

    private int mLastActivityFlags = -1;

    public RotationHelper(Launcher launcher) {
        mLauncher = launcher;
    }

    public void setRotationHadDifferentUI(boolean rotationHasDifferentUI) {
        mRotationHasDifferentUI = rotationHasDifferentUI;
    }

    public boolean homeScreenCanRotate() {
        return false;
    }

    public void updateRotationAnimation() {
        if (FeatureFlags.FAKE_LANDSCAPE_UI.get()) {
            WindowManager.LayoutParams lp = mLauncher.getWindow().getAttributes();
            int oldAnim = lp.rotationAnimation;
            lp.rotationAnimation = homeScreenCanRotate()
                    ? WindowManager.LayoutParams.ROTATION_ANIMATION_ROTATE
                    : WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS;
            if (oldAnim != lp.rotationAnimation) {
                mLauncher.getWindow().setAttributes(lp);
            }
        }
    }

    public void setStateHandlerRequest(int request) {
        if (mStateHandlerRequest != request) {
            mStateHandlerRequest = request;
            updateRotationAnimation();
            notifyChange();
        }
    }

    public void setCurrentTransitionRequest(int request) {
        if (mCurrentTransitionRequest != request) {
            mCurrentTransitionRequest = request;
            notifyChange();
        }
    }

    public void setCurrentStateRequest(int request) {
        if (mCurrentStateRequest != request) {
            mCurrentStateRequest = request;
            notifyChange();
        }
    }

    // Used by tests only.
    public void forceAllowRotationForTesting(boolean allowRotation) {
        notifyChange();
    }

    public void initialize() {
        if (!mInitialized) {
            mInitialized = true;
            notifyChange();
            updateRotationAnimation();
        }
    }

    public void destroy() {
        if (!mDestroyed) {
            mDestroyed = true;
        }
    }

    private void notifyChange() {
        if (!mInitialized || mDestroyed) {
            return;
        }

        final int activityFlags = SCREEN_ORIENTATION_PORTRAIT;
        if (activityFlags != mLastActivityFlags) {
            mLastActivityFlags = activityFlags;
            UiThreadHelper.setOrientationAsync(mLauncher, activityFlags);
        }
    }

    @Override
    public String toString() {
        return String.format("[mStateHandlerRequest=%d, mCurrentStateRequest=%d,"
                + " mLastActivityFlags=%d]",
                mStateHandlerRequest, mCurrentStateRequest, mLastActivityFlags);
    }
}
