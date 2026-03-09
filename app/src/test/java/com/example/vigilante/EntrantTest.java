package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntrantTest {

    private Entrant buildEntrant() {
        return new Entrant("id-1", "Alice Johnson", "alice@email.com", "780-111-2222", "Waiting");
    }

    @Test
    public void testGetId() {
        assertEquals("id-1", buildEntrant().getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Alice Johnson", buildEntrant().getName());
    }

    @Test
    public void testGetEmail() {
        assertEquals("alice@email.com", buildEntrant().getEmail());
    }

    @Test
    public void testGetPhone() {
        assertEquals("780-111-2222", buildEntrant().getPhone());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Waiting", buildEntrant().getStatus());
    }

    @Test
    public void testDifferentStatuses() {
        Entrant selected = new Entrant("2", "Bob", "bob@email.com", "", "Selected");
        Entrant cancelled = new Entrant("3", "Carol", "carol@email.com", "", "Cancelled");
        assertEquals("Selected", selected.getStatus());
        assertEquals("Cancelled", cancelled.getStatus());
    }
}
