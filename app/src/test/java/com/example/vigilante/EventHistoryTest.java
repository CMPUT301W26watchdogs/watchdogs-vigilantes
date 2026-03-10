package com.example.vigilante;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventHistoryTest {

    @Test
    public void testEventHistoryCreation() {

        EventHistory event = new EventHistory("AI Conference", "Selected");

        assertEquals("AI Conference", event.getEventTitle());
        assertEquals("Selected", event.getStatus());
    }
}