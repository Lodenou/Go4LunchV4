package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;

import java.util.List;

public class ViewModelListView extends AndroidViewModel {

//TODO USELESS SEE IF THE DELETE IS POSSIBLE

    private MutableLiveData<List<Result>> mMutableLiveData;
    private MutableLiveData<Location> mMutableLiveDataLocation;

    private RestaurantRepository mRestaurantRepository;

    public ViewModelListView(@NonNull Application application) {
        super(application);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRestaurantRepository = new RestaurantRepository(application, userCallData,idUser);
    }


    public void init() {
        if (mMutableLiveData != null) {
            return;
        }
    }
}
