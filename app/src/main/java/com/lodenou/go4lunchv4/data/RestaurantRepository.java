package com.lodenou.go4lunchv4.data;


import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.DetailResult;
import com.lodenou.go4lunchv4.model.nearbysearch.NearbySearchResults;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.adapters.ListViewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private ArrayList<Result> dataset = new ArrayList<>();
    private com.lodenou.go4lunchv4.model.detail.Result mRestaurant;
    MutableLiveData<List<Result>> dataNearby = new MutableLiveData<>();
    MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> dataDetail =
            new MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result>();
    MutableLiveData<Location> dataLocation = new MutableLiveData<>();
    MutableLiveData<List<Result>> dataResult = new MutableLiveData<>();
    private ArrayList<Result> datasetResult = new ArrayList<>();

    public static RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Result>> getNearbyRestaurants(String location, Boolean isInit) {
        if (isInit) {
            Go4LunchApi.retrofit.create(Go4LunchApi.class).getNearbyPlaces(location)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<NearbySearchResults>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d("TAG", "onSubscribe: ");
                        }

                        @Override
                        public void onNext(NearbySearchResults nearbySearchResults) {
                            dataset.clear();
                            dataset.addAll(nearbySearchResults.getResults());
                            dataNearby.setValue(dataset);
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
        return dataNearby;
    }

    public MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> getRestaurantDetails(String restaurantId) {
        Go4LunchApi.retrofit.create(Go4LunchApi.class).getPlaceDetails(restaurantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DetailResult detailResult) {
                        mRestaurant = detailResult.getResult();
                        dataDetail.setValue(mRestaurant);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return dataDetail;
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

    // LIST VIEW PURPOSE
    public MutableLiveData<List<Result>> getRestaurants(Boolean permission, Task task) {

        // get restaurants & get the number of workmates who selected a restaurant
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
                    List<Result> results = RestaurantRepository.getInstance().getNearbyRestaurants(loc, true).getValue();
                    UserCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Result> datasetResult = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User mUser = document.toObject(User.class);
                                    for (Result restaurant : results) {
                                        if (mUser.getRestaurantChosenId() != null && mUser.getRestaurantChosenId()
                                                .equals(restaurant.getPlaceId())) {
                                            //the boolean avoid the increment +1 on a null var
                                            boolean found = false;
                                            for (Result r : datasetResult) {
                                                if (r.getPlaceId().equals(restaurant.getPlaceId())) {
                                                    r.setRestaurantUserNumber(r.getRestaurantUserNumber() + 1);
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (!found) {
                                                restaurant.setRestaurantUserNumber(1);
                                                datasetResult.add(restaurant);
                                            }
                                            break;
                                        }
                                    }
                                }
                                // add restaurants with getRestaurantNumber = 0 to the list
                                for (Result restaurant : results) {
                                    boolean found = false;
                                    for (Result r : datasetResult) {
                                        if (r.getPlaceId().equals(restaurant.getPlaceId())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        datasetResult.add(restaurant);
                                    }
                                }
                                dataResult.postValue(datasetResult);
                            }
                        }
                    });
                }
            }
        });
        return dataResult;
    }
}
