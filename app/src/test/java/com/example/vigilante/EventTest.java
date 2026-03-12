// unit tests for the Event model — testing logic beyond getters/setters

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    // building a fully populated Event for use across tests
    private Event buildFullEvent() {
        Event e = new Event();
        e.setId("event-001");
        e.setTitle("Swimming Lessons");
        e.setDescription("Beginner class");
        e.setDate("Jan 15 2026");
        e.setLocation("Community Pool");
        e.setCapacity("20");
        e.setPrice("$45.00");
        e.setRegistrationStart("Dec 1 2025");
        e.setRegistrationEnd("Dec 15 2025");
        e.setOrganizerId("org-uid-123");
        e.setPosterUrl("https://example.com/poster.jpg");
        return e;
    }

    @Test
    public void firestoreDeserialization_noArgConstructorCreatesEmptyEvent() {
        // Firestore requires a no-arg constructor — verifying it doesn't throw and returns an object
        Event event = new Event();
        assertNotNull(event);
    }

    @Test
    public void settersPopulateAllFields_correctlyViaFirestorePath() {
        // simulating how Firestore deserializes: creates via no-arg constructor then calls setters
        Event event = buildFullEvent();
        assertEquals("event-001", event.getId());
        assertEquals("Swimming Lessons", event.getTitle());
        assertEquals("Beginner class", event.getDescription());
        assertEquals("Jan 15 2026", event.getDate());
        assertEquals("Community Pool", event.getLocation());
        assertEquals("20", event.getCapacity());
        assertEquals("$45.00", event.getPrice());
        assertEquals("Dec 1 2025", event.getRegistrationStart());
        assertEquals("Dec 15 2025", event.getRegistrationEnd());
        assertEquals("org-uid-123", event.getOrganizerId());
        assertEquals("https://example.com/poster.jpg", event.getPosterUrl());
    }

    @Test
    public void overwritingField_updatesValue() {
        // verifying that calling a setter twice overwrites the previous value
        Event event = new Event();
        event.setTitle("Old Title");
        event.setTitle("New Title");
        assertEquals("New Title", event.getTitle());
    }

    @Test
    public void twoEvents_withDifferentIds_areDistinct() {
        // verifying that two events with different IDs are not equal by ID
        Event a = new Event();
        a.setId("id-aaa");
        Event b = new Event();
        b.setId("id-bbb");
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    public void twoEvents_withSameId_haveMatchingIds() {
        Event a = new Event();
        a.setId("shared-id");
        Event b = new Event();
        b.setId("shared-id");
        assertEquals(a.getId(), b.getId());
    }

    @Test
    public void emptyStringTitle_isStored() {
        // verifying that an empty string title is stored as-is, not converted to null
        Event event = new Event();
        event.setTitle("");
        assertEquals("", event.getTitle());
    }

    @Test
    public void nullTitle_isStored() {
        // verifying that null title is stored — Firestore may deserialize missing fields as null
        Event event = new Event();
        event.setTitle(null);
        assertNull(event.getTitle());
    }

    @Test
    public void isSerializable() {
        // verifying Event implements Serializable so it can be passed between activities via Intent
        Event event = new Event();
        assertTrue(event instanceof java.io.Serializable);
    }
}
