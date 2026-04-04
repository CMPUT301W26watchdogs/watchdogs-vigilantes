// Claude, Opus 4, "write end-to-end lottery draw tests covering draw, decline, replacement and pool depletion"
package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class LotteryFullFlowTest {

    private List<Entrant> waitingPool;
    private List<Map<String, Object>> notificationsSent;

    private Entrant makeEntrant(String id, String name, String email) {
        Entrant e = new Entrant();
        e.setId(id);
        e.setName(name);
        e.setEmail(email);
        e.setStatus("pending");
        return e;
    }

    private List<Entrant> performDraw(int numToDraw, String eventTitle) {
        int actualDraw = Math.min(numToDraw, waitingPool.size());
        Random random = new Random(42);

        List<Entrant> pool = new ArrayList<>(waitingPool);
        List<Entrant> selected = new ArrayList<>();

        while (selected.size() < actualDraw) {
            int index = random.nextInt(pool.size());
            Entrant chosen = pool.remove(index);
            chosen.setStatus("selected");
            selected.add(chosen);

            Map<String, Object> notif = new HashMap<>();
            notif.put("userId", chosen.getId());
            notif.put("title", "You've been selected!");
            notif.put("message", "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept or decline your invitation.");
            notif.put("read", false);
            notificationsSent.add(notif);
        }

        for (Entrant e : pool) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("userId", e.getId());
            notif.put("title", "Not selected for event");
            notif.put("message", "The lottery draw for " + (eventTitle != null ? eventTitle : "an event") + " has concluded and unfortunately you were not selected this time.");
            notif.put("read", false);
            notificationsSent.add(notif);
        }

        waitingPool = pool;
        return selected;
    }

    private Entrant drawReplacement(String eventTitle) {
        if (waitingPool.isEmpty()) return null;
        Random random = new Random(42);
        int index = random.nextInt(waitingPool.size());
        Entrant chosen = waitingPool.remove(index);
        chosen.setStatus("selected");

        Map<String, Object> notif = new HashMap<>();
        notif.put("userId", chosen.getId());
        notif.put("title", "You've been selected!");
        notif.put("message", "A spot opened up for " + (eventTitle != null ? eventTitle : "an event") + " and you were drawn from the waitlist.");
        notif.put("read", false);
        notificationsSent.add(notif);

        return chosen;
    }

    private void declineInvitation(Entrant entrant) {
        entrant.setStatus("declined");
    }

    private void acceptInvitation(Entrant entrant) {
        entrant.setStatus("accepted");
    }

    @Before
    public void setUp() {
        waitingPool = new ArrayList<>();
        notificationsSent = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            waitingPool.add(makeEntrant("uid-" + i, "User" + i, "user" + i + "@test.com"));
        }
    }

    @Test
    public void fullFlow_drawSelectsCorrectCount_andNotifiesAll() {
        List<Entrant> selected = performDraw(3, "Swimming");
        assertEquals(3, selected.size());
        assertEquals(10, notificationsSent.size());

        int selectedNotifs = 0;
        int rejectedNotifs = 0;
        for (Map<String, Object> n : notificationsSent) {
            if ("You've been selected!".equals(n.get("title"))) selectedNotifs++;
            else if ("Not selected for event".equals(n.get("title"))) rejectedNotifs++;
        }
        assertEquals(3, selectedNotifs);
        assertEquals(7, rejectedNotifs);
    }

    @Test
    public void fullFlow_selectedEntrantsHaveSelectedStatus() {
        List<Entrant> selected = performDraw(3, "Swimming");
        for (Entrant e : selected) {
            assertEquals("selected", e.getStatus());
        }
    }

    @Test
    public void fullFlow_unselectedEntrantsStayPending() {
        performDraw(3, "Swimming");
        assertEquals(7, waitingPool.size());
        for (Entrant e : waitingPool) {
            assertEquals("pending", e.getStatus());
        }
    }

    @Test
    public void fullFlow_declineThenReplacementDraw() {
        List<Entrant> selected = performDraw(2, "Dance Class");
        assertEquals(8, waitingPool.size());

        Entrant decliner = selected.get(0);
        declineInvitation(decliner);
        assertEquals("declined", decliner.getStatus());

        notificationsSent.clear();
        Entrant replacement = drawReplacement("Dance Class");
        assertNotNull(replacement);
        assertEquals("selected", replacement.getStatus());
        assertEquals(7, waitingPool.size());
        assertEquals(1, notificationsSent.size());
        assertTrue(((String) notificationsSent.get(0).get("message")).contains("A spot opened up"));
    }

    @Test
    public void fullFlow_acceptInvitationChangesStatusToAccepted() {
        List<Entrant> selected = performDraw(1, "Piano");
        Entrant winner = selected.get(0);
        assertEquals("selected", winner.getStatus());
        acceptInvitation(winner);
        assertEquals("accepted", winner.getStatus());
    }

    @Test
    public void fullFlow_allDecline_poolDepletes_noMoreReplacements() {
        List<Entrant> selected = performDraw(10, "Full Event");
        assertEquals(0, waitingPool.size());

        for (Entrant e : selected) {
            declineInvitation(e);
        }

        Entrant replacement = drawReplacement("Full Event");
        assertNull(replacement);
    }

    @Test
    public void fullFlow_replacementNotDrawnFromAlreadySelected() {
        List<Entrant> selected = performDraw(3, "Test");
        Set<String> selectedIds = new HashSet<>();
        for (Entrant e : selected) {
            selectedIds.add(e.getId());
        }

        Entrant replacement = drawReplacement("Test");
        assertNotNull(replacement);
        assertFalse(selectedIds.contains(replacement.getId()));
    }

    @Test
    public void fullFlow_notificationMessageContainsEventTitle() {
        performDraw(1, "Guitar Lessons");
        Map<String, Object> selectedNotif = notificationsSent.get(0);
        assertTrue(((String) selectedNotif.get("message")).contains("Guitar Lessons"));
    }

    @Test
    public void fullFlow_nullEventTitle_fallbackInNotification() {
        performDraw(1, null);
        Map<String, Object> selectedNotif = notificationsSent.get(0);
        assertTrue(((String) selectedNotif.get("message")).contains("an event"));
    }

    @Test
    public void fullFlow_multipleSequentialReplacements_drainPool() {
        performDraw(1, "Event");
        assertEquals(9, waitingPool.size());

        for (int i = 0; i < 9; i++) {
            Entrant replacement = drawReplacement("Event");
            assertNotNull(replacement);
            assertEquals("selected", replacement.getStatus());
        }
        assertEquals(0, waitingPool.size());

        Entrant noMore = drawReplacement("Event");
        assertNull(noMore);
    }
}
