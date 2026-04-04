// testing the confirmation ticket generator data handling and text truncation logic (Wildcard)

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TicketGeneratorTest {

    // verifying the generator stores the correct event title (Wildcard)
    @Test
    public void testStoresEventTitle() {
        TicketGenerator gen = new TicketGenerator("Swimming Lessons", "Mar 10", "Pool", "Ved", "abc123");
        assertEquals("Swimming Lessons", gen.getEventTitle());
    }

    // verifying the generator stores the correct event date (Wildcard)
    @Test
    public void testStoresEventDate() {
        TicketGenerator gen = new TicketGenerator("Concert", "Apr 5", "Arena", "Ved", "xyz789");
        assertEquals("Apr 5", gen.getEventDate());
    }

    // verifying the generator stores the correct event location (Wildcard)
    @Test
    public void testStoresEventLocation() {
        TicketGenerator gen = new TicketGenerator("Concert", "Apr 5", "Rogers Place", "Ved", "xyz789");
        assertEquals("Rogers Place", gen.getEventLocation());
    }

    // verifying the generator stores the correct attendee name (Wildcard)
    @Test
    public void testStoresAttendeeName() {
        TicketGenerator gen = new TicketGenerator("Concert", "Apr 5", "Arena", "John Smith", "xyz789");
        assertEquals("John Smith", gen.getAttendeeName());
    }

    // verifying the generator stores the correct ticket ID (Wildcard)
    @Test
    public void testStoresTicketId() {
        TicketGenerator gen = new TicketGenerator("Concert", "Apr 5", "Arena", "Ved", "ticket001");
        assertEquals("ticket001", gen.getTicketId());
    }

    // verifying null event title defaults to Untitled Event (Wildcard)
    @Test
    public void testNullTitleDefaultsToUntitled() {
        TicketGenerator gen = new TicketGenerator(null, "Mar 10", "Pool", "Ved", "abc123");
        assertEquals("Untitled Event", gen.getEventTitle());
    }

    // verifying null date defaults to TBD (Wildcard)
    @Test
    public void testNullDateDefaultsToTBD() {
        TicketGenerator gen = new TicketGenerator("Event", null, "Pool", "Ved", "abc123");
        assertEquals("TBD", gen.getEventDate());
    }

    // verifying null location defaults to TBD (Wildcard)
    @Test
    public void testNullLocationDefaultsToTBD() {
        TicketGenerator gen = new TicketGenerator("Event", "Mar 10", null, "Ved", "abc123");
        assertEquals("TBD", gen.getEventLocation());
    }

    // verifying null attendee name defaults to Attendee (Wildcard)
    @Test
    public void testNullAttendeeDefaultsToAttendee() {
        TicketGenerator gen = new TicketGenerator("Event", "Mar 10", "Pool", null, "abc123");
        assertEquals("Attendee", gen.getAttendeeName());
    }

    // verifying null ticket ID defaults to 000000 (Wildcard)
    @Test
    public void testNullTicketIdDefaultsToZeroes() {
        TicketGenerator gen = new TicketGenerator("Event", "Mar 10", "Pool", "Ved", null);
        assertEquals("000000", gen.getTicketId());
    }

    // verifying page dimensions are set to the expected ticket stub size (Wildcard)
    @Test
    public void testPageDimensions() {
        assertEquals(432, TicketGenerator.PAGE_WIDTH);
        assertEquals(216, TicketGenerator.PAGE_HEIGHT);
    }

    // verifying all fields can be set independently without affecting each other (Wildcard)
    @Test
    public void testFieldsAreIndependent() {
        TicketGenerator gen1 = new TicketGenerator("A", "B", "C", "D", "E");
        TicketGenerator gen2 = new TicketGenerator("X", "Y", "Z", "W", "V");
        assertNotEquals(gen1.getEventTitle(), gen2.getEventTitle());
        assertNotEquals(gen1.getTicketId(), gen2.getTicketId());
    }

    // verifying the generator handles empty strings without defaulting (Wildcard)
    @Test
    public void testEmptyStringsAreKeptAsIs() {
        TicketGenerator gen = new TicketGenerator("", "", "", "", "");
        assertEquals("", gen.getEventTitle());
        assertEquals("", gen.getEventDate());
    }

    // verifying a long ticket ID is stored fully (Wildcard)
    @Test
    public void testLongTicketIdIsStored() {
        String longId = "abcdefghijklmnop123456789";
        TicketGenerator gen = new TicketGenerator("Event", "Date", "Loc", "Name", longId);
        assertEquals(longId, gen.getTicketId());
    }

    // verifying special characters in event title are preserved (Wildcard)
    @Test
    public void testSpecialCharactersInTitle() {
        TicketGenerator gen = new TicketGenerator("Event & Music @ Park!", "Mar 10", "Pool", "Ved", "abc");
        assertEquals("Event & Music @ Park!", gen.getEventTitle());
    }
}
