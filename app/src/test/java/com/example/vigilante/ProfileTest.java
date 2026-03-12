// unit tests for the Profile model — testing logic beyond getters/setters

package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileTest {

    // building a profile via setters (Firestore deserialization path)
    private Profile buildProfile(boolean isOrganizer) {
        Profile p = new Profile();
        p.setId("user-uid-abc");
        p.setName("Bob Smith");
        p.setEmail("bob@example.com");
        p.setOrganizer(isOrganizer);
        return p;
    }

    @Test
    public void firestoreDeserialization_noArgConstructorCreatesObject() {
        // Firestore requires a no-arg constructor — verifying it doesn't throw
        Profile profile = new Profile();
        assertNotNull(profile);
    }

    @Test
    public void regularUser_isNotOrganizer() {
        // verifying that a regular entrant profile correctly reflects isOrganizer = false
        Profile profile = buildProfile(false);
        assertFalse(profile.isOrganizer());
    }

    @Test
    public void organizer_isOrganizer() {
        // verifying that an organizer profile correctly reflects isOrganizer = true
        Profile profile = buildProfile(true);
        assertTrue(profile.isOrganizer());
    }

    @Test
    public void organizerFlag_canBeChanged() {
        // verifying that the organizer flag can be updated (e.g. admin grants organizer role)
        Profile profile = buildProfile(false);
        profile.setOrganizer(true);
        assertTrue(profile.isOrganizer());
    }

    @Test
    public void settersPopulateAllFields() {
        Profile profile = buildProfile(true);
        assertEquals("user-uid-abc", profile.getId());
        assertEquals("Bob Smith", profile.getName());
        assertEquals("bob@example.com", profile.getEmail());
        assertTrue(profile.isOrganizer());
    }

    @Test
    public void twoProfiles_withDifferentIds_areDistinct() {
        Profile a = new Profile();
        a.setId("uid-aaa");
        Profile b = new Profile();
        b.setId("uid-bbb");
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    public void nameField_canBeUpdated() {
        // verifying that profile name updates correctly — US 01.02.02 update profile info
        Profile profile = buildProfile(false);
        profile.setName("Updated Name");
        assertEquals("Updated Name", profile.getName());
    }

    @Test
    public void emailField_canBeUpdated() {
        // verifying that email updates correctly — US 01.02.02 update profile info
        Profile profile = buildProfile(false);
        profile.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", profile.getEmail());
    }
}
