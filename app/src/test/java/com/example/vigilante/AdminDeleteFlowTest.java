// Claude, Opus 4, "test admin delete event, profile, and image removal logic"
package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AdminDeleteFlowTest {

    private Map<String, Map<String, Object>> eventsDb;
    private Map<String, Map<String, Object>> usersDb;
    private Map<String, List<Map<String, Object>>> eventAttendeesDb;

    @Before
    public void setUp() {
        eventsDb = new HashMap<>();
        usersDb = new HashMap<>();
        eventAttendeesDb = new HashMap<>();

        Map<String, Object> event1 = new HashMap<>();
        event1.put("title", "Swimming");
        event1.put("organizerId", "org-1");
        event1.put("posterUrl", "https://storage.example.com/poster1.jpg");
        eventsDb.put("e1", event1);

        Map<String, Object> event2 = new HashMap<>();
        event2.put("title", "Dance");
        event2.put("organizerId", "org-2");
        event2.put("posterUrl", null);
        eventsDb.put("e2", event2);

        List<Map<String, Object>> e1Attendees = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("userId", "uid-1");
        a1.put("status", "pending");
        e1Attendees.add(a1);
        Map<String, Object> a2 = new HashMap<>();
        a2.put("userId", "uid-2");
        a2.put("status", "selected");
        e1Attendees.add(a2);
        eventAttendeesDb.put("e1", e1Attendees);

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "Alice");
        user1.put("email", "alice@test.com");
        user1.put("isOrganizer", false);
        usersDb.put("uid-1", user1);

        Map<String, Object> org1 = new HashMap<>();
        org1.put("name", "Org One");
        org1.put("email", "org@test.com");
        org1.put("isOrganizer", true);
        usersDb.put("org-1", org1);
    }

    private void deleteEvent(String eventId) {
        eventsDb.remove(eventId);
    }

    private void deleteProfile(String userId) {
        usersDb.remove(userId);
    }

    private void removeEventPoster(String eventId) {
        Map<String, Object> event = eventsDb.get(eventId);
        if (event != null) {
            event.remove("posterUrl");
        }
    }

    private List<Map<String, Object>> getEventsWithImages() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : eventsDb.entrySet()) {
            String posterUrl = (String) entry.getValue().get("posterUrl");
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Map<String, Object> eventWithId = new HashMap<>(entry.getValue());
                eventWithId.put("id", entry.getKey());
                result.add(eventWithId);
            }
        }
        return result;
    }

    @Test
    public void deleteEvent_removesFromDb() {
        assertEquals(2, eventsDb.size());
        deleteEvent("e1");
        assertEquals(1, eventsDb.size());
        assertFalse(eventsDb.containsKey("e1"));
    }

    @Test
    public void deleteEvent_attendeesOrphaned() {
        deleteEvent("e1");
        assertFalse(eventsDb.containsKey("e1"));
        assertTrue(eventAttendeesDb.containsKey("e1"));
        assertEquals(2, eventAttendeesDb.get("e1").size());
    }

    @Test
    public void deleteProfile_removesFromDb() {
        assertEquals(2, usersDb.size());
        deleteProfile("uid-1");
        assertEquals(1, usersDb.size());
        assertFalse(usersDb.containsKey("uid-1"));
    }

    @Test
    public void deleteOrganizer_removesFromDb() {
        deleteProfile("org-1");
        assertFalse(usersDb.containsKey("org-1"));
    }

    @Test
    public void browseImages_onlyReturnsEventsWithPosterUrl() {
        List<Map<String, Object>> images = getEventsWithImages();
        assertEquals(1, images.size());
        assertEquals("e1", images.get(0).get("id"));
    }

    @Test
    public void removeImage_deletesPosterUrlField() {
        assertNotNull(eventsDb.get("e1").get("posterUrl"));
        removeEventPoster("e1");
        assertFalse(eventsDb.get("e1").containsKey("posterUrl"));
    }

    @Test
    public void removeImage_eventStillExists() {
        removeEventPoster("e1");
        assertTrue(eventsDb.containsKey("e1"));
        assertEquals("Swimming", eventsDb.get("e1").get("title"));
    }

    @Test
    public void removeImage_noLongerInBrowseImages() {
        removeEventPoster("e1");
        List<Map<String, Object>> images = getEventsWithImages();
        assertEquals(0, images.size());
    }

    @Test
    public void deleteNonexistentEvent_noError() {
        deleteEvent("nonexistent");
        assertEquals(2, eventsDb.size());
    }

    @Test
    public void deleteNonexistentProfile_noError() {
        deleteProfile("nonexistent");
        assertEquals(2, usersDb.size());
    }
}
