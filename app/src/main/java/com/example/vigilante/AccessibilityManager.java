// managing accessibility preferences using SharedPreferences so settings persist across sessions (Wildcard)

package com.example.vigilante;

import android.content.Context;
import android.content.SharedPreferences;

public class AccessibilityManager {

    private static final String PREFS_NAME = "accessibility_prefs";
    private static final String KEY_COLOR_BLIND_MODE = "color_blind_mode";
    private static final String KEY_LARGE_TEXT = "large_text";
    private static final String KEY_LARGE_BUTTONS = "large_buttons";
    private static final String KEY_REDUCE_MOTION = "reduce_motion";
    private static final String KEY_HIGH_CONTRAST = "high_contrast";

    // color blind mode options stored as strings
    public static final String COLOR_BLIND_NONE = "none";
    public static final String COLOR_BLIND_DEUTERANOPIA = "deuteranopia";
    public static final String COLOR_BLIND_PROTANOPIA = "protanopia";
    public static final String COLOR_BLIND_TRITANOPIA = "tritanopia";

    private final SharedPreferences prefs;

    // Citation: Ved, March 17 2025, Claude referred to https://developer.android.com/training/data-storage/shared-preferences
    public AccessibilityManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // getting the current color blind mode setting
    public String getColorBlindMode() {
        return prefs.getString(KEY_COLOR_BLIND_MODE, COLOR_BLIND_NONE);
    }

    // saving the selected color blind mode
    public void setColorBlindMode(String mode) {
        prefs.edit().putString(KEY_COLOR_BLIND_MODE, mode).apply();
    }

    // checking if large text mode is turned on
    public boolean isLargeTextEnabled() {
        return prefs.getBoolean(KEY_LARGE_TEXT, false);
    }

    // toggling large text mode on or off
    public void setLargeTextEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_LARGE_TEXT, enabled).apply();
    }

    // checking if large buttons mode is turned on
    public boolean isLargeButtonsEnabled() {
        return prefs.getBoolean(KEY_LARGE_BUTTONS, false);
    }

    // toggling large buttons mode on or off
    public void setLargeButtonsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_LARGE_BUTTONS, enabled).apply();
    }

    // checking if reduce motion mode is turned on
    public boolean isReduceMotionEnabled() {
        return prefs.getBoolean(KEY_REDUCE_MOTION, false);
    }

    // toggling reduce motion mode on or off
    public void setReduceMotionEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REDUCE_MOTION, enabled).apply();
    }

    // checking if high contrast mode is turned on
    public boolean isHighContrastEnabled() {
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false);
    }

    // toggling high contrast mode on or off
    public void setHighContrastEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply();
    }

    // resetting all accessibility settings back to defaults
    public void resetAll() {
        prefs.edit().clear().apply();
    }
}
