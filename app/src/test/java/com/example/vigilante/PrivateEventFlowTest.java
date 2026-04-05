package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PrivateEventFlowTest {

    private List<Map<String, Object>> allEventsDb;

    private Map<String, Object> makeEventDoc(String id, String title, Boolean isPrivate, String organizerId) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("title", title);
        doc.put("isPrivate", isPrivate);
        doc.put("organizerId", organizerId);
        return doc;
    }

    private List<Map<String, Object>> getPublicEvents() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> doc : allEventsDb) {
            Boolean isPrivate = (Boolean) doc.get("isPrivate");
            if (!Boolean.TRUE.equals(isPrivate)) {
                result.add(doc);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getOrganizerEvents(String organizerId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> doc : allEventsDb) {
            if (organizerId.equals(doc.get("organizerId"))) {
                result.add(doc);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getAdminEvents() {
        return new ArrayList<>(allEventsDb);
    }

    @Before
    public void setUp() {
        allEventsDb = new ArrayList<>();
        allEventsDb.add(makeEventDoc("e1", "Public Swim", false, "org-1"));
        allEventsDb.add(makeEventDoc("e2", "Private Dance", true, "org-1"));
        allEventsDb.add(makeEventDoc("e3", "Public Music", false, "org-2"));
        allEventsDb.add(makeEventDoc("e4", "Private Art", true, "org-2"));
        allEventsDb.add(makeEventDoc("e5", "Null Flag Event", null, "org-1"));
    }

    @Test
    public void publicView_excludesPrivateEvents() {
        List<Map<String, Object>> publicEvents = getPublicEvents();
        assertEquals(3, publicEvents.size());
        for (Map<String, Object> e : publicEvents) {
            assertFalse(Boolean.TRUE.equals(e.get("isPrivate")));
        }
    }

    @Test
    public void publicView_nullPrivateFlag_treatedAsPublic() {
        List<Map<String, Object>> publicEvents = getPublicEvents();
        boolean foundNull = false;
        for (Map<String, Object> e : publicEvents) {
            if ("e5".equals(e.get("id"))) foundNull = true;
        }
        assertTrue(foundNull);
    }

    @Test
    public void organizerView_seesOwnPrivateEvents() {
        List<Map<String, Object>> orgEvents = getOrganizerEvents("org-1");
        assertEquals(3, orgEvents.size());
        boolean foundPrivate = false;
        for (Map<String, Object> e : orgEvents) {
            if (Boolean.TRUE.equals(e.get("isPrivate"))) foundPrivate = true;
        }
        assertTrue(foundPrivate);
    }

    @Test
    public void organizerView_doesNotSeeOtherOrganizerEvents() {
        List<Map<String, Object>> orgEvents = getOrganizerEvents("org-1");
        for (Map<String, Object> e : orgEvents) {
            assertEquals("org-1", e.get("organizerId"));
        }
    }

    @Test
    public void adminView_seesAllEvents() {
        List<Map<String, Object>> adminEvents = getAdminEvents();
        assertEquals(5, adminEvents.size());
    }

    @Test
    public void adminView_includesPrivateEvents() {
        List<Map<String, Object>> adminEvents = getAdminEvents();
        int privateCount = 0;
        for (Map<String, Object> e : adminEvents) {
            if (Boolean.TRUE.equals(e.get("isPrivate"))) privateCount++;
        }
        assertEquals(2, privateCount);
    }
}
