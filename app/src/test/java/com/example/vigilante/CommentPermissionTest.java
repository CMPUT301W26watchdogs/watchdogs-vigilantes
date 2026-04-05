package com.example.vigilante;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommentPermissionTest {

    private boolean canDeleteComment(String currentUserId, String currentUserEmail,
                                      String eventOrganizerId, String commentUserId) {
        boolean isAdmin = "admin@admin.com".equals(currentUserEmail);
        boolean isOrganizer = currentUserId.equals(eventOrganizerId);
        return isAdmin || isOrganizer;
    }

    @Test
    public void admin_canDeleteAnyComment() {
        boolean result = canDeleteComment("uid-admin", "admin@admin.com", "uid-org", "uid-commenter");
        assertTrue(result);
    }

    @Test
    public void organizer_canDeleteCommentOnOwnEvent() {
        boolean result = canDeleteComment("uid-org", "org@test.com", "uid-org", "uid-commenter");
        assertTrue(result);
    }

    @Test
    public void regularUser_cannotDeleteOthersComment() {
        boolean result = canDeleteComment("uid-user", "user@test.com", "uid-org", "uid-commenter");
        assertFalse(result);
    }

    @Test
    public void regularUser_cannotDeleteEvenOwnComment() {
        boolean result = canDeleteComment("uid-commenter", "user@test.com", "uid-org", "uid-commenter");
        assertFalse(result);
    }

    @Test
    public void admin_canDeleteOnAnyEvent() {
        boolean result1 = canDeleteComment("uid-admin", "admin@admin.com", "uid-org1", "uid-x");
        boolean result2 = canDeleteComment("uid-admin", "admin@admin.com", "uid-org2", "uid-y");
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    public void organizer_cannotDeleteOnOtherOrganizerEvent() {
        boolean result = canDeleteComment("uid-org1", "org1@test.com", "uid-org2", "uid-commenter");
        assertFalse(result);
    }

    @Test
    public void adminCheck_isCaseSensitive() {
        boolean result = canDeleteComment("uid-admin", "Admin@admin.com", "uid-org", "uid-c");
        assertFalse(result);
    }
}
