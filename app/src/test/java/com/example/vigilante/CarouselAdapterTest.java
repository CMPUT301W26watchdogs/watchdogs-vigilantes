// testing the carousel adapter data handling and event update logic (Wildcard)

package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CarouselAdapterTest {

    private List<Event> events;

    private Event makeEvent(String id, String title, String description, String category) {
        // creating a test event with the given fields
        Event e = new Event();
        e.setId(id);
        e.setTitle(title);
        e.setDescription(description);
        e.setCategory(category);
        return e;
    }

    @Before
    public void setUp() {
        // setting up a list of test events for the carousel
        events = new ArrayList<>();
        events.add(makeEvent("e1", "Swimming Lessons", "Learn to swim", "Sports"));
        events.add(makeEvent("e2", "Art Workshop", "Painting class", "Arts"));
        events.add(makeEvent("e3", "Live Concert", "Jazz night", "Music"));
    }

    // verifying the adapter reports the correct item count (Wildcard)
    @Test
    public void testGetItemCount() {
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals(3, adapter.getItemCount());
    }

    // verifying the adapter returns zero for an empty event list (Wildcard)
    @Test
    public void testEmptyListReturnsZeroCount() {
        CarouselAdapter adapter = new CarouselAdapter(null, new ArrayList<>());
        assertEquals(0, adapter.getItemCount());
    }

    // verifying the adapter holds the same event objects that were passed in (Wildcard)
    @Test
    public void testGetEventsReturnsSameList() {
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals(events, adapter.getEvents());
    }

    // verifying that replacing the event list changes the item count (Wildcard)
    @Test
    public void testReplacingEventListChangesCount() {
        List<Event> newEvents = new ArrayList<>();
        newEvents.add(makeEvent("e4", "Yoga", "Morning yoga", "Sports"));
        CarouselAdapter adapter = new CarouselAdapter(null, newEvents);

        assertEquals(1, adapter.getItemCount());
        assertEquals("Yoga", adapter.getEvents().get(0).getTitle());
    }

    // verifying that an adapter created with an empty list has zero items (Wildcard)
    @Test
    public void testEmptyListAtCreation() {
        CarouselAdapter adapter = new CarouselAdapter(null, new ArrayList<>());
        assertEquals(0, adapter.getItemCount());
        assertTrue(adapter.getEvents().isEmpty());
    }

    // verifying the first event in the adapter has the correct title (Wildcard)
    @Test
    public void testFirstEventHasCorrectTitle() {
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals("Swimming Lessons", adapter.getEvents().get(0).getTitle());
    }

    // verifying the adapter preserves event IDs from the input list (Wildcard)
    @Test
    public void testEventIdsArePreserved() {
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals("e1", adapter.getEvents().get(0).getId());
        assertEquals("e2", adapter.getEvents().get(1).getId());
        assertEquals("e3", adapter.getEvents().get(2).getId());
    }

    // verifying event categories are preserved in the adapter (Wildcard)
    @Test
    public void testEventCategoriesArePreserved() {
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals("Sports", adapter.getEvents().get(0).getCategory());
        assertEquals("Arts", adapter.getEvents().get(1).getCategory());
        assertEquals("Music", adapter.getEvents().get(2).getCategory());
    }

    // verifying an event with null category does not break the adapter (Wildcard)
    @Test
    public void testNullCategoryDoesNotCrash() {
        events.add(makeEvent("e4", "Mystery Event", "Unknown", null));
        CarouselAdapter adapter = new CarouselAdapter(null, events);
        assertEquals(4, adapter.getItemCount());
        assertNull(adapter.getEvents().get(3).getCategory());
    }

    // verifying the adapter handles a single event correctly (Wildcard)
    @Test
    public void testSingleEventList() {
        List<Event> single = new ArrayList<>();
        single.add(makeEvent("solo", "Solo Event", "Just one", "Sports"));
        CarouselAdapter adapter = new CarouselAdapter(null, single);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Solo Event", adapter.getEvents().get(0).getTitle());
    }
}
