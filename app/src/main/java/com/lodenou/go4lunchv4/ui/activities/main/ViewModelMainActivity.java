package com.lodenou.go4lunchv4.ui.activities.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public class ViewModelMainActivity extends AndroidViewModel {

    private DetailRepository mDetailRepository;
    private RestaurantRepository mRestaurantRepository;
    private LiveData<List<Restaurant>> mListRestaurantsLiveData;
    private LiveData<User> mUserLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    public ViewModelMainActivity(@NonNull Application application) {
        super(application);
        mRestaurantRepository = new RestaurantRepository(application);

    }


    public void init() {
        mDetailRepository = DetailRepository.getInstance();
        mUserLiveData = mDetailRepository.getUser();
    }
    public LiveData<User> getUser() {
        return mUserLiveData;
    }

    public void fetchAllRestaurants(Task task, Boolean permission, Context context){
         mRestaurantRepository.fetchAllRestaurants(task, permission, context);
    }

    public LiveData<List<Restaurant>> getAllRestaurants(){
        mListRestaurantsLiveData = mRestaurantRepository.getAllRestaurants();
        return mListRestaurantsLiveData;
    }


    public void insertRestaurant(Restaurant restaurant) {
        mRestaurantRepository.insertRestaurant(restaurant);
    }
    public void updateRestaurant(Restaurant restaurant, Boolean isAddition){
        mRestaurantRepository.updateRestaurants(restaurant, isAddition);
    }

    public void deleteAllRestaurants(){
        mRestaurantRepository.deleteAllRestaurants();
    }




    public LiveData<Restaurant> getRestaurantById(String restaurantId){
        mRestaurantLiveData = mRestaurantRepository.getRestaurantById(restaurantId);
        return mRestaurantLiveData;
    }


}
