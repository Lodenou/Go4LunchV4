package com.lodenou.go4lunchv4.model;

import java.util.Date;
import java.util.List;

public class SelectedRestaurant {

    private List<User> users;
    private String idRestaurant;
    private Date date;


    public SelectedRestaurant() {
    }

    public SelectedRestaurant(List<User> users, String idRestaurant, Date date) {
        this.users = users;
        this.idRestaurant = idRestaurant;
        this.date = date;
    }


    public List<User> getIdUser() {
        return users;
    }

    public void setIdUser(List<User> users) {
        this.users = users;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    }

