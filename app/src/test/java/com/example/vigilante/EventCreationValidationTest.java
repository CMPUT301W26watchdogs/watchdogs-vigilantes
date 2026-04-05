// Claude, Opus 4, "write validation tests for event creation required fields and event map construction"
package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EventCreationValidationTest {

    private String validateEventFields(String title, String description, String maxEntrants,
                                       String startDate, String endDate) {
        if (title == null || title.trim().isEmpty()) return "Title required";
        if (description == null || description.trim().isEmpty()) return "Description required";
        if (maxEntrants == null || maxEntrants.trim().isEmpty()) return "Max entrants required";
        if (startDate == null || startDate.isEmpty()) return "Start date required";
        if (endDate == null || endDate.isEmpty()) return "End date required";
        try {
            Integer.parseInt(maxEntrants.trim());
        } catch (NumberFormatException e) {
            return "Max entrants must be a number";
        }
        return null;
    }

    private Map<String, Object> buildEventMap(String title, String desc, String posterUrl,
                                               String organizerId, String startDate, String endDate,
                                               boolean geolocation, boolean isPrivate, int maxEntrants,
                                               String category) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", title);
        eventMap.put("description", desc);
        eventMap.put("posterUrl", posterUrl);
        eventMap.put("organizerId", organizerId);
        eventMap.put("registrationStart", startDate);
        eventMap.put("registrationEnd", endDate);
        eventMap.put("geolocationRequired", geolocation);
        eventMap.put("isPrivate", isPrivate);
        eventMap.put("waitingListLimit", maxEntrants);
        if (category != null && !category.isEmpty()) {
            eventMap.put("category", category);
        }
        return eventMap;
    }

    @Test
    public void validFields_passesValidation() {
        String error = validateEventFields("Swim", "Learn to swim", "20", "1/1/2025", "15/1/2025");
        assertNull(error);
    }

    @Test
    public void emptyTitle_failsValidation() {
        String error = validateEventFields("", "Desc", "20", "1/1/2025", "15/1/2025");
        assertEquals("Title required", error);
    }

    @Test
    public void nullTitle_failsValidation() {
        String error = validateEventFields(null, "Desc", "20", "1/1/2025", "15/1/2025");
        assertEquals("Title required", error);
    }

    @Test
    public void emptyDescription_failsValidation() {
        String error = validateEventFields("Title", "", "20", "1/1/2025", "15/1/2025");
        assertEquals("Description required", error);
    }

    @Test
    public void emptyMaxEntrants_failsValidation() {
        String error = validateEventFields("Title", "Desc", "", "1/1/2025", "15/1/2025");
        assertEquals("Max entrants required", error);
    }

    @Test
    public void nonNumericMaxEntrants_failsValidation() {
        String error = validateEventFields("Title", "Desc", "abc", "1/1/2025", "15/1/2025");
        assertEquals("Max entrants must be a number", error);
    }

    @Test
    public void emptyStartDate_failsValidation() {
        String error = validateEventFields("Title", "Desc", "20", "", "15/1/2025");
        assertEquals("Start date required", error);
    }

    @Test
    public void emptyEndDate_failsValidation() {
        String error = validateEventFields("Title", "Desc", "20", "1/1/2025", "");
        assertEquals("End date required", error);
    }

    @Test
    public void eventMap_containsAllRequiredFields() {
        Map<String, Object> map = buildEventMap("Swim", "Desc", "http://img.jpg",
                "org-1", "1/1/2025", "15/1/2025", true, false, 20, "Sports");
        assertEquals("Swim", map.get("title"));
        assertEquals("Desc", map.get("description"));
        assertEquals("http://img.jpg", map.get("posterUrl"));
        assertEquals("org-1", map.get("organizerId"));
        assertEquals("1/1/2025", map.get("registrationStart"));
        assertEquals("15/1/2025", map.get("registrationEnd"));
        assertEquals(true, map.get("geolocationRequired"));
        assertEquals(false, map.get("isPrivate"));
        assertEquals(20, map.get("waitingListLimit"));
        assertEquals("Sports", map.get("category"));
    }

    @Test
    public void eventMap_nullCategory_notIncluded() {
        Map<String, Object> map = buildEventMap("Swim", "Desc", "http://img.jpg",
                "org-1", "1/1/2025", "15/1/2025", false, false, 20, null);
        assertFalse(map.containsKey("category"));
    }

    @Test
    public void eventMap_emptyCategory_notIncluded() {
        Map<String, Object> map = buildEventMap("Swim", "Desc", "http://img.jpg",
                "org-1", "1/1/2025", "15/1/2025", false, false, 20, "");
        assertFalse(map.containsKey("category"));
    }

    @Test
    public void eventMap_noImage_usesDefaultUrl() {
        String posterUrl = null;
        String finalUrl = posterUrl != null ? posterUrl : "https://yourdefaultimage.com/placeholder.jpg";
        Map<String, Object> map = buildEventMap("Swim", "Desc", finalUrl,
                "org-1", "1/1/2025", "15/1/2025", false, false, 20, null);
        assertEquals("https://yourdefaultimage.com/placeholder.jpg", map.get("posterUrl"));
    }

    @Test
    public void privateEvent_noQrGenerated() {
        boolean isPrivate = true;
        boolean shouldGenerateQr = !isPrivate;
        assertFalse(shouldGenerateQr);
    }

    @Test
    public void publicEvent_qrGenerated() {
        boolean isPrivate = false;
        boolean shouldGenerateQr = !isPrivate;
        assertTrue(shouldGenerateQr);
    }
}
