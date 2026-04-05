// accessibility settings screen letting users configure color blind mode, large text, large buttons, high contrast and reduce motion (Wildcard)
// March 31 2026, Claude Opus 4.6, built full accessibility settings page with toggle controls for five accessibility features

package com.example.vigilante;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AccessibilityActivity extends AppCompatActivity {

    private AccessibilityManager accessibilityManager;
    private RadioGroup colorBlindGroup;
    private SwitchMaterial reduceMotionToggle;

    /**
     * setting up the accessibility settings screen with toggle controls
     * for color blind mode and reduce motion preferences
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        accessibilityManager = new AccessibilityManager(this);

        colorBlindGroup = findViewById(R.id.colorBlindGroup);
        reduceMotionToggle = findViewById(R.id.reduceMotionToggle);

        // loading saved preferences into the UI controls
        loadCurrentSettings();

        // listening for color blind mode radio button changes
        colorBlindGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String mode;
            if (checkedId == R.id.radioDeuteranopia) {
                mode = AccessibilityManager.COLOR_BLIND_DEUTERANOPIA;
            } else if (checkedId == R.id.radioProtanopia) {
                mode = AccessibilityManager.COLOR_BLIND_PROTANOPIA;
            } else if (checkedId == R.id.radioTritanopia) {
                mode = AccessibilityManager.COLOR_BLIND_TRITANOPIA;
            } else {
                mode = AccessibilityManager.COLOR_BLIND_NONE;
            }
            accessibilityManager.setColorBlindMode(mode);
            Toast.makeText(this, "Color filter updated", Toast.LENGTH_SHORT).show();
        });

        // saving reduce motion preference when toggled
        reduceMotionToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            accessibilityManager.setReduceMotionEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Motion reduced" : "Motion restored", Toast.LENGTH_SHORT).show();
        });

        // resetting all settings back to defaults
        findViewById(R.id.resetButton).setOnClickListener(v -> {
            accessibilityManager.resetAll();
            loadCurrentSettings();
            Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
        });

        // back arrow navigates back to the profile page
        findViewById(R.id.backArrow).setOnClickListener(v -> finish());
    }

    /**
     * reading the saved accessibility preferences from SharedPreferences
     * and updating the radio buttons and toggle switches to match
     */
    // reading the current accessibility preferences and setting the UI controls to match
    // Citation: Ved, March 17 2025, Claude referred to https://developer.android.com/reference/android/widget/RadioGroup#check(int)
    private void loadCurrentSettings() {
        String colorMode = accessibilityManager.getColorBlindMode();
        switch (colorMode) {
            case AccessibilityManager.COLOR_BLIND_DEUTERANOPIA:
                colorBlindGroup.check(R.id.radioDeuteranopia);
                break;
            case AccessibilityManager.COLOR_BLIND_PROTANOPIA:
                colorBlindGroup.check(R.id.radioProtanopia);
                break;
            case AccessibilityManager.COLOR_BLIND_TRITANOPIA:
                colorBlindGroup.check(R.id.radioTritanopia);
                break;
            default:
                colorBlindGroup.check(R.id.radioNone);
                break;
        }

        reduceMotionToggle.setChecked(accessibilityManager.isReduceMotionEnabled());
    }
}
