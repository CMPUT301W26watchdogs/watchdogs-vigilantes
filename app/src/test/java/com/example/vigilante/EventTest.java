package com.example.vigilante;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class EventTest {

    private Event buildEvent(String id) {
        return new Event(id, "Swimming Lessons", "Beginner class", "Jan 15 2026",
                "Community Pool", "20", "$45.00", "Dec 1 2025", "Dec 15 2025");
    }

    @Test
    public void testGetId() {
        Event event = buildEvent("test-id-001");
        assertEquals("test-id-001", event.getId());
    }

    @Test
    public void testGetTitle() {
        Event event = buildEvent("x");
        assertEquals("Swimming Lessons", event.getTitle());
    }

    @Test
    public void testGetDescription() {
        Event event = buildEvent("x");
        assertEquals("Beginner class", event.getDescription());
    }

    @Test
    public void testGetDate() {
        Event event = buildEvent("x");
        assertEquals("Jan 15 2026", event.getDate());
    }

    @Test
    public void testGetLocation() {
        Event event = buildEvent("x");
        assertEquals("Community Pool", event.getLocation());
    }

    @Test
    public void testGetCapacity() {
        Event event = buildEvent("x");
        assertEquals("20", event.getCapacity());
    }

    @Test
    public void testGetPrice() {
        Event event = buildEvent("x");
        assertEquals("$45.00", event.getPrice());
    }

    @Test
    public void testGetRegistrationStart() {
        Event event = buildEvent("x");
        assertEquals("Dec 1 2025", event.getRegistrationStart());
    }

    @Test
    public void testGetRegistrationEnd() {
        Event event = buildEvent("x");
        assertEquals("Dec 15 2025", event.getRegistrationEnd());
    }

    @Test
    public void testUUIDsAreUnique() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testUUIDIsNotEmpty() {
        String id = UUID.randomUUID().toString();
        assertFalse(id.isEmpty());
    }

    @Test
    public void testEventIdMatchesUUID() {
        String id = UUID.randomUUID().toString();
        Event event = buildEvent(id);
        assertEquals(id, event.getId());
    }
}
