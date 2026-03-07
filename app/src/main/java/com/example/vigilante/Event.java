package com.example.vigilante;

import java.io.Serializable;

public class Event implements Serializable {

    private final String id;
    private final String title;
    private final String description;
    private final String date;
    private final String location;
    private final String capacity;
    private final String price;
    private final String registrationStart;
    private final String registrationEnd;

    public Event(String id, String title, String description, String date,
                 String location, String capacity, String price,
                 String registrationStart, String registrationEnd) {
        this.id = id;
        this.title = title;
        this.description = description;
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
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getCapacity() { return capacity; }
    public String getPrice() { return price; }
    public String getRegistrationStart() { return registrationStart; }
    public String getRegistrationEnd() { return registrationEnd; }
}
