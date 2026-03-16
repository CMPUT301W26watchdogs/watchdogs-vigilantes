package com.example.vigilante;

import java.io.Serializable;

public class Event implements Serializable {

    private String posterUrl;
    private String id;
    private String currentUser;
    private String title;
    private String description;
    private String date;
    private String location;
    private String capacity;
    private String price;
    private String registrationStart;
    private String registrationEnd;
    private String organizerId;
    private String category;

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

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getDate() { return date; }

    public String getLocation() { return location; }

    public String getCapacity() { return capacity; }

    public String getPrice() { return price; }

    public String getRegistrationStart() { return registrationStart; }

    public String getRegistrationEnd() { return registrationEnd; }

    public String getOrganizerId() { return organizerId; }

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

    public void setCategory(String category) { this.category = category; }

    public  void   setcurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
