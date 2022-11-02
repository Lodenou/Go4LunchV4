package com.lodenou.go4lunchv4.model;

import java.util.Date;

public class SelectedRestaurant {

    private String idUser;
    private String idRestaurant;
    private Date date;
    private String restaurantName;
    private String restaurantImage;

    public SelectedRestaurant() {
    }

    public SelectedRestaurant(String IdUser, String idRestaurant, Date date, String restaurantName, String restaurantImage) {
        this.idUser = IdUser;
        this.idRestaurant = idRestaurant;
        this.date = date;
        this.restaurantName = restaurantName;
        this.restaurantImage = restaurantImage;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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


    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }
}
