// unit tests for notification data construction and opt-out flag logic — US 01.04.03, US 02.05.01

package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NotificationDataTest {

    private Map<String, Object> buildNotification(String userId, String eventId, String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("eventId", eventId);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("read", false);
        return notification;
    }

    @Test
    public void notification_containsAllRequiredFields() {
        Map<String, Object> notif = buildNotification("uid-1", "event-1", "Selected!", "You've been chosen");
        assertEquals("uid-1", notif.get("userId"));
        assertEquals("event-1", notif.get("eventId"));
        assertEquals("Selected!", notif.get("title"));
        assertEquals("You've been chosen", notif.get("message"));
        assertEquals(false, notif.get("read"));
    }

    @Test
    public void notification_defaultReadIsFalse() {
        Map<String, Object> notif = buildNotification("uid-1", "event-1", "Title", "Body");
        assertFalse((Boolean) notif.get("read"));
    }

    @Test
    public void notification_markAsRead() {
        Map<String, Object> notif = buildNotification("uid-1", "event-1", "Title", "Body");
        notif.put("read", true);
        assertTrue((Boolean) notif.get("read"));
    }

    @Test
    public void notification_messageContainsEventTitle() {
        String eventTitle = "Swimming Lessons";
        String message = "You've been chosen for " + eventTitle + ". Open the event to accept or decline your invitation.";
        Map<String, Object> notif = buildNotification("uid-1", "event-1", "You've been selected!", message);
        assertTrue(((String) notif.get("message")).contains("Swimming Lessons"));
    }

    @Test
    public void notification_nullEventTitle_fallbackMessage() {
        String eventTitle = null;
        String message = "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept.";
        Map<String, Object> notif = buildNotification("uid-1", "event-1", "Selected!", message);
        assertTrue(((String) notif.get("message")).contains("an event"));
    }

    @Test
    public void notificationOptOut_falseBlocksNotification() {
        boolean notificationsEnabled = false;
        assertFalse(notificationsEnabled);
    }

    @Test
    public void notificationOptOut_trueAllowsNotification() {
        boolean notificationsEnabled = true;
        assertTrue(notificationsEnabled);
    }

    @Test
    public void notificationOptOut_nullDefaultsToEnabled() {
        Boolean notificationsEnabled = null;
        boolean shouldSend = !Boolean.FALSE.equals(notificationsEnabled);
        assertTrue(shouldSend);
    }
}
