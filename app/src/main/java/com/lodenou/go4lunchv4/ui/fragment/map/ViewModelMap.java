package com.lodenou.go4lunchv4.ui.fragment.map;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.data.SelectedRestaurantRepository;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelMap extends ViewModel {

    private MutableLiveData<List<Result>> mMutableLiveDataNearby;
    private MutableLiveData<List<SelectedRestaurant>> mMutableLiveDataSelected;
    private RestaurantRepository mRestaurantRepository;
    private SelectedRestaurantRepository mSelectedRestaurantRepository;

    public void init(String location){

        // Nearby
        if(mMutableLiveDataNearby != null){
            return;
        }
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveDataNearby = mRestaurantRepository.getNearbyRestaurants(location, true);

        // SelectedRestaurant
        if (mMutableLiveDataSelected != null){
            return;
        }
        mSelectedRestaurantRepository = SelectedRestaurantRepository.getInstance();
        mMutableLiveDataSelected = mSelectedRestaurantRepository.getSelectedRestaurants();

    }

    public LiveData<List<Result>> getNearbyRestaurants(){
        return mMutableLiveDataNearby;
    }

    public LiveData<List<SelectedRestaurant>> getSelectedRestaurantInfo(){
        return mMutableLiveDataSelected;
    }
}
