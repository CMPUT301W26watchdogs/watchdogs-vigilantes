// model class holding all event fields

package com.example.vigilante;

import java.io.Serializable;

// serializable so an Event object can be passed between activities via Intent.putExtra()
// also has no-arg constructor + setters so Firestore can deserialize it automatically
/**
 * This class is used to create a Event object which is used by other classes such as addEvent and AllEventActivity to grab data
 */
public class Event implements Serializable {

    // fields are NOT final so Firestore can set them during deserialization
    private String posterUrl;    // URL for the event poster image
    private String id;           // unique event ID
    private String currentUser;  // current user interacting with the event
    private String title;        // event name
    private String description;  // full event description
    private String date;         // event date
    private String location;     // event location
    private String capacity;     // max number of attendees
    private String price;        // event price
    private String registrationStart;  // registration start date
    private String registrationEnd;    // registration end date
    private String organizerId;  // ID of the event organizer
    private String category;     // event category

    private Boolean isPrivate;

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

    // getters
    /**
     * Getter for id
     */
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
     * Getter for currentUser
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
     * Getter for registrationStart
     */
    public String getRegistrationStart() { return registrationStart; }

    /**
     * Getter for registrationEnd
     */
    public String getRegistrationEnd() { return registrationEnd; }

    /**
     * Getter for organizerId
     */
    public String getOrganizerId() { return organizerId; }

    /**
     * Getter for posterUrl
     */
    public  String getPosterUrl() {return  posterUrl;}

    public void setPosterUrl(String posterUrl){ this.posterUrl =posterUrl;}

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setDate(String date) { this.date = date; }

    public void setLocation(String location) { this.location = location; }

    public void setCapacity(String capacity) { this.capacity = capacity; }

    public void setPrice(String price) { this.price = price; }

    public void setRegistrationStart(String registrationStart) { this.registrationStart = registrationStart; }

    public void setRegistrationEnd(String registrationEnd) { this.registrationEnd = registrationEnd; }

    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getCategory() { return category; }

    public Boolean getIsPrivate() {

        return  isPrivate;
    }

    public void setCategory(String category) { this.category = category; }

    public  void   setcurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
