// unit tests for lottery draw logic — correct count, no duplicates, replacement draw and edge cases — US 02.05.01

package com.example.vigilante;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class LotteryDrawTest {

    private List<Entrant> buildPendingEntrants(int count) {
        List<Entrant> pending = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Entrant e = new Entrant();
            e.setId("entrant-" + i);
            e.setName("User " + i);
            e.setEmail("user" + i + "@test.com");
            e.setStatus("pending");
            pending.add(e);
        }
        return pending;
    }

    private List<Entrant> performDraw(List<Entrant> pending, int numToDraw) {
        int actualDraw = Math.min(numToDraw, pending.size());
        Random random = new Random(42);
        List<Entrant> pool = new ArrayList<>(pending);
        List<Entrant> selected = new ArrayList<>();

        while (selected.size() < actualDraw) {
            int index = random.nextInt(pool.size());
            Entrant chosen = pool.remove(index);
            chosen.setStatus("selected");
            selected.add(chosen);
        }
        return selected;
    }

    @Test
    public void draw_selectsCorrectNumber() {
        List<Entrant> pending = buildPendingEntrants(10);
        List<Entrant> selected = performDraw(pending, 3);
        assertEquals(3, selected.size());
    }

    @Test
    public void draw_requestMoreThanAvailable_selectsAll() {
        List<Entrant> pending = buildPendingEntrants(5);
        List<Entrant> selected = performDraw(pending, 10);
        assertEquals(5, selected.size());
    }

    @Test
    public void draw_zeroRequested_selectsNone() {
        List<Entrant> pending = buildPendingEntrants(10);
        List<Entrant> selected = performDraw(pending, 0);
        assertEquals(0, selected.size());
    }

    @Test
    public void draw_fromEmptyList_selectsNone() {
        List<Entrant> pending = buildPendingEntrants(0);
        List<Entrant> selected = performDraw(pending, 5);
        assertEquals(0, selected.size());
    }

    @Test
    public void draw_selectedEntrantsHaveSelectedStatus() {
        List<Entrant> pending = buildPendingEntrants(10);
        List<Entrant> selected = performDraw(pending, 4);
        for (Entrant e : selected) {
            assertEquals("selected", e.getStatus());
        }
    }

    @Test
    public void draw_noDuplicates() {
        List<Entrant> pending = buildPendingEntrants(20);
        List<Entrant> selected = performDraw(pending, 10);
        Set<String> ids = new HashSet<>();
        for (Entrant e : selected) {
            assertTrue("Duplicate entrant: " + e.getId(), ids.add(e.getId()));
        }
    }

    @Test
    public void draw_singleEntrant_selectsOne() {
        List<Entrant> pending = buildPendingEntrants(1);
        List<Entrant> selected = performDraw(pending, 1);
        assertEquals(1, selected.size());
        assertEquals("entrant-0", selected.get(0).getId());
    }

    @Test
    public void replacementDraw_afterDecline_selectsFromRemaining() {
        List<Entrant> pending = buildPendingEntrants(5);
        List<Entrant> firstDraw = performDraw(pending, 2);

        List<Entrant> remaining = new ArrayList<>();
        Set<String> selectedIds = new HashSet<>();
        for (Entrant e : firstDraw) {
            selectedIds.add(e.getId());
        }
        for (Entrant e : buildPendingEntrants(5)) {
            if (!selectedIds.contains(e.getId())) {
                remaining.add(e);
            }
        }

        List<Entrant> replacement = performDraw(remaining, 1);
        assertEquals(1, replacement.size());
        assertFalse(selectedIds.contains(replacement.get(0).getId()));
    }

    @Test
    public void replacementDraw_noRemainingPending_selectsNone() {
        List<Entrant> pending = buildPendingEntrants(3);
        List<Entrant> firstDraw = performDraw(pending, 3);
        assertEquals(3, firstDraw.size());

        List<Entrant> remaining = new ArrayList<>();
        List<Entrant> replacement = performDraw(remaining, 1);
        assertEquals(0, replacement.size());
    }
}
