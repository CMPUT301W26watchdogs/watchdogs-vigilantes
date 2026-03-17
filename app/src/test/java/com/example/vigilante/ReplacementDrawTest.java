// unit tests for replacement draw logic — selecting from remaining pool after cancellation — US 02.05.03

package com.example.vigilante;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class ReplacementDrawTest {

    // creating a list of pending entrants for the waiting pool
    private List<Entrant> buildPendingPool(int count) {
        List<Entrant> pool = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Entrant e = new Entrant();
            e.setId("entrant-" + i);
            e.setName("User " + i);
            e.setEmail("user" + i + "@test.com");
            e.setStatus("pending");
            pool.add(e);
        }
        return pool;
    }

    // simulating a single replacement draw from the pending pool
    private Entrant drawOneReplacement(List<Entrant> pendingPool) {
        if (pendingPool.isEmpty()) return null;
        int randomIndex = new Random(42).nextInt(pendingPool.size());
        Entrant chosen = pendingPool.remove(randomIndex);
        chosen.setStatus("selected");
        return chosen;
    }

    @Test
    // verifying a single replacement is drawn from the pending pool — US 02.05.03
    public void replacementDraw_selectsOneFromPool() {
        List<Entrant> pool = buildPendingPool(5);
        Entrant replacement = drawOneReplacement(pool);
        assertNotNull(replacement);
        assertEquals("selected", replacement.getStatus());
        assertEquals(4, pool.size());
    }

    @Test
    // verifying replacement draw returns null when pool is empty — US 02.05.03
    public void replacementDraw_emptyPool_returnsNull() {
        List<Entrant> pool = buildPendingPool(0);
        Entrant replacement = drawOneReplacement(pool);
        assertNull(replacement);
    }

    @Test
    // verifying replacement is not a duplicate of already selected entrants — US 02.05.03
    public void replacementDraw_notInAlreadySelectedSet() {
        List<Entrant> pool = buildPendingPool(10);
        Set<String> alreadySelected = new HashSet<>();
        alreadySelected.add("entrant-0");
        alreadySelected.add("entrant-1");

        List<Entrant> filteredPool = new ArrayList<>();
        for (Entrant e : pool) {
            if (!alreadySelected.contains(e.getId())) {
                filteredPool.add(e);
            }
        }

        Entrant replacement = drawOneReplacement(filteredPool);
        assertNotNull(replacement);
        assertFalse(alreadySelected.contains(replacement.getId()));
    }

    @Test
    // verifying replacement draw with single entrant in pool works — US 02.05.03
    public void replacementDraw_singleEntrantPool_selectsIt() {
        List<Entrant> pool = buildPendingPool(1);
        Entrant replacement = drawOneReplacement(pool);
        assertNotNull(replacement);
        assertEquals("entrant-0", replacement.getId());
        assertEquals("selected", replacement.getStatus());
        assertTrue(pool.isEmpty());
    }

    @Test
    // verifying multiple sequential replacement draws deplete the pool correctly — US 02.05.03
    public void replacementDraw_multipleDraws_depletesPool() {
        List<Entrant> pool = buildPendingPool(3);
        Set<String> drawnIds = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            Entrant r = drawOneReplacement(pool);
            assertNotNull(r);
            assertTrue(drawnIds.add(r.getId()));
        }

        assertTrue(pool.isEmpty());
        assertNull(drawOneReplacement(pool));
    }

    @Test
    // verifying replacement draw changes status from pending to selected — US 02.05.03
    public void replacementDraw_changesStatusToSelected() {
        List<Entrant> pool = buildPendingPool(5);
        for (Entrant e : pool) {
            assertEquals("pending", e.getStatus());
        }
        Entrant replacement = drawOneReplacement(pool);
        assertEquals("selected", replacement.getStatus());
    }
}
