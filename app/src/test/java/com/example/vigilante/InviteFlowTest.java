package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InviteFlowTest {

    private Map<String, Map<String, Object>> attendeesDb;
    private List<Map<String, Object>> notificationsDb;
    private Map<String, Map<String, Object>> usersDb;

    @Before
    public void setUp() {
        attendeesDb = new HashMap<>();
        notificationsDb = new ArrayList<>();
        usersDb = new HashMap<>();

        Map<String, Object> enabledUser = new HashMap<>();
        enabledUser.put("name", "Alice");
        enabledUser.put("notificationsEnabled", true);
        usersDb.put("uid-1", enabledUser);

        Map<String, Object> disabledUser = new HashMap<>();
        disabledUser.put("name", "Bob");
        disabledUser.put("notificationsEnabled", false);
        usersDb.put("uid-2", disabledUser);

        Map<String, Object> nullPrefUser = new HashMap<>();
        nullPrefUser.put("name", "Charlie");
        nullPrefUser.put("notificationsEnabled", null);
        usersDb.put("uid-3", nullPrefUser);
    }

    private void inviteSingleUser(Profile profile, String eventId, String eventTitle) {
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", profile.getId());
        attendeeData.put("name", profile.getName());
        attendeeData.put("email", profile.getEmail());
        attendeeData.put("status", "selected");
        attendeesDb.put(profile.getId(), attendeeData);

        Map<String, Object> userDoc = usersDb.get(profile.getId());
        if (userDoc != null) {
            Boolean notificationsEnabled = (Boolean) userDoc.get("notificationsEnabled");
            if (Boolean.FALSE.equals(notificationsEnabled)) return;
        }

        Map<String, Object> notif = new HashMap<>();
        notif.put("userId", profile.getId());
        notif.put("eventId", eventId);
        notif.put("title", "You've been invited!");
        notif.put("message", "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept or decline your invitation.");
        notif.put("read", false);
        notificationsDb.add(notif);
    }

    private Profile makeProfile(String id, String name, String email) {
        Profile p = new Profile();
        p.setId(id);
        p.setName(name);
        p.setEmail(email);
        return p;
    }

    @Test
    public void invite_setsStatusToSelected_notPending() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Dance Class");
        Map<String, Object> attendee = attendeesDb.get("uid-1");
        assertEquals("selected", attendee.get("status"));
    }

    @Test
    public void invite_createsAttendeeRecord() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertTrue(attendeesDb.containsKey("uid-1"));
        assertEquals("Alice", attendeesDb.get("uid-1").get("name"));
        assertEquals("alice@test.com", attendeesDb.get("uid-1").get("email"));
    }

    @Test
    public void invite_enabledUser_receivesNotification() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertEquals(1, notificationsDb.size());
        assertEquals("You've been invited!", notificationsDb.get(0).get("title"));
    }

    @Test
    public void invite_disabledUser_noNotification() {
        Profile p = makeProfile("uid-2", "Bob", "bob@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertEquals(0, notificationsDb.size());
        assertTrue(attendeesDb.containsKey("uid-2"));
    }

    @Test
    public void invite_nullPrefUser_receivesNotification() {
        Profile p = makeProfile("uid-3", "Charlie", "charlie@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertEquals(1, notificationsDb.size());
    }

    @Test
    public void invite_notificationContainsEventTitle() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Piano Lessons");
        assertTrue(((String) notificationsDb.get(0).get("message")).contains("Piano Lessons"));
    }

    @Test
    public void invite_nullEventTitle_fallbackInNotification() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", null);
        assertTrue(((String) notificationsDb.get(0).get("message")).contains("an event"));
    }

    @Test
    public void invite_overwritesPreviousAttendeeRecord() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertEquals(1, attendeesDb.size());
        inviteSingleUser(p, "event-1", "Dance");
        assertEquals(1, attendeesDb.size());
    }

    @Test
    public void invite_notificationReadIsFalse() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-1", "Dance");
        assertFalse((Boolean) notificationsDb.get(0).get("read"));
    }

    @Test
    public void invite_notificationContainsCorrectEventId() {
        Profile p = makeProfile("uid-1", "Alice", "alice@test.com");
        inviteSingleUser(p, "event-42", "Dance");
        assertEquals("event-42", notificationsDb.get(0).get("eventId"));
    }
}
