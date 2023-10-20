package com.lodenou.go4lunchv4.data.detail;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.Result;

import java.util.List;

public interface IDetailRepository {

    MutableLiveData<Result> getRestaurantDetails(String restaurantId);

    FirebaseUser getCurrentUser();

    MutableLiveData<User> getUser();

    MutableLiveData<List<User>> getUsersEatingHere(String restaurantId);

    void addUserChoiceToDatabase(String restaurantId);

    void removeUserChoiceFromDatabase();

    MutableLiveData<Boolean> isCurrentUserHasChosenThisRestaurant(String restaurantId);

    void updateUserList();

    void isRestaurantChosen(String restaurantId);

    void addUserFavoriteToDatabase(String restaurantId);

    void removeUserFavoriteFromDatabase();

    MutableLiveData<Boolean> isRestaurantEqualToUserFavorite(String restaurantId);

    MutableLiveData<List<String>> getListOfColleaguesWhoEatWithCurrentUser(String restaurantId);
}
