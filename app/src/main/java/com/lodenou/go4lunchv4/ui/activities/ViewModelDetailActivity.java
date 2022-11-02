package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.lodenou.go4lunchv4.data.SelectedRestaurantRepository;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import java.util.List;

public class ViewModelDetailActivity extends ViewModel {


    private MutableLiveData<List<SelectedRestaurant>> mMutableLiveDataRestaurants;
    private MutableLiveData<SelectedRestaurant> mMutableLiveDataRestaurant;

    public void init(){
        if(mMutableLiveDataRestaurants != null && mMutableLiveDataRestaurant != null){
            return;
        }
        SelectedRestaurantRepository selectedRestaurantRepository = SelectedRestaurantRepository.getInstance();
        mMutableLiveDataRestaurants = selectedRestaurantRepository.getSelectedRestaurants();
        mMutableLiveDataRestaurant = selectedRestaurantRepository.getSelectedRestaurant();
    }


    public LiveData<List<SelectedRestaurant>> getSelectedRestaurants(){
        return mMutableLiveDataRestaurants;
    }

    //TODO PEUT ETRE USELESS PEUT ETRE QU IL FAUT LE DETAIL RESTAURANT SUIVANT L ID DU RESTAURANT SELECTIONNE
    public LiveData<SelectedRestaurant> getSelectedRestaurant(){
        return mMutableLiveDataRestaurant;
    }
}
