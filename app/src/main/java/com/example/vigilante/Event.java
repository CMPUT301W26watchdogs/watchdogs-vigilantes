// model class holding all event fields
package com.example.vigilante;

import java.io.Serializable;

// serializable so an Event object can be passed between activities via Intent.putExtra()
// also has no-arg constructor + setters so Firestore can deserialize it automatically
public class Event implements Serializable {

    private String posterUrl;
    // fields are NOT final so Firestore can set them during deserialization
    private String id;               // unique event ID (UUID placeholder for Firestore doc ID)
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
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getCapacity() { return capacity; }
    public String getPrice() { return price; }
    public String getRegistrationStart() { return registrationStart; }
    public String getRegistrationEnd() { return registrationEnd; }
    public String getOrganizerId() { return organizerId; }
    public  String getPosterUrl() {return  posterUrl;}
    // setters needed by Firestore deserialization
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
}
