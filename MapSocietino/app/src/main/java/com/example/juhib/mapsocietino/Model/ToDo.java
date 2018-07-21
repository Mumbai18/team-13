package com.example.juhib.mapsocietino.Model;

public class ToDo {
    private String id, name,contact, type;

    public ToDo() {
    }

    public ToDo(String id, String name, String contact, String type) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setPhone(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    /* public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }*/
}
