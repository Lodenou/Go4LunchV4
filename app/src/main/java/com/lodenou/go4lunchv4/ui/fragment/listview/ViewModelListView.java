package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelListView extends AndroidViewModel {

//TODO USELESS SEE IF THE DELETE IS POSSIBLE

    private MutableLiveData<List<Result>> mMutableLiveData;
    private MutableLiveData<Location> mMutableLiveDataLocation;

    private RestaurantRepository mRestaurantRepository;

    public ViewModelListView(@NonNull Application application) {
        super(application);
        mRestaurantRepository = new RestaurantRepository(application);

    }


    public void init() {
        if (mMutableLiveData != null) {
            return;
        }
    }
}
