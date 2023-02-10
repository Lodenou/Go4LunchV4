package com.lodenou.go4lunchv4.ui.fragment.map;
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
import com.lodenou.go4lunchv4.data.user.UserRepository;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelMap extends AndroidViewModel {

//    private MutableLiveData<List<Result>> mMutableLiveDataNearby;
    private MutableLiveData<List<String>> mMutableLiveDataRestaurantChosenId;
    private MutableLiveData<Location> mMutableLiveDataLocation;
    private RestaurantRepository mRestaurantRepository;
    private UserRepository mUserRepository;
    private RestaurantRoomRepository mRestaurantRoomRepository;

    public ViewModelMap(@NonNull Application application) {
        super(application);
        mRestaurantRoomRepository = new RestaurantRoomRepository(application);
    }


    public void init(String location){

        // Nearby
//        if(mMutableLiveDataNearby != null){
//            return;
//        }
//        mRestaurantRepository = RestaurantRepository.getInstance();
//        mMutableLiveDataNearby = mRestaurantRepository.getNearbyRestaurants(location, true);

    }


    public LiveData<List<String>> getRestaurantChosenId() {
        mUserRepository = UserRepository.getInstance();
        mMutableLiveDataRestaurantChosenId = mUserRepository.getRestaurantChosenId();
        return mMutableLiveDataRestaurantChosenId;
    }

    public LiveData<Location> getLocation(Boolean permission, Task task) {
//        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveDataLocation = mRestaurantRoomRepository.getLocation(permission, task);
        return mMutableLiveDataLocation;
    }

}
