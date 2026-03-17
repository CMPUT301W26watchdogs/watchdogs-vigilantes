// unit tests for selected entrant notification data construction — US 02.07.02

package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SelectedNotificationTest {

    // building a notification data map for a selected entrant
    private Map<String, Object> buildSelectedNotification(String userId, String eventId, String eventTitle) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("eventId", eventId);
        notification.put("title", "You've been selected!");
        notification.put("message", "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept or decline your invitation.");
        notification.put("read", false);
        return notification;
    }

    @Test
    // verifying selected notification has correct title — US 02.07.02
    public void selectedNotification_hasCorrectTitle() {
        Map<String, Object> notif = buildSelectedNotification("uid-1", "event-1", "Swimming Lessons");
        assertEquals("You've been selected!", notif.get("title"));
    }

    @Test
    // verifying selected notification message includes event title — US 02.07.02
    public void selectedNotification_messageIncludesEventTitle() {
        Map<String, Object> notif = buildSelectedNotification("uid-1", "event-1", "Basketball Tournament");
        assertTrue(((String) notif.get("message")).contains("Basketball Tournament"));
    }

    @Test
    // verifying selected notification with null event title falls back to "an event" — US 02.07.02
    public void selectedNotification_nullTitle_fallback() {
        Map<String, Object> notif = buildSelectedNotification("uid-1", "event-1", null);
        assertTrue(((String) notif.get("message")).contains("an event"));
    }

    @Test
    // verifying selected notification message includes accept/decline instruction — US 02.07.02
    public void selectedNotification_messageIncludesAcceptDecline() {
        Map<String, Object> notif = buildSelectedNotification("uid-1", "event-1", "Art Class");
        assertTrue(((String) notif.get("message")).contains("accept or decline"));
    }

    @Test
    // verifying selected notification defaults to unread — US 02.07.02
    public void selectedNotification_defaultUnread() {
        Map<String, Object> notif = buildSelectedNotification("uid-1", "event-1", "Event");
        assertFalse((Boolean) notif.get("read"));
    }

    @Test
    // verifying notification contains userId and eventId — US 02.07.02
    public void selectedNotification_containsIds() {
        Map<String, Object> notif = buildSelectedNotification("uid-42", "event-99", "Concert");
        assertEquals("uid-42", notif.get("userId"));
        assertEquals("event-99", notif.get("eventId"));
    }

    @Test
    // verifying multiple notifications for different users are independent — US 02.07.02
    public void selectedNotification_multipleUsersIndependent() {
        Map<String, Object> notif1 = buildSelectedNotification("uid-1", "event-1", "Event A");
        Map<String, Object> notif2 = buildSelectedNotification("uid-2", "event-1", "Event A");
        assertNotEquals(notif1.get("userId"), notif2.get("userId"));
        assertEquals(notif1.get("eventId"), notif2.get("eventId"));
    }
}
