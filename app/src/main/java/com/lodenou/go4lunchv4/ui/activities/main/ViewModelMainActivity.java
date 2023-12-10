package com.lodenou.go4lunchv4.ui.activities.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public class ViewModelMainActivity extends AndroidViewModel {

    private DetailRepository mDetailRepository;
    private RestaurantRepository mRestaurantRepository;
    private LiveData<List<Restaurant>> mListRestaurantsLiveData;
    private LiveData<User> mUserLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    public ViewModelMainActivity(@NonNull Application application) {
        super(application);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRestaurantRepository = new RestaurantRepository(application, userCallData, idUser);
    }


    public void init() {

    }
    public LiveData<User> getUser() {
        mUserLiveData = mRestaurantRepository.getUser();
        return mUserLiveData;
    }

    public void fetchAllRestaurants(Task task, Boolean permission, Context context){
         mRestaurantRepository.fetchAllRestaurants(task, permission, context);
    }

    public LiveData<List<Restaurant>> getAllRestaurants(){
        mListRestaurantsLiveData = mRestaurantRepository.getAllRestaurants();
        return mListRestaurantsLiveData;
    }


    public void deleteAllRestaurants(){
        mRestaurantRepository.deleteAllRestaurants();
    }




    public LiveData<Restaurant> getRestaurantById(String restaurantId){
        mRestaurantLiveData = mRestaurantRepository.getRestaurantById(restaurantId);
        return mRestaurantLiveData;
    }


}
