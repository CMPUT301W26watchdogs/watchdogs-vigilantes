package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EventSearchFilterTest {

    private List<Event> allEvents;

    private Event makeEvent(String title, String category, String location) {
        Event e = new Event();
        e.setTitle(title);
        e.setCategory(category);
        e.setLocation(location);
        return e;
    }

    private List<Event> applyFilter(String categoryFilter, String searchQuery) {
        String query = searchQuery != null ? searchQuery.toLowerCase().trim() : "";
        List<Event> filtered = new ArrayList<>();
        for (Event event : allEvents) {
            boolean matchesCategory = "All".equals(categoryFilter) ||
                    (event.getCategory() != null && event.getCategory().equalsIgnoreCase(categoryFilter));

            boolean matchesSearch = query.isEmpty() ||
                    (event.getTitle() != null && event.getTitle().toLowerCase().contains(query)) ||
                    (event.getCategory() != null && event.getCategory().toLowerCase().contains(query)) ||
                    (event.getLocation() != null && event.getLocation().toLowerCase().contains(query));

            if (matchesCategory && matchesSearch) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    @Before
    public void setUp() {
        allEvents = new ArrayList<>();
        allEvents.add(makeEvent("Swimming Lessons", "Sports", "Downtown Pool"));
        allEvents.add(makeEvent("Painting Workshop", "Arts", "Community Centre"));
        allEvents.add(makeEvent("Guitar Class", "Music", "Music Hall"));
        allEvents.add(makeEvent("Basketball Tournament", "Sports", "East Gym"));
        allEvents.add(makeEvent("Choir Practice", "Music", null));
        allEvents.add(makeEvent("Open Mic Night", null, "Downtown Bar"));
    }

    @Test
    public void searchByTitle_findsMatchingEvent() {
        List<Event> result = applyFilter("All", "swimming");
        assertEquals(1, result.size());
        assertEquals("Swimming Lessons", result.get(0).getTitle());
    }

    @Test
    public void searchByLocation_findsMatchingEvents() {
        List<Event> result = applyFilter("All", "downtown");
        assertEquals(2, result.size());
    }

    @Test
    public void searchByCategory_findsMatchingEvents() {
        List<Event> result = applyFilter("All", "sports");
        assertEquals(2, result.size());
    }

    @Test
    public void searchIsCaseInsensitive() {
        List<Event> result1 = applyFilter("All", "SWIMMING");
        List<Event> result2 = applyFilter("All", "swimming");
        assertEquals(result1.size(), result2.size());
    }

    @Test
    public void emptySearch_returnsAll() {
        List<Event> result = applyFilter("All", "");
        assertEquals(6, result.size());
    }

    @Test
    public void nullSearch_returnsAll() {
        List<Event> result = applyFilter("All", null);
        assertEquals(6, result.size());
    }

    @Test
    public void searchWithNoMatch_returnsEmpty() {
        List<Event> result = applyFilter("All", "nonexistent");
        assertEquals(0, result.size());
    }

    @Test
    public void categoryPlusSearch_combinedFilter() {
        List<Event> result = applyFilter("Sports", "basketball");
        assertEquals(1, result.size());
        assertEquals("Basketball Tournament", result.get(0).getTitle());
    }

    @Test
    public void categoryPlusSearch_noOverlap_returnsEmpty() {
        List<Event> result = applyFilter("Arts", "basketball");
        assertEquals(0, result.size());
    }

    @Test
    public void nullLocation_doesNotCrash_searchByLocation() {
        List<Event> result = applyFilter("All", "Music Hall");
        assertEquals(1, result.size());
        assertEquals("Guitar Class", result.get(0).getTitle());
    }

    @Test
    public void nullCategory_excludedFromCategoryFilter_butSearchable() {
        List<Event> result = applyFilter("Music", "");
        assertEquals(2, result.size());

        List<Event> result2 = applyFilter("All", "mic");
        assertEquals(1, result2.size());
        assertEquals("Open Mic Night", result2.get(0).getTitle());
    }

    @Test
    public void searchWithWhitespace_trimmed() {
        List<Event> result = applyFilter("All", "  swimming  ");
        assertEquals(1, result.size());
    }
}
