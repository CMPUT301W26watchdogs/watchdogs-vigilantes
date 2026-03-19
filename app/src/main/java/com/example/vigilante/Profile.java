// data model for a user profile holding name, email, organizer flag for Firestore deserialization US 01.02.01

package com.example.vigilante;

/**
* This class is what creates a profile for a user.
 */
public class Profile {
    private String name;
    private String email;
    private String organizerId;
    private String id;

    private Boolean isOrganizer;

    public Profile(){
    }
    /**
* This function takes all the necessary arguments required to create a user
 */
    public Profile(String name , String email, String organizerId, String id, Boolean isOrganizer){
        this.name = name;
        this.email = email;
        this.organizerId = organizerId;
        this.id = id;
        this.isOrganizer = isOrganizer;

    }

    /**
* Getter function for name
 */
    public  String getName() { return name;}
    /**
     * Getter function for email
     */
    public String getEmail() {return  email;}
    /**
     * Getter function for organizerid
     */
    public  String getOrganizerId() {return  organizerId;}
    /**
     * Getter function for id
     */
    public String getId() { return id; }
    /**
     * Getter function for bool isorganizer
     */
    public Boolean getIsOrganizer() { return isOrganizer;}
    /**
     * Setter function for id
     */
    public void setId(String id) { this.id = id; }
    /**
     * Setter function for name
     */
    public void setName(String name) { this.name = name; }
    /**
     * Setter function for email
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * Setter function for organizer id
     */
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    /**
     * Setter function for bool isorganzier
     */
    public void setIsOrganizer(Boolean isOrganizer) { this.isOrganizer = isOrganizer;}
}
