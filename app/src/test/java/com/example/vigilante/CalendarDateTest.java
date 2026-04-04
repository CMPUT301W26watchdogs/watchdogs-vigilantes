// testing the calendar date normalization and filtering logic (Wildcard)

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

public class CalendarDateTest {

    // verifying that normalizeDate produces the expected "d/M/yyyy" format
    @Test
    public void testNormalizeDateProducesCorrectFormat() {
        String result = CalendarActivity.normalizeDate(15, 3, 2026);
        assertEquals("15/3/2026", result);
    }

    // verifying single digit day and month are not zero padded
    @Test
    public void testNormalizeDateSingleDigits() {
        String result = CalendarActivity.normalizeDate(5, 1, 2026);
        assertEquals("5/1/2026", result);
    }

    // verifying that normalizeEventDate handles a standard "d/M/yyyy" string
    @Test
    public void testNormalizeEventDateStandard() {
        String result = CalendarActivity.normalizeEventDate("15/3/2026");
        assertEquals("15/3/2026", result);
    }

    // verifying that normalizeEventDate trims leading and trailing whitespace
    @Test
    public void testNormalizeEventDateWithWhitespace() {
        String result = CalendarActivity.normalizeEventDate("  15/3/2026  ");
        assertEquals("15/3/2026", result);
    }

    // verifying that normalizeEventDate handles null input gracefully
    @Test
    public void testNormalizeEventDateNull() {
        String result = CalendarActivity.normalizeEventDate(null);
        assertEquals("", result);
    }

    // verifying that zero padded input like "05/01/2026" gets normalized to "5/1/2026"
    @Test
    public void testNormalizeEventDateZeroPadded() {
        String result = CalendarActivity.normalizeEventDate("05/01/2026");
        assertEquals("5/1/2026", result);
    }

    // verifying two different dates do not match after normalization
    @Test
    public void testDifferentDatesDoNotMatch() {
        String date1 = CalendarActivity.normalizeDate(15, 3, 2026);
        String date2 = CalendarActivity.normalizeDate(16, 3, 2026);
        assertNotEquals(date1, date2);
    }

    // verifying that an event's registration start date matches a normalized calendar date
    @Test
    public void testEventDateMatchesCalendarDate() {
        Event event = new Event();
        event.setRegistrationStart("15/3/2026");

        String calendarDate = CalendarActivity.normalizeDate(15, 3, 2026);
        String eventDate = CalendarActivity.normalizeEventDate(event.getRegistrationStart());

        assertEquals(calendarDate, eventDate);
    }

    // verifying that filtering works by matching event dates to selected date
    @Test
    public void testFilteringEventsByDate() {
        List<Event> events = new ArrayList<>();

        Event e1 = new Event();
        e1.setRegistrationStart("15/3/2026");
        e1.setTitle("March 15 Event");
        events.add(e1);

        Event e2 = new Event();
        e2.setRegistrationStart("16/3/2026");
        e2.setTitle("March 16 Event");
        events.add(e2);

        Event e3 = new Event();
        e3.setRegistrationStart("15/3/2026");
        e3.setTitle("Another March 15 Event");
        events.add(e3);

        String selectedDate = CalendarActivity.normalizeDate(15, 3, 2026);

        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (CalendarActivity.normalizeEventDate(event.getRegistrationStart()).equals(selectedDate)) {
                filtered.add(event);
            }
        }

        assertEquals(2, filtered.size());
        assertEquals("March 15 Event", filtered.get(0).getTitle());
        assertEquals("Another March 15 Event", filtered.get(1).getTitle());
    }

    // verifying that an event with no registration date does not match any calendar date
    @Test
    public void testEventWithNullDateDoesNotMatch() {
        Event event = new Event();
        event.setRegistrationStart(null);

        String calendarDate = CalendarActivity.normalizeDate(15, 3, 2026);
        String eventDate = CalendarActivity.normalizeEventDate(event.getRegistrationStart());

        assertNotEquals(calendarDate, eventDate);
    }
}
