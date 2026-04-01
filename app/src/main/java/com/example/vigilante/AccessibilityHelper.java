// applying accessibility settings to any activity's view hierarchy (Wildcard)

package com.example.vigilante;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AccessibilityHelper {

    // applying all active accessibility settings to the given activity's root view
    public static void apply(Activity activity) {
        AccessibilityManager manager = new AccessibilityManager(activity);

        String colorMode = manager.getColorBlindMode();
        boolean anyEnabled = !AccessibilityManager.COLOR_BLIND_NONE.equals(colorMode)
                || manager.isLargeTextEnabled()
                || manager.isLargeButtonsEnabled()
                || manager.isHighContrastEnabled();

        if (!anyEnabled) return;

        View rootView = activity.getWindow().getDecorView().getRootView();

        if (!AccessibilityManager.COLOR_BLIND_NONE.equals(colorMode)) {
            applyColorBlindFilter(rootView, colorMode);
        }

        if (manager.isLargeTextEnabled()) {
            applyLargeText(rootView);
        }

        if (manager.isLargeButtonsEnabled()) {
            applyLargeButtons(rootView);
        }

        if (manager.isHighContrastEnabled()) {
            applyHighContrast(rootView);
        }
    }

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

    // walking through all views and increasing text size by a scale factor for readability
    // Citation: Ved, March 17 2025, Claude referred to https://stackoverflow.com/questions/12128331/how-to-change-fontsize-of-all-textviews-in-activity
    private static final int TAG_ORIGINAL_TEXT_SIZE = R.id.backArrow;

    private static void applyLargeText(View view) {
        if (view instanceof TextView && !(view instanceof Button)) {
            TextView tv = (TextView) view;
            if (tv.getTag(TAG_ORIGINAL_TEXT_SIZE) == null) {
                float originalSize = tv.getTextSize() / view.getResources().getDisplayMetrics().scaledDensity;
                tv.setTag(TAG_ORIGINAL_TEXT_SIZE, originalSize);
                tv.setTextSize(originalSize * 1.3f);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyLargeText(group.getChildAt(i));
            }
        }
    }

    // walking through all views and increasing button minimum height and padding
    private static final int TAG_LARGE_BUTTONS_APPLIED = R.id.backButton;

    private static void applyLargeButtons(View view) {
        if (view instanceof Button) {
            Button btn = (Button) view;
            if (btn.getTag(TAG_LARGE_BUTTONS_APPLIED) == null) {
                btn.setTag(TAG_LARGE_BUTTONS_APPLIED, true);
                int extraPadding = (int) (12 * view.getResources().getDisplayMetrics().density);
                btn.setPadding(
                        btn.getPaddingLeft() + extraPadding,
                        btn.getPaddingTop() + extraPadding,
                        btn.getPaddingRight() + extraPadding,
                        btn.getPaddingBottom() + extraPadding
                );
                btn.setMinHeight((int) (56 * view.getResources().getDisplayMetrics().density));
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyLargeButtons(group.getChildAt(i));
            }
        }
    }

    // increasing contrast by making non-button text fully black for readability
    private static void applyHighContrast(View view) {
        if (view instanceof TextView && !(view instanceof Button)) {
            TextView tv = (TextView) view;
            tv.setTextColor(0xFF000000);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyHighContrast(group.getChildAt(i));
            }
        }
    }

    // checking if reduce motion is enabled so animations can be skipped throughout the app
    public static boolean shouldReduceMotion(Activity activity) {
        return new AccessibilityManager(activity).isReduceMotionEnabled();
    }
}
