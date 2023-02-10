package com.lodenou.go4lunchv4.ui.activities.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.data.DetailRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRoomRepository;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelMainActivity extends AndroidViewModel {

    private DetailRepository mDetailRepository;
    private RestaurantRoomRepository mRestaurantRoomRepository;
    private LiveData<List<Restaurant>> mListRestaurantsLiveData;
    private MutableLiveData<List<Restaurant>> mListMutableLiveDataRestaurantsApi;
    private MutableLiveData<List<Restaurant>> mListMutableLiveDataRestaurantsUpdate;
    private LiveData<User> mUserLiveData;
    private LiveData<List<Result>> mListLiveData;
    private MutableLiveData<Integer> mIntegerLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    public ViewModelMainActivity(@NonNull Application application) {
        super(application);
        mRestaurantRoomRepository = new RestaurantRoomRepository(application);
        mListRestaurantsLiveData = mRestaurantRoomRepository.getAllRestaurants();
    }


    public void init() {
        mDetailRepository = DetailRepository.getInstance();
        mUserLiveData = mDetailRepository.getUser();
    }
    public LiveData<User> getUser() {
        return mUserLiveData;
    }

    public LiveData<List<Result>> getNearbyRestaurants(Task task, Boolean permission){
        mListLiveData = mDetailRepository.getNearbyRestaurants(task, permission);
        return mListLiveData;
    }

    public LiveData<List<Restaurant>> getAllRestaurantsFromVm() {
        return mListRestaurantsLiveData;
    }

    public void insertRestaurant(Restaurant restaurant) {
        mRestaurantRoomRepository.insertRestaurant(restaurant);
    }
    public void updateRestaurant(Restaurant restaurant, Boolean isAddition){
        mRestaurantRoomRepository.updateRestaurants(restaurant, isAddition);
    }


    public void deleteAllRestaurants(){
        mRestaurantRoomRepository.deleteAllRestaurants();
    }

//    public MutableLiveData<Integer> getWorkmateNumber(Result result){
//       mIntegerLiveData = mRestaurantRoomRepository.getWorkmateNumber(result);
//        return mIntegerLiveData;
//    }

    public MutableLiveData<List<Restaurant>> getAllRestaurantsFromApi(List<Result> results){
     mListMutableLiveDataRestaurantsApi =   mRestaurantRoomRepository.getAllRestaurantsFromApi(results );
        return mListMutableLiveDataRestaurantsApi;
    }

    public LiveData<Restaurant> getRestaurantById(String restaurantId){
        mRestaurantLiveData = mRestaurantRoomRepository.getRestaurantById(restaurantId);
        return mRestaurantLiveData;
    }

//    public MutableLiveData<List<Restaurant>> getAllRestaurantsForUpdate(List<Restaurant> restaurants){
//        mListMutableLiveDataRestaurantsUpdate = mRestaurantRoomRepository.getAllRestaurantsForUpdate(restaurants);
//        return mListMutableLiveDataRestaurantsUpdate;
//    }


}
