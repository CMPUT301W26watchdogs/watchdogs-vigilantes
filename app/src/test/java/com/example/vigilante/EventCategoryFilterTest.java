// unit tests for category-based event filtering logic — case insensitivity, null categories, empty lists — US 01.01.04

package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EventCategoryFilterTest {

    private List<Event> allEvents;

    private Event makeEvent(String title, String category) {
        // creating a test event with the given title and category
        Event e = new Event();
        e.setTitle(title);
        e.setCategory(category);
        return e;
    }

    @Before
    public void setUp() {
        // creating test data with events across Sports, Arts, Music and null categories
        allEvents = new ArrayList<>();
        allEvents.add(makeEvent("Swimming", "Sports"));
        allEvents.add(makeEvent("Painting", "Arts"));
        allEvents.add(makeEvent("Guitar Class", "Music"));
        allEvents.add(makeEvent("Basketball", "Sports"));
        allEvents.add(makeEvent("Choir", "Music"));
        allEvents.add(makeEvent("No Category", null));
    }

    private List<Event> applyFilter(String filter) {
        // applying filter logic — "All" returns everything, otherwise filters by category (case-insensitive)
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
        // verifying "All" filter returns all 6 events — US 01.01.04
        assertEquals(6, result.size());
    }

    @Test
    public void filterSports_returnsOnlySportsEvents() {
        List<Event> result = applyFilter("Sports");
        // verifying Sports filter returns only the 2 sports events — US 01.01.04
        assertEquals(2, result.size());
        for (Event e : result) {
            assertEquals("Sports", e.getCategory());
        }
    }

    @Test
    public void filterArts_returnsOnlyArtsEvents() {
        List<Event> result = applyFilter("Arts");
        // verifying Arts filter returns only the 1 arts event — US 01.01.04
        assertEquals(1, result.size());
        assertEquals("Painting", result.get(0).getTitle());
    }

    @Test
    public void filterMusic_returnsOnlyMusicEvents() {
        List<Event> result = applyFilter("Music");
        // verifying Music filter returns only the 2 music events — US 01.01.04
        assertEquals(2, result.size());
        for (Event e : result) {
            assertEquals("Music", e.getCategory());
        }
    }

    @Test
    public void filterUnknownCategory_returnsEmpty() {
        List<Event> result = applyFilter("Technology");
        // verifying an unknown category returns an empty result — US 01.01.04
        assertEquals(0, result.size());
    }

    @Test
    public void filterIsCaseInsensitive() {
        List<Event> result = applyFilter("sports");
        // verifying filter is case-insensitive (lowercase "sports" matches "Sports") — US 01.01.04
        assertEquals(2, result.size());
    }

    @Test
    public void eventWithNullCategory_excludedFromCategoryFilter() {
        List<Event> result = applyFilter("Sports");
        // verifying null-category events are excluded from specific category filters — US 01.01.04
        for (Event e : result) {
            assertNotNull(e.getCategory());
        }
    }

    @Test
    public void eventWithNullCategory_includedInAllFilter() {
        List<Event> result = applyFilter("All");
        // verifying null-category events are included when "All" filter is selected — US 01.01.04
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
        // verifying "All" filter on an empty list returns empty — US 01.01.04
        assertEquals(0, result.size());
    }

    @Test
    public void emptyEventList_categoryFilterReturnsEmpty() {
        allEvents.clear();
        List<Event> result = applyFilter("Sports");
        // verifying category filter on an empty list returns empty — US 01.01.04
        assertEquals(0, result.size());
    }
}
