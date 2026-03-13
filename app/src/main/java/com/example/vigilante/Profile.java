package com.example.vigilante;

public class Profile {
    private String name;
    private String email;
    private String organizerId;
    private String id;

    private Boolean isOrganizer;

    public Profile(){
    }

    public Profile(String name , String email, String organizerId, String id, Boolean isOrganizer){
        this.name = name;
        this.email = email;
        this.organizerId = organizerId;
        this.id = id;
        this.isOrganizer = isOrganizer;

    }

    public  String getName() { return name;}
    public String getEmail() {return  email;}
    public  String getOrganizerId() {return  organizerId;}

    public String getId() { return id; }
    public Boolean getIsOrganizer() { return isOrganizer;}

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public void setIsOrganizer(Boolean isOrganizer) { this.isOrganizer = isOrganizer;}


}
