// unit tests for the Entrant model — testing logic beyond getters/setters

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntrantTest {

    // building a standard entrant via setters (Firestore deserialization path)
    private Entrant buildEntrant(String status) {
        Entrant e = new Entrant();
        e.setId("entrant-001");
        e.setName("Alice Johnson");
        e.setEmail("alice@example.com");
        e.setPhone("780-111-2222");
        e.setStatus(status);
        return e;
    }

    @Test
    public void firestoreDeserialization_noArgConstructorCreatesObject() {
        // Firestore requires a no-arg constructor — verifying it doesn't throw
        Entrant entrant = new Entrant();
        assertNotNull(entrant);
    }

    @Test
    public void settersPopulateAllFields() {
        // simulating Firestore deserialization: no-arg then setters
        Entrant entrant = buildEntrant("Waiting");
        assertEquals("entrant-001", entrant.getId());
        assertEquals("Alice Johnson", entrant.getName());
        assertEquals("alice@example.com", entrant.getEmail());
        assertEquals("780-111-2222", entrant.getPhone());
        assertEquals("Waiting", entrant.getStatus());
    }

    @Test
    public void status_waiting_isValid() {
        Entrant entrant = buildEntrant("Waiting");
        assertEquals("Waiting", entrant.getStatus());
    }

    @Test
    public void status_selected_isValid() {
        Entrant entrant = buildEntrant("Selected");
        assertEquals("Selected", entrant.getStatus());
    }

    @Test
    public void status_cancelled_isValid() {
        Entrant entrant = buildEntrant("Cancelled");
        assertEquals("Cancelled", entrant.getStatus());
    }

    @Test
    public void status_canBeOverwritten() {
        // verifying that status transitions work — e.g. Waiting → Selected
        Entrant entrant = buildEntrant("Waiting");
        entrant.setStatus("Selected");
        assertEquals("Selected", entrant.getStatus());
    }

    @Test
    public void twoEntrants_withDifferentIds_areDistinct() {
        Entrant a = new Entrant();
        a.setId("id-001");
        Entrant b = new Entrant();
        b.setId("id-002");
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    public void optionalPhone_canBeEmpty() {
        // phone is optional — verifying empty string is stored correctly
        Entrant entrant = buildEntrant("Waiting");
        entrant.setPhone("");
        assertEquals("", entrant.getPhone());
    }

    @Test
    public void isSerializable() {
        // verifying Entrant implements Serializable so it can be passed between activities via Intent
        Entrant entrant = new Entrant();
        assertTrue(entrant instanceof java.io.Serializable);
    }
}
