package com.lodenou.go4lunchv4.model;

public class Restaurant {


    private String restaurantId;
    private String restaurantAddress;
    private String restaurantName;
    private String restaurantImageUrl;


    public Restaurant(){
    }

    public Restaurant( String restaurantId,String restaurantAddress, String restaurantName, String restaurantImageUrl){
        this.restaurantId = restaurantId;
        this.restaurantAddress = restaurantAddress;
        this.restaurantName = restaurantName;
        this.restaurantImageUrl = restaurantImageUrl;
    }


    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantImageUrl() {
        return restaurantImageUrl;
    }

    public void setRestaurantImageUrl(String restaurantImageUrl) {
        this.restaurantImageUrl = restaurantImageUrl;
    }
}
