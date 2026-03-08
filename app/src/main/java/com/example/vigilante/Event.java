package com.example.vigilante;

public class Event {
    private String title;
    private String description;

    private String organizerId;

    public Event(){
    }

    public Event(String title ,String description, String organizerId){
        this.title = title;
        this.description = description;
        this.organizerId = organizerId;

    }

    public  String getTitle() { return title;}
    public String getDescription() {return  description;}

    public  String getOrganizerId() {return  organizerId;}
}
