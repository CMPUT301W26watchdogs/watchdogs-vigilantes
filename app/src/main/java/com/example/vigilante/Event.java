package com.example.vigilante;

public class Event {
    private String title;
    private String description;

    private String organizerId;

    private String posterUrl;

    private String id;

    public Event(){
    }

    public Event(String title ,String description, String organizerId, String posterUrl, String id){
        this.title = title;
        this.description = description;
        this.organizerId = organizerId;
        this.posterUrl = posterUrl;
        this.id = id;

    }

    public  String getTitle() { return title;}
    public String getDescription() {return  description;}

    public  String getOrganizerId() {return  organizerId;}
    public  String getPosterUrl() {return  posterUrl;}
    public void setPosterUrl(String posterUrl){ this.posterUrl =posterUrl;}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
