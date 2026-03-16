package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EventCategoryFilterTest {

    private List<Event> allEvents;

    private Event makeEvent(String title, String category) {
        Event e = new Event();
        e.setTitle(title);
        e.setCategory(category);
        return e;
    }

    @Before
    public void setUp() {
        allEvents = new ArrayList<>();
        allEvents.add(makeEvent("Swimming", "Sports"));
        allEvents.add(makeEvent("Painting", "Arts"));
        allEvents.add(makeEvent("Guitar Class", "Music"));
        allEvents.add(makeEvent("Basketball", "Sports"));
        allEvents.add(makeEvent("Choir", "Music"));
        allEvents.add(makeEvent("No Category", null));
    }

    private List<Event> applyFilter(String filter) {
        List<Event> filtered = new ArrayList<>();
        if ("All".equals(filter)) {
            filtered.addAll(allEvents);
        } else {
            for (Event event : allEvents) {
                String cat = event.getCategory();
                if (cat != null && cat.equalsIgnoreCase(filter)) {
                    filtered.add(event);
                }
            }
        }
        return filtered;
    }

    @Test
    public void filterAll_returnsAllEvents() {
        List<Event> result = applyFilter("All");
        assertEquals(6, result.size());
    }

    @Test
    public void filterSports_returnsOnlySportsEvents() {
        List<Event> result = applyFilter("Sports");
        assertEquals(2, result.size());
        for (Event e : result) {
            assertEquals("Sports", e.getCategory());
        }
    }

    @Test
    public void filterArts_returnsOnlyArtsEvents() {
        List<Event> result = applyFilter("Arts");
        assertEquals(1, result.size());
        assertEquals("Painting", result.get(0).getTitle());
    }

    @Test
    public void filterMusic_returnsOnlyMusicEvents() {
        List<Event> result = applyFilter("Music");
        assertEquals(2, result.size());
        for (Event e : result) {
            assertEquals("Music", e.getCategory());
        }
    }

    @Test
    public void filterUnknownCategory_returnsEmpty() {
        List<Event> result = applyFilter("Technology");
        assertEquals(0, result.size());
    }

    @Test
    public void filterIsCaseInsensitive() {
        List<Event> result = applyFilter("sports");
        assertEquals(2, result.size());
    }

    @Test
    public void eventWithNullCategory_excludedFromCategoryFilter() {
        List<Event> result = applyFilter("Sports");
        for (Event e : result) {
            assertNotNull(e.getCategory());
        }
    }

    @Test
    public void eventWithNullCategory_includedInAllFilter() {
        List<Event> result = applyFilter("All");
        boolean foundNull = false;
        for (Event e : result) {
            if (e.getCategory() == null) {
                foundNull = true;
                break;
            }
        }
        assertTrue(foundNull);
    }

    @Test
    public void emptyEventList_allFilterReturnsEmpty() {
        allEvents.clear();
        List<Event> result = applyFilter("All");
        assertEquals(0, result.size());
    }

    @Test
    public void emptyEventList_categoryFilterReturnsEmpty() {
        allEvents.clear();
        List<Event> result = applyFilter("Sports");
        assertEquals(0, result.size());
    }
}
