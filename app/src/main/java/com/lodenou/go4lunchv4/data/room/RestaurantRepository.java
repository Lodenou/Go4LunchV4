package com.lodenou.go4lunchv4.data.room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.Go4LunchApi;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.NearbySearchResults;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantRepository {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_IS_FIRST_LAUNCH = "isFirstLaunch";

    MutableLiveData<Location> dataLocation = new MutableLiveData<>();

    MutableLiveData<List<Restaurant>> dataRestaurants = new MutableLiveData<>();
    private ArrayList<Restaurant> datasetRestaurants = new ArrayList<>();

    MutableLiveData<List<Restaurant>> dataRestaurantsUpdate = new MutableLiveData<>();
    private ArrayList<Restaurant> datasetRestaurantsUpdate = new ArrayList<>();

    private ArrayList<Result> dataset = new ArrayList<>();


    // Room
    RestaurantRoomDatabase mRestaurantRoomDatabase;
    RestaurantDao mRestaurantDao;
    private LiveData<List<Restaurant>> mListRestaurantLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    public RestaurantRepository(Application application) {
        //Room
        mRestaurantRoomDatabase = RestaurantRoomDatabase.getDatabase(application);
        mRestaurantDao = mRestaurantRoomDatabase.mRestaurantDao();
        mListRestaurantLiveData = mRestaurantDao.getAllRestaurants();
    }

    // Room
    public void insertRestaurant(Restaurant restaurant) {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.insert(restaurant));
    }

    public void insertAllRestaurants(List<Restaurant> restaurants){
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.insertAll(restaurants));
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
        return dataRestaurants;
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

    public void fetchAllRestaurants(Task task, Boolean permission, Context context) {
        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", 0);
        boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @SuppressLint("CheckResult")
                @Override
                public void onSuccess(Location location) {
                    if (!permission) {
                        return;
                    }
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String loc = lat + "," + lng;

                        Go4LunchApi.retrofit.create(Go4LunchApi.class).getNearbyPlaces(loc)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<NearbySearchResults>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d("TAG", "onSubscribe: ");
                                    }
                                    @Override
                                    public void onNext(NearbySearchResults nearbySearchResults) {
                                        ArrayList<User> datasetUsers = new ArrayList<>();
                                        dataset.clear();
                                        dataset.addAll(nearbySearchResults.getResults());
                                        UserCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        User mUser = document.toObject(User.class);
                                                        datasetUsers.add(mUser);
                                                    }
                                                    for (int i = 0; i <= dataset.size() - 1; i++) {
                                                        Result result = dataset.get(i);
                                                        Restaurant restaurant = resultToRestaurant(result, getWorkmateNumber(result.getPlaceId(), datasetUsers));
                                                        datasetRestaurants.add(restaurant);
                                                    }
                                                    dataRestaurants.setValue(datasetRestaurants);

                                                    RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.deleteAll());
                                                    RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.insertAll(datasetRestaurants));
                                                    Log.d("123", datasetRestaurants.size()+"");
                                                }
                                            }
                                        });
                                        Log.d("TAG", "onNext: ");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("TAG", "error: ");
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("TAG", "onComplete: ");
                                    }
                                });
                    }
                }
            });
        }
        else   {
            dataRestaurants.setValue(mRestaurantRoomDatabase.mRestaurantDao().getAllRestaurants().getValue());
        }
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

