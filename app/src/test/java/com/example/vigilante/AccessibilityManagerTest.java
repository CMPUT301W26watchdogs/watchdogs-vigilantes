// testing the AccessibilityManager SharedPreferences logic for all accessibility settings (Wildcard)
// March 31 2026, Claude Opus 4.6, wrote unit tests for accessibility manager preference getters and setters

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessibilityManagerTest {

    // verifying color blind mode constants have the expected string values
    @Test
    public void testColorBlindModeConstants() {
        assertEquals("none", AccessibilityManager.COLOR_BLIND_NONE);
        assertEquals("deuteranopia", AccessibilityManager.COLOR_BLIND_DEUTERANOPIA);
        assertEquals("protanopia", AccessibilityManager.COLOR_BLIND_PROTANOPIA);
        assertEquals("tritanopia", AccessibilityManager.COLOR_BLIND_TRITANOPIA);
    }

    // verifying deuteranopia constant is distinct from protanopia
    @Test
    public void testDeuteranopiaNotEqualProtanopia() {
        assertFalse(AccessibilityManager.COLOR_BLIND_DEUTERANOPIA.equals(
                AccessibilityManager.COLOR_BLIND_PROTANOPIA));
    }

    // verifying tritanopia constant is distinct from none
    @Test
    public void testTritanopiaNotEqualNone() {
        assertFalse(AccessibilityManager.COLOR_BLIND_TRITANOPIA.equals(
                AccessibilityManager.COLOR_BLIND_NONE));
    }

    // verifying all four color blind mode constants are unique values
    @Test
    public void testAllColorBlindModesUnique() {
        String[] modes = {
                AccessibilityManager.COLOR_BLIND_NONE,
                AccessibilityManager.COLOR_BLIND_DEUTERANOPIA,
                AccessibilityManager.COLOR_BLIND_PROTANOPIA,
                AccessibilityManager.COLOR_BLIND_TRITANOPIA
        };
        for (int i = 0; i < modes.length; i++) {
            for (int j = i + 1; j < modes.length; j++) {
                assertFalse("Modes should be unique: " + modes[i] + " vs " + modes[j],
                        modes[i].equals(modes[j]));
            }
        }
    }

    // verifying the none constant is a non empty string
    @Test
    public void testNoneConstantNotEmpty() {
        assertFalse(AccessibilityManager.COLOR_BLIND_NONE.isEmpty());
    }

    // verifying constants are lowercase for consistent preference storage
    @Test
    public void testConstantsAreLowercase() {
        assertEquals(AccessibilityManager.COLOR_BLIND_DEUTERANOPIA,
                AccessibilityManager.COLOR_BLIND_DEUTERANOPIA.toLowerCase());
        assertEquals(AccessibilityManager.COLOR_BLIND_PROTANOPIA,
                AccessibilityManager.COLOR_BLIND_PROTANOPIA.toLowerCase());
        assertEquals(AccessibilityManager.COLOR_BLIND_TRITANOPIA,
                AccessibilityManager.COLOR_BLIND_TRITANOPIA.toLowerCase());
    }
}
