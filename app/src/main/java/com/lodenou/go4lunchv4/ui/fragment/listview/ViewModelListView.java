package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRoomRepository;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelListView extends AndroidViewModel {

    //FIXME USELESS ATM VERIFY IF DELETE IS POSSIBLE

    private MutableLiveData<List<Result>> mMutableLiveData;
    private MutableLiveData<Location> mMutableLiveDataLocation;

    private RestaurantRoomRepository mRestaurantRoomRepository;

    public ViewModelListView(@NonNull Application application) {
        super(application);
        mRestaurantRoomRepository = new RestaurantRoomRepository(application);

    }


    public void init() {
        if (mMutableLiveData != null) {
            return;
        }
//        mRestaurantRepository = RestaurantRepository.getInstance();

//        mMutableLiveData = mRestaurantRepository.getNearbyRestaurants("0,0", false);
//        mMutableLiveDataLocation = mRestaurantRepository.getLocation(permission, task);

    }

//    public LiveData<List<Result>> getNearbyRestaurants() {
//        return mMutableLiveData;
//    }

//    public LiveData<Location> getLocation(Boolean permission, Task task) {
//        mMutableLiveDataLocation = mRestaurantRoomRepository.getLocation(permission, task);
//        return mMutableLiveDataLocation;
//    }

//    public void fetchNearbyRestaurants(String location) {
//        mMutableLiveData = mRestaurantRepository.getNearbyRestaurants(location, true);
//    }

//    public LiveData<List<Result>> getRestaurants(Boolean permission, Task task){
//        mIntegerMutableLiveData = mRestaurantRepository.getRestaurants(permission, task);
//        return mIntegerMutableLiveData;
//    }
}
