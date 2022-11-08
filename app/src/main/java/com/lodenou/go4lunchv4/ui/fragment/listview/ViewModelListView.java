package com.lodenou.go4lunchv4.ui.fragment.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelListView extends ViewModel {

    private MutableLiveData<List<Result>> mMutableLiveData;
    private RestaurantRepository mRestaurantRepository;

    public void init(String location){
        if(mMutableLiveData != null){
            return;
        }
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveData = mRestaurantRepository.getNearbyRestaurants(location);
    }

    public LiveData<List<Result>> getNearbyRestaurants(){
        return mMutableLiveData;
    }
}
