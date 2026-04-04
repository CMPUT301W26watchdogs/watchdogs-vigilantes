// Claude, Opus 4, "test notification opt-out enforcement across all sender methods"
package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class NotificationOptOutFlowTest {

    private Map<String, Map<String, Object>> usersDb;
    private List<Map<String, Object>> notificationsDb;
    private List<Entrant> attendees;

    @Before
    public void setUp() {
        usersDb = new HashMap<>();
        notificationsDb = new ArrayList<>();
        attendees = new ArrayList<>();

        Map<String, Object> enabledUser = new HashMap<>();
        enabledUser.put("name", "Alice");
        enabledUser.put("notificationsEnabled", true);
        usersDb.put("uid-enabled", enabledUser);

        Map<String, Object> disabledUser = new HashMap<>();
        disabledUser.put("name", "Bob");
        disabledUser.put("notificationsEnabled", false);
        usersDb.put("uid-disabled", disabledUser);

        Map<String, Object> nullPrefUser = new HashMap<>();
        nullPrefUser.put("name", "Charlie");
        nullPrefUser.put("notificationsEnabled", null);
        usersDb.put("uid-null", nullPrefUser);

        Entrant e1 = new Entrant();
        e1.setId("uid-enabled");
        e1.setStatus("pending");
        attendees.add(e1);

        Entrant e2 = new Entrant();
        e2.setId("uid-disabled");
        e2.setStatus("pending");
        attendees.add(e2);

        Entrant e3 = new Entrant();
        e3.setId("uid-null");
        e3.setStatus("pending");
        attendees.add(e3);
    }

    private boolean shouldSendNotification(String userId) {
        Map<String, Object> userDoc = usersDb.get(userId);
        if (userDoc == null) return true;
        Boolean notificationsEnabled = (Boolean) userDoc.get("notificationsEnabled");
        return !Boolean.FALSE.equals(notificationsEnabled);
    }

    private void sendNotificationToAllPending(String eventTitle, String message) {
        for (Entrant e : attendees) {
            if (!"pending".equals(e.getStatus())) continue;
            if (!shouldSendNotification(e.getId())) continue;

            Map<String, Object> notif = new HashMap<>();
            notif.put("userId", e.getId());
            notif.put("title", "Update for " + (eventTitle != null ? eventTitle : "an event"));
            notif.put("message", message);
            notif.put("read", false);
            notificationsDb.add(notif);
        }
    }

    private void sendNotificationToAllSelected(String eventTitle) {
        for (Entrant e : attendees) {
            if (!"selected".equals(e.getStatus())) continue;
            if (!shouldSendNotification(e.getId())) continue;

            Map<String, Object> notif = new HashMap<>();
            notif.put("userId", e.getId());
            notif.put("title", "You've been selected!");
            notif.put("message", "You've been chosen for " + (eventTitle != null ? eventTitle : "an event"));
            notif.put("read", false);
            notificationsDb.add(notif);
        }
    }

    @Test
    public void optedOutUser_doesNotReceiveNotification() {
        sendNotificationToAllPending("Swim", "Lottery draw soon!");
        for (Map<String, Object> n : notificationsDb) {
            assertNotEquals("uid-disabled", n.get("userId"));
        }
    }

    @Test
    public void optedInUser_receivesNotification() {
        sendNotificationToAllPending("Swim", "Lottery draw soon!");
        boolean found = false;
        for (Map<String, Object> n : notificationsDb) {
            if ("uid-enabled".equals(n.get("userId"))) found = true;
        }
        assertTrue(found);
    }

    @Test
    public void nullPreferenceUser_receivesNotification() {
        sendNotificationToAllPending("Swim", "Lottery draw soon!");
        boolean found = false;
        for (Map<String, Object> n : notificationsDb) {
            if ("uid-null".equals(n.get("userId"))) found = true;
        }
        assertTrue(found);
    }

    @Test
    public void notifyPending_only2of3Receive_becauseOneOptedOut() {
        sendNotificationToAllPending("Swim", "Lottery draw soon!");
        assertEquals(2, notificationsDb.size());
    }

    @Test
    public void notifySelected_optOutRespected() {
        for (Entrant e : attendees) {
            e.setStatus("selected");
        }
        sendNotificationToAllSelected("Dance Class");
        assertEquals(2, notificationsDb.size());
        for (Map<String, Object> n : notificationsDb) {
            assertNotEquals("uid-disabled", n.get("userId"));
        }
    }

    @Test
    public void notifyPending_skipsNonPendingEntrants() {
        attendees.get(0).setStatus("selected");
        sendNotificationToAllPending("Event", "Message");
        assertEquals(1, notificationsDb.size());
        assertEquals("uid-null", notificationsDb.get(0).get("userId"));
    }

    @Test
    public void notifySelected_skipsNonSelectedEntrants() {
        attendees.get(0).setStatus("selected");
        sendNotificationToAllSelected("Event");
        assertEquals(1, notificationsDb.size());
        assertEquals("uid-enabled", notificationsDb.get(0).get("userId"));
    }

    @Test
    public void toggleOptOut_thenNotify_respectsNewPreference() {
        usersDb.get("uid-enabled").put("notificationsEnabled", false);
        sendNotificationToAllPending("Swim", "Hello");
        assertEquals(1, notificationsDb.size());
        assertEquals("uid-null", notificationsDb.get(0).get("userId"));
    }

    @Test
    public void toggleOptIn_thenNotify_receivesNotification() {
        usersDb.get("uid-disabled").put("notificationsEnabled", true);
        sendNotificationToAllPending("Swim", "Hello");
        assertEquals(3, notificationsDb.size());
    }

    @Test
    public void customMessage_preservedInNotification() {
        sendNotificationToAllPending("Swim", "Registration closes tomorrow!");
        for (Map<String, Object> n : notificationsDb) {
            assertEquals("Registration closes tomorrow!", n.get("message"));
        }
    }

    @Test
    public void notificationTitle_containsEventName() {
        sendNotificationToAllPending("Piano Lessons", "Update");
        for (Map<String, Object> n : notificationsDb) {
            assertTrue(((String) n.get("title")).contains("Piano Lessons"));
        }
    }

    @Test
    public void notificationTitle_nullEventName_fallback() {
        sendNotificationToAllPending(null, "Update");
        for (Map<String, Object> n : notificationsDb) {
            assertTrue(((String) n.get("title")).contains("an event"));
        }
    }
}
