package com.lodenou.go4lunchv4.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurant_table")
public class Restaurant {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String placeId;
    private String name;
    private String geometry;
    private String openingHours;
    private String photo;
    private Double rating;
    private String vicinity;
    private Integer restaurantUserNumber;


    public Restaurant(@NonNull String placeId, String name, String geometry,
                      String openingHours, String photo, Double rating, String vicinity, Integer restaurantUserNumber ){
        this.placeId = placeId;
        this.name = name;
        this.geometry = geometry;
        this.openingHours = openingHours;
        this.photo = photo;
        this.rating = rating;
        this.vicinity = vicinity;
        this.restaurantUserNumber = restaurantUserNumber;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photos) {
        this.photo = photo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Integer getRestaurantUserNumber() {
        return restaurantUserNumber;
    }

    public void setRestaurantUserNumber(Integer restaurantUserNumber) {
        this.restaurantUserNumber = restaurantUserNumber;
    }
}
