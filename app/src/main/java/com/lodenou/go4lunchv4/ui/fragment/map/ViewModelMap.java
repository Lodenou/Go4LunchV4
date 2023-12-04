package com.lodenou.go4lunchv4.ui.fragment.map;
import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.data.user.UserRepository;

import java.util.List;

public class ViewModelMap extends AndroidViewModel {


    private MutableLiveData<List<String>> mMutableLiveDataRestaurantChosenId;
    private MutableLiveData<Location> mMutableLiveDataLocation;
    private RestaurantRepository mRestaurantRepository;


    public ViewModelMap(@NonNull Application application) {
        super(application);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        mRestaurantRepository = new RestaurantRepository(application, userCallData);
    }


    public void init(String location){
        //TODO DELETE
    }


    public LiveData<List<String>> getRestaurantChosenId() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        UserRepository mUserRepository = new UserRepository(userCallData);
        mMutableLiveDataRestaurantChosenId = mUserRepository.getRestaurantChosenId();
        return mMutableLiveDataRestaurantChosenId;
    }

    public LiveData<Location> getLocation(Boolean permission, Task task) {
//        mRestaurantRepository = RestaurantRepository.getInstance();
        mMutableLiveDataLocation = mRestaurantRepository.getLocation(permission, task);
        return mMutableLiveDataLocation;
    }

}
