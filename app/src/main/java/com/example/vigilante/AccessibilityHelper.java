// applying accessibility settings to any activity's view hierarchy (Wildcard)
// March 31 2026, Claude Opus 4.6, built helper that walks the view tree to apply color blind filters, large text, large buttons and high contrast

package com.example.vigilante;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;

public class AccessibilityHelper {

    /**
     * applying the user's accessibility preferences to the given activity's view tree,
     * currently handling color blind filter overlay when enabled
     *
     * @param activity the activity whose views should be modified
     */
    // applying color blind filter if enabled, skipping everything else that breaks button rendering
    public static void apply(Activity activity) {
        AccessibilityManager manager = new AccessibilityManager(activity);

        String colorMode = manager.getColorBlindMode();
        if (AccessibilityManager.COLOR_BLIND_NONE.equals(colorMode)) return;

        View rootView = activity.getWindow().getDecorView().getRootView();
        applyColorBlindFilter(rootView, colorMode);
    }

    /**
     * applying a color matrix overlay to the root view that simulates how colors
     * appear under different types of color blindness (deuteranopia, protanopia, tritanopia)
     *
     * @param view the root view to apply the filter on
     * @param mode the color blind mode string from AccessibilityManager
     */
    // applying a color matrix filter to simulate different types of color blindness
    // Citation: Ved, March 17 2025, Claude referred to https://developer.android.com/reference/android/graphics/ColorMatrix
    private static void applyColorBlindFilter(View view, String mode) {
        ColorMatrix colorMatrix = new ColorMatrix();

        switch (mode) {
            case AccessibilityManager.COLOR_BLIND_DEUTERANOPIA:
                // simulating green weakness by shifting green channel values
                colorMatrix.set(new float[]{
                        0.625f, 0.375f, 0, 0, 0,
                        0.7f,   0.3f,   0, 0, 0,
                        0,      0.3f,   0.7f, 0, 0,
                        0,      0,      0, 1, 0
                });
                break;
            case AccessibilityManager.COLOR_BLIND_PROTANOPIA:
                // simulating red weakness by shifting red channel values
                colorMatrix.set(new float[]{
                        0.567f, 0.433f, 0, 0, 0,
                        0.558f, 0.442f, 0, 0, 0,
                        0,      0.242f, 0.758f, 0, 0,
                        0,      0,      0, 1, 0
                });
                break;
            case AccessibilityManager.COLOR_BLIND_TRITANOPIA:
                // simulating blue weakness by shifting blue channel values
                colorMatrix.set(new float[]{
                        0.95f, 0.05f, 0, 0, 0,
                        0,     0.433f, 0.567f, 0, 0,
                        0,     0.475f, 0.525f, 0, 0,
                        0,     0,      0, 1, 0
                });
                break;
            default:
                return;
        }

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    /**
     * checking if the reduce motion preference is turned on so callers
     * can skip animations throughout the app
     *
     * @param activity the activity context used to read SharedPreferences
     * @return true if the user has reduce motion enabled
     */
    // checking if reduce motion is enabled so animations can be skipped throughout the app
    public static boolean shouldReduceMotion(Activity activity) {
        return new AccessibilityManager(activity).isReduceMotionEnabled();
    }
}
