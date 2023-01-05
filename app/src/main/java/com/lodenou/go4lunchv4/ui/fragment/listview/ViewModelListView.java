package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelListView extends ViewModel {

    private MutableLiveData<List<Result>> mMutableLiveData;
    private MutableLiveData<Location> mMutableLiveDataLocation;
    private RestaurantRepository mRestaurantRepository;
    private MutableLiveData<List<Result>> mIntegerMutableLiveData;
    private MutableLiveData<Boolean> dataLoaded;


    public void init(Boolean permission, Task task) {
        if (mMutableLiveData != null) {
            return;
        }
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveData = mRestaurantRepository.getNearbyRestaurants("0,0", false);
        mMutableLiveDataLocation = mRestaurantRepository.getLocation(permission, task);

    }

    public LiveData<List<Result>> getNearbyRestaurants() {
        return mMutableLiveData;
    }

    public LiveData<Location> getLocation() {
        return mMutableLiveDataLocation;
    }

    public void fetchNearbyRestaurants(String location) {
        mMutableLiveData = mRestaurantRepository.getNearbyRestaurants(location, true);
    }

    public LiveData<List<Result>> getRestaurants(Boolean permission, Task task){
        mIntegerMutableLiveData = mRestaurantRepository.getRestaurants(permission, task);
        return mIntegerMutableLiveData;
    }
}
