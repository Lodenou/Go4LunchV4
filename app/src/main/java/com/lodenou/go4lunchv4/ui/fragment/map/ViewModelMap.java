package com.lodenou.go4lunchv4.ui.fragment.map;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelMap extends ViewModel {

    private MutableLiveData<List<Result>> mMutableLiveDataNearby;
    private MutableLiveData<List<String>> mMutableLiveDataRestaurantChosenId;
    private MutableLiveData<Location> mMutableLiveDataLocation;
    private RestaurantRepository mRestaurantRepository;
    private UserRepository mUserRepository;


    public void init(String location){

        // Nearby
        if(mMutableLiveDataNearby != null){
            return;
        }
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveDataNearby = mRestaurantRepository.getNearbyRestaurants(location, true);

    }

    public LiveData<List<Result>> getNearbyRestaurants(){
        return mMutableLiveDataNearby;
    }

    public LiveData<List<String>> getRestaurantChosenId() {
        mUserRepository = UserRepository.getInstance();
        mMutableLiveDataRestaurantChosenId = mUserRepository.getRestaurantChosenId();
        return mMutableLiveDataRestaurantChosenId;
    }

    public LiveData<Location> getLocation(Boolean permission, Task task) {
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveDataLocation = mRestaurantRepository.getLocation(permission, task);
        return mMutableLiveDataLocation;
    }

}
