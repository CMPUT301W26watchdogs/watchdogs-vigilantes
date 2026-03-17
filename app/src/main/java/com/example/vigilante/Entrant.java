// data model for a waiting list entrant — holds name, email, phone and lottery status for Firestore deserialization

package com.example.vigilante;

import java.io.Serializable;

// Serializable so an Entrant object can be passed between activities via Intent.putExtra()
// also has no-arg constructor + setters so Firestore can deserialize it automatically
public class Entrant implements Serializable {

    // fields are NOT final so Firestore can set them during deserialization
    private String id;     // unique entrant ID (placeholder for Firestore doc ID)
    private String name;   // full name
    private String email;  // contact email
    private String phone;  // contact phone number
    private String status; // current lottery status

    // no-arg constructor required by Firestore for automatic deserialization
    public Entrant() {}

    public Entrant(String id, String name, String email, String phone, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }

    // setters needed by Firestore deserialization
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(String status) { this.status = status; }
}
