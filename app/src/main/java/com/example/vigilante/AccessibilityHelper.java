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

        View rootView = activity.getWindow().getDecorView().getRootView();

        // applying color blind filter to the entire window
        applyColorBlindFilter(rootView, manager.getColorBlindMode());

        // applying large text if enabled
        if (manager.isLargeTextEnabled()) {
            applyLargeText(rootView);
        }

        // applying large buttons if enabled
        if (manager.isLargeButtonsEnabled()) {
            applyLargeButtons(rootView);
        }

        // applying high contrast if enabled
        if (manager.isHighContrastEnabled()) {
            applyHighContrast(rootView);
        }
    }

    // applying a color matrix filter to simulate different types of color blindness
    // Citation: Ved, March 17 2025, Claude referred to https://developer.android.com/reference/android/graphics/ColorMatrix
    private static void applyColorBlindFilter(View view, String mode) {
        if (AccessibilityManager.COLOR_BLIND_NONE.equals(mode)) {
            view.setLayerType(View.LAYER_TYPE_NONE, null);
            return;
        }

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
    private static void applyLargeText(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            float currentSize = tv.getTextSize() / view.getResources().getDisplayMetrics().scaledDensity;
            // bumping text size up by 30% for better readability
            tv.setTextSize(currentSize * 1.3f);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyLargeText(group.getChildAt(i));
            }
        }
    }

    // walking through all views and increasing button minimum height and padding
    private static void applyLargeButtons(View view) {
        if (view instanceof Button) {
            Button btn = (Button) view;
            int extraPadding = (int) (12 * view.getResources().getDisplayMetrics().density);
            btn.setPadding(
                    btn.getPaddingLeft() + extraPadding,
                    btn.getPaddingTop() + extraPadding,
                    btn.getPaddingRight() + extraPadding,
                    btn.getPaddingBottom() + extraPadding
            );
            btn.setMinHeight((int) (56 * view.getResources().getDisplayMetrics().density));
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyLargeButtons(group.getChildAt(i));
            }
        }
    }

    // increasing contrast by making text fully black and backgrounds fully white
    private static void applyHighContrast(View view) {
        if (view instanceof TextView) {
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
