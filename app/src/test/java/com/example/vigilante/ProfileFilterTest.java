package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProfileFilterTest {

    private List<Profile> allProfiles;

    private Profile makeProfile(String id, String name, String email, Boolean isOrganizer) {
        Profile p = new Profile();
        p.setId(id);
        p.setName(name);
        p.setEmail(email);
        p.setIsOrganizer(isOrganizer);
        return p;
    }

    private List<Profile> filterByType(String type) {
        List<Profile> filtered = new ArrayList<>();
        for (Profile p : allProfiles) {
            if ("org".equals(type)) {
                if (Boolean.TRUE.equals(p.getIsOrganizer())) {
                    filtered.add(p);
                }
            } else {
                if (!Boolean.TRUE.equals(p.getIsOrganizer())) {
                    filtered.add(p);
                }
            }
        }
        return filtered;
    }

    private List<Profile> searchProfiles(String query) {
        String q = query.toLowerCase().trim();
        List<Profile> filtered = new ArrayList<>();
        for (Profile p : allProfiles) {
            if (q.isEmpty()) {
                filtered.add(p);
                continue;
            }
            boolean match = (p.getName() != null && p.getName().toLowerCase().contains(q)) ||
                    (p.getEmail() != null && p.getEmail().toLowerCase().contains(q)) ||
                    (p.getPhone() != null && p.getPhone().toLowerCase().contains(q));
            if (match) filtered.add(p);
        }
        return filtered;
    }

    @Before
    public void setUp() {
        allProfiles = new ArrayList<>();
        allProfiles.add(makeProfile("uid-1", "Alice Entrant", "alice@test.com", false));
        allProfiles.add(makeProfile("uid-2", "Bob Organizer", "bob@test.com", true));
        allProfiles.add(makeProfile("uid-3", "Charlie Entrant", "charlie@test.com", false));
        allProfiles.add(makeProfile("uid-4", "Diana Organizer", "diana@test.com", true));
        allProfiles.add(makeProfile("uid-5", "Eve NullFlag", "eve@test.com", null));
    }

    @Test
    public void filterOrganizers_returnsOnlyOrganizers() {
        List<Profile> result = filterByType("org");
        assertEquals(2, result.size());
        for (Profile p : result) {
            assertTrue(p.getIsOrganizer());
        }
    }

    @Test
    public void filterEntrants_returnsNonOrganizers() {
        List<Profile> result = filterByType("all");
        assertEquals(3, result.size());
        for (Profile p : result) {
            assertFalse(Boolean.TRUE.equals(p.getIsOrganizer()));
        }
    }

    @Test
    public void nullOrganizerFlag_treatedAsEntrant() {
        List<Profile> entrants = filterByType("all");
        boolean foundNull = false;
        for (Profile p : entrants) {
            if ("uid-5".equals(p.getId())) foundNull = true;
        }
        assertTrue(foundNull);

        List<Profile> organizers = filterByType("org");
        for (Profile p : organizers) {
            assertNotEquals("uid-5", p.getId());
        }
    }

    @Test
    public void searchByName_findsMatch() {
        List<Profile> result = searchProfiles("alice");
        assertEquals(1, result.size());
        assertEquals("uid-1", result.get(0).getId());
    }

    @Test
    public void searchByEmail_findsMatch() {
        List<Profile> result = searchProfiles("bob@test");
        assertEquals(1, result.size());
        assertEquals("uid-2", result.get(0).getId());
    }

    @Test
    public void searchEmpty_returnsAll() {
        List<Profile> result = searchProfiles("");
        assertEquals(5, result.size());
    }

    @Test
    public void searchNoMatch_returnsEmpty() {
        List<Profile> result = searchProfiles("zzz");
        assertEquals(0, result.size());
    }

    @Test
    public void searchIsCaseInsensitive() {
        List<Profile> result = searchProfiles("ALICE");
        assertEquals(1, result.size());
    }
}
