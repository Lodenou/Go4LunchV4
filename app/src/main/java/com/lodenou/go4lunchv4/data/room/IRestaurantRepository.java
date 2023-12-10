package com.lodenou.go4lunchv4.data.room;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public interface IRestaurantRepository {

    LiveData<Restaurant> getRestaurantById(String restaurantId);

    void deleteAllRestaurants();

    LiveData<List<Restaurant>> getAllRestaurants();

    void fetchAllRestaurants(Task task, Boolean permission, Context context);

    MutableLiveData<Location> getLocation(Boolean permission, Task task);
}
