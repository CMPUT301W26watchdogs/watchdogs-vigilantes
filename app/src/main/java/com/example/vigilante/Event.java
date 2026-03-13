// model class holding all event fields
package com.example.vigilante;

import java.io.Serializable;

// serializable so an Event object can be passed between activities via Intent.putExtra()
// also has no-arg constructor + setters so Firestore can deserialize it automatically
/**
* This class is used to create a Event object which is used by other classes such as addEvent and AllEventActivity to grab data
 */
public class Event implements Serializable {

    private String posterUrl;
    // fields are NOT final so Firestore can set them during deserialization
    private String id;               // unique event ID (UUID placeholder for Firestore doc ID)

    private String currentUser;
    private String title;            // event name
    private String description;      // full event description
    private String date;             // date/time string
    private String location;         // venue name or address
    private String capacity;         // max number of attendees
    private String price;            // cost to attend
    private String registrationStart; // when sign-ups open
    private String registrationEnd;   // when sign-ups close
    private String organizerId;       // UID of the organizer who created this event (from Firebase Auth)

    // no-arg constructor required by Firestore for automatic deserialization
    public Event() {}
    /**
* This functions sets all the parameter required and optional needed to create and event
 */
    public Event(String id, String title, String description, String date,
                 String location, String capacity, String price,
                 String registrationStart, String registrationEnd, String posterUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.organizerId = organizerId;
        this.posterUrl = posterUrl;
        this.id = id;

        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
    }
    /**
     * Getter for id
     */
    // getters
    public String getId() { return id; }
    /**
     * Getter for title
     */
    public String getTitle() { return title; }
    /**
     * Getter for description
     */
    public String getDescription() { return description; }
    /**
     * Getter for currentuser
     */
    public String getCurrentUser() {
        return currentUser;
    }
    /**
     * Getter for date
     */
    public String getDate() { return date; }
    /**
     * Getter for location
     */
    public String getLocation() { return location; }
    /**
     * Getter for capacity
     */
    public String getCapacity() { return capacity; }
    /**
     * Getter for price
     */
    public String getPrice() { return price; }
    /**
     * Getter for registration start date
     */
    public String getRegistrationStart() { return registrationStart; }
    /**
     * Getter for registration end date
     */
    public String getRegistrationEnd() { return registrationEnd; }
    /**
     * Getter for organizerid
     */
    public String getOrganizerId() { return organizerId; }
    /**
     * Getter for poster url
     */
    public  String getPosterUrl() {return  posterUrl;}
    // setters needed by Firestore deserialization

    /**
     * Setter for posterurl
     */
    public void setPosterUrl(String posterUrl){ this.posterUrl =posterUrl;}
    /**
     * Setter for id
     */
    public void setId(String id) { this.id = id; }
    /**
     * Setter for title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Setter for description
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * Setter for date
     */
    public void setDate(String date) { this.date = date; }
    /**
     * Setter for location
     */
    public void setLocation(String location) { this.location = location; }
    /**
     * Setter for capacity
     */
    public void setCapacity(String capacity) { this.capacity = capacity; }
    /**
     * Setter for price
     */
    public void setPrice(String price) { this.price = price; }
    /**
     * Setter for registration start date
     */
    public void setRegistrationStart(String registrationStart) { this.registrationStart = registrationStart; }
    /**
     * Setter for registration end date
     */
    public void setRegistrationEnd(String registrationEnd) { this.registrationEnd = registrationEnd; }
    /**
     * Setter for organizerId
     */
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    /**
     * Setter for currentuser
     */
    public  void   setcurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
