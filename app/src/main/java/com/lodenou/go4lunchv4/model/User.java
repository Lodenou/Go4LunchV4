package com.lodenou.go4lunchv4.model;

import java.util.List;

public class User {

    private String uid;
    private String userName;
    private String userAvatarUrl;
    private String FavoritesRestaurantId;
    private String restaurantChosen;


    public User() {
    }

    public User(String uid, String userName, String userAvatarUrl, String FavoriteRestaurantId, String restaurantChosen) {
        this.uid = uid;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.FavoritesRestaurantId = FavoriteRestaurantId;
        this.restaurantChosen = restaurantChosen;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getFavoritesRestaurant() {
        return FavoritesRestaurantId;
    }

    public void setFavoritesRestaurant(String favoriteRestaurantId) {
        FavoritesRestaurantId = favoriteRestaurantId;
    }
    public String getRestaurantChosen() {
        return restaurantChosen;
    }

    public void setRestaurantChosen(String restaurantChosen) {
        this.restaurantChosen = restaurantChosen;
    }
}
