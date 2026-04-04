// data model for a user profile — holds name, email, organizer flag for Firestore deserialization — US 01.02.01

package com.example.vigilante;
/**
 * This class is what creates a comment for a user.
 */
public class Comment {
    private String name, commentText, timeStamp, id, userId;

    public Comment(){
    }
    /**
     * This function takes all the necessary arguments required to create a comment
     */
    public Comment(String name , String commentText){
        this.name = name;
        this.commentText = commentText;
    }
    /**
     * Getter function for name
     */
    public  String getName() { return name;}
    /**
     * Getter function for comment
     */
    public String getCommentText() {return  commentText;}
    /**
     * Getter function for timestamp
     */
    public String getTimeStampText() {return  timeStamp;}
    /**
     * Getter function for id
     */
    public String getId() { return id; }
    /**
     * Getter function for userId
     */
    public String getUserId() { return userId; }
    /**
     * Setter function for name
     */
    public void setName(String name) { this.name = name; }
    /**
     * Setter function for timeStamp
     */
    public void setTimeStampText(String timeStamp) { this.timeStamp = timeStamp; }
    /**
     * Setter function for comment
     */
    public void setCommentText(String commentText) { this.commentText = commentText; }
    // Gemini, 2026-04-02, Organizers should be able to delete comments on their own events. Admins should be able to delete comments on any event.
    /**
     * Setter function for id
     */
    public void setId(String id) { this.id = id; }
    /**
     * Setter function for userId
     */
    public void setUserId(String userId) { this.userId = userId; }

}
