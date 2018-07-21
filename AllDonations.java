package com.ksapps.shelfshare;

public class AllDonations
{

    public String description;
    public String days;
    public String landmark;
    public String pick_up;
    public String username;

    AllDonations(){}

    public AllDonations(String description, String days, String landmark, String pick_up, String username) {
        this.description = description;
        this.days = days;
        this.landmark = landmark;
        this.pick_up = pick_up;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPick_up() {
        return pick_up;
    }

    public void setPick_up(String pick_up) {
        this.pick_up = pick_up;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
