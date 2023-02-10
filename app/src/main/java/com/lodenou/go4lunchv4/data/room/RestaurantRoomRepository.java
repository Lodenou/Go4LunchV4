package com.lodenou.go4lunchv4.data.room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantRoomRepository {

    MutableLiveData<Location> dataLocation = new MutableLiveData<>();

    MutableLiveData<List<Restaurant>> dataRestaurants = new MutableLiveData<>();
    private ArrayList<Restaurant> datasetRestaurants = new ArrayList<>();

    MutableLiveData<List<Restaurant>> dataRestaurantsUpdate = new MutableLiveData<>();
    private ArrayList<Restaurant> datasetRestaurantsUpdate = new ArrayList<>();

    MutableLiveData<List<Result>> dataNearby = new MutableLiveData<>();
    private ArrayList<Result> dataset = new ArrayList<>();

    // Room
    RestaurantRoomDatabase mRestaurantRoomDatabase;
    RestaurantDao mRestaurantDao;
    private LiveData<List<Restaurant>> mListRestaurantLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    public RestaurantRoomRepository(Application application) {
        //Room
        mRestaurantRoomDatabase = RestaurantRoomDatabase.getDatabase(application);
        mRestaurantDao = mRestaurantRoomDatabase.mRestaurantDao();
        mListRestaurantLiveData = mRestaurantDao.getAllRestaurants();
    }

    // Room
    public void insertRestaurant(Restaurant restaurant) {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.insert(restaurant));
    }

    public void updateRestaurants(Restaurant restaurant, Boolean isAddition){

        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> {
            int newUserNumber = 0;
            if (isAddition) {
                newUserNumber = restaurant.getRestaurantUserNumber() + 1;
            }
            else {
                newUserNumber = restaurant.getRestaurantUserNumber() - 1;
            }
            mRestaurantDao.updateRestaurant(
                newUserNumber, restaurant.getPlaceId());});
    }


    public LiveData<Restaurant> getRestaurantById(String restaurantId){
      mRestaurantLiveData =  mRestaurantDao.getRestaurantById(restaurantId);
      return mRestaurantLiveData;
    }


    public void deleteAllRestaurants() {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.deleteAll());
    }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        return mListRestaurantLiveData;
    }

    private int getWorkmateNumber(String placeId, List<User> users) {
        int nbWorkmate = 0;

        for(int i =0; i <= users.size() -1; i++) {
            if (Objects.equals(placeId, users.get(i).getRestaurantChosenId())){
                nbWorkmate++;
            }
        }
        return nbWorkmate;
    }

    public MutableLiveData<List<Restaurant>> getAllRestaurantsFromApi(List<Result> results) {
        ArrayList<User> datasetUsers = new ArrayList<>();
        UserCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User mUser = document.toObject(User.class);
                        datasetUsers.add(mUser);
                    }
                    for (int i = 0; i <= results.size() - 1; i++) {
                        Result result = results.get(i);
                        Restaurant restaurant = resultToRestaurant(result, getWorkmateNumber(result.getPlaceId(),datasetUsers));
                        datasetRestaurants.add(restaurant);
                    }
                    dataRestaurants.setValue(datasetRestaurants);
                }
            }
        });
        return dataRestaurants;
    }

    public MutableLiveData<List<Restaurant>> getAllRestaurantsForUpdate(List<Restaurant> restaurants){
        ArrayList<User> datasetUsersUpdate = new ArrayList<>();
        UserCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User mUser = document.toObject(User.class);
                        datasetUsersUpdate.add(mUser);
                    }
                    for (int i = 0; i <= restaurants.size() - 1; i++) {
                        Restaurant restaurant = restaurants.get(i);
                        restaurant.setRestaurantUserNumber(getWorkmateNumber(restaurant.getPlaceId(),datasetUsersUpdate));
                        datasetRestaurantsUpdate.add(restaurant);
                    }
                    dataRestaurantsUpdate.setValue(datasetRestaurantsUpdate);
                }
            }
        });
        return dataRestaurantsUpdate;
    }

    private Restaurant resultToRestaurant(Result result, Integer nbUser) {

        Double lat = result.getGeometry().getLocation().getLat();
        Double lng = result.getGeometry().getLocation().getLng();
        String geometry = Utils.formatLocation(lat, lng);
        // OpeningHours
        String isOpenNow = Utils.isOpenOrNot(result.getOpeningHours());
        String photo = "";
        if (result.getPhotos() != null) {
            photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
                    result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;
        }
        return new Restaurant(
                result.getPlaceId(),
                result.getName(),
                geometry,
                isOpenNow,
                photo,
                result.getRating(),
                result.getVicinity(),
                nbUser);
    }

    public MutableLiveData<Location> getLocation(Boolean permission, Task task) {

        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @SuppressLint("CheckResult")
            @Override
            public void onSuccess(Location location) {
                if (!permission) {
                    return;
                }
                if (location != null) {
                    dataLocation.setValue(location);
                }
            }
        });
        return dataLocation;
    }
}

