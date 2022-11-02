package com.lodenou.go4lunchv4.data;

import androidx.lifecycle.MutableLiveData;

import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public class SelectedRestaurantRepository {

    private static SelectedRestaurantRepository instance;
    MutableLiveData<List<SelectedRestaurant>> mSelectedRestaurants = new MutableLiveData<>();
    MutableLiveData<SelectedRestaurant> mSelectedRestaurant = new MutableLiveData<>();

    public static SelectedRestaurantRepository getInstance(){
        if (instance == null) {
            instance = new SelectedRestaurantRepository();
        }
        return instance;
    }

    public MutableLiveData<List<SelectedRestaurant>> getSelectedRestaurants(){
        return mSelectedRestaurants;
    }

    public MutableLiveData<SelectedRestaurant> getSelectedRestaurant(){
        return mSelectedRestaurant;
    }
}
