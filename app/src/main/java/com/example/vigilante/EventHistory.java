package com.example.vigilante;

public class EventHistory {
    private String eventTitle;
    private String status;
    public EventHistory(){}
    public EventHistory(String eventTitle, String status){
        this.eventTitle = eventTitle;
        this.status = status;
    }
    public String getEventTitle(){
        return eventTitle;
    }
    public String getStatus(){
        return status;
    }
}