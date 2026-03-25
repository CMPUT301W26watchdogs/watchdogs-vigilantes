// testing waiting list notification data construction and opt out logic US 02.07.01

package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class WaitingListNotificationTest {

    // building a notification data map for a waiting list entrant
    private Map<String, Object> buildWaitingNotification(String userId, String eventId, String eventTitle, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("eventId", eventId);
        notification.put("title", "Update for " + (eventTitle != null ? eventTitle : "an event"));
        notification.put("message", message);
        notification.put("read", false);
        return notification;
    }

    // verifying waiting list notification contains all required fields US 02.07.01
    @Test
    public void waitingNotification_containsAllFields() {
        Map<String, Object> notif = buildWaitingNotification("uid-1", "event-1", "Swimming Lessons", "Draw is coming up!");
        assertEquals("uid-1", notif.get("userId"));
        assertEquals("event-1", notif.get("eventId"));
        assertEquals("Update for Swimming Lessons", notif.get("title"));
        assertEquals("Draw is coming up!", notif.get("message"));
        assertEquals(false, notif.get("read"));
    }

    // verifying notification title falls back to an event when event title is null US 02.07.01
    @Test
    public void waitingNotification_nullEventTitle_fallback() {
        Map<String, Object> notif = buildWaitingNotification("uid-1", "event-1", null, "Update message");
        assertEquals("Update for an event", notif.get("title"));
    }

    // verifying custom message is preserved in the notification US 02.07.01
    @Test
    public void waitingNotification_customMessagePreserved() {
        String customMsg = "The lottery draw has been rescheduled to next week.";
        Map<String, Object> notif = buildWaitingNotification("uid-1", "event-1", "Art Class", customMsg);
        assertEquals(customMsg, notif.get("message"));
    }

    // verifying notification defaults to unread US 02.07.01
    @Test
    public void waitingNotification_defaultUnread() {
        Map<String, Object> notif = buildWaitingNotification("uid-1", "event-1", "Event", "msg");
        assertFalse((Boolean) notif.get("read"));
    }

    // verifying opt out check where false blocks sending US 02.07.01
    @Test
    public void optOutCheck_falseBlocksSending() {
        Boolean notificationsEnabled = false;
        boolean shouldSend = !Boolean.FALSE.equals(notificationsEnabled);
        assertFalse(shouldSend);
    }

    // verifying opt in check where true allows sending US 02.07.01
    @Test
    public void optInCheck_trueAllowsSending() {
        Boolean notificationsEnabled = true;
        boolean shouldSend = !Boolean.FALSE.equals(notificationsEnabled);
        assertTrue(shouldSend);
    }

    // verifying null notification preference defaults to enabled US 02.07.01
    @Test
    public void optOutCheck_nullDefaultsToEnabled() {
        Boolean notificationsEnabled = null;
        boolean shouldSend = !Boolean.FALSE.equals(notificationsEnabled);
        assertTrue(shouldSend);
    }
}
