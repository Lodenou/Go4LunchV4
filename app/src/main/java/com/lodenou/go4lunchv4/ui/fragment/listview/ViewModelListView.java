package com.lodenou.go4lunchv4.ui.fragment.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.RestaurantRepository;
import com.lodenou.go4lunchv4.model.Restaurant;

import java.util.List;

public class ViewModelListView extends ViewModel {

    private MutableLiveData<List<Restaurant>> mMutableLiveData;
    private RestaurantRepository mRestaurantRepository;

    public void init(){
        if(mMutableLiveData != null){
            return;
        }
        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveData = mRestaurantRepository.getRestaurants();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return mMutableLiveData;
    }
}
