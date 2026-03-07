package com.example.vigilante;

import java.io.Serializable;

public class Entrant implements Serializable {

    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final String status;

    public Entrant(String id, String name, String email, String phone, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
}
