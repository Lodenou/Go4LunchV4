package com.lodenou.go4lunchv4.data.room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class RestaurantRepository implements IRestaurantRepository {


    MutableLiveData<Location> dataLocation = new MutableLiveData<>();

    MutableLiveData<List<Restaurant>> dataRestaurants = new MutableLiveData<>();
    private ArrayList<Restaurant> datasetRestaurants = new ArrayList<>();

    MutableLiveData<User> dataUser = new MutableLiveData<>();
    private ArrayList<Result> dataset = new ArrayList<>();

    // Room
    RestaurantRoomDatabase mRestaurantRoomDatabase;
    RestaurantDao mRestaurantDao;
    private LiveData<List<Restaurant>> mListRestaurantLiveData;
    private LiveData<Restaurant> mRestaurantLiveData;

    private UserCallData userCallData;
    private String idUser;

    User mUser;

    /**
     * Constructor for the RestaurantRepository class.
     *
     * @param application  The instance of the Android application.
     * @param userCallData The UserCallData object for user calls.
     * @param idUser       The user's ID.
     */
    public RestaurantRepository(Application application, UserCallData userCallData, String idUser) {
        // Room injection
        RestaurantRoomDatabase mRestaurantRoomDatabase = RestaurantRoomDatabase.getDatabase(application);
        this.mRestaurantDao = mRestaurantRoomDatabase.mRestaurantDao();
        this.mListRestaurantLiveData = mRestaurantDao.getAllRestaurants();
        //  UserCallData injection
        this.userCallData = userCallData;
        this.idUser = idUser;
    }


    /**
     * Gets user information using the user's ID.
     *
     * @return MutableLiveData containing user data.
     */
    public MutableLiveData<User> getUser() {

        userCallData.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mUser = documentSnapshot.toObject(User.class);
                dataUser.setValue(mUser);
            }
        });
        return dataUser;
    }

    // Room

    /**
     * Gets a restaurant by its ID.
     *
     * @param restaurantId The restaurant's ID.
     * @return LiveData containing the corresponding restaurant.
     */
    public LiveData<Restaurant> getRestaurantById(String restaurantId) {
        mRestaurantLiveData = mRestaurantDao.getRestaurantById(restaurantId);
        return mRestaurantLiveData;
    }


    /**
     * Deletes all restaurants from the local database.
     */
    public void deleteAllRestaurants() {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.deleteAll());
    }

    /**
     * Gets the list of restaurants in real-time.
     *
     * @return LiveData containing the list of restaurants.
     */
    public LiveData<List<Restaurant>> getAllRestaurants() {
        return dataRestaurants;
    }

    /**
     * Calculates the number of workmates in a restaurant.
     *
     * @param placeId The Place ID of the restaurant.
     * @param users   The list of users.
     * @return The number of workmates in the restaurant.
     */
    private int getWorkmateNumber(String placeId, List<User> users) {
        int nbWorkmate = 0;

        for (int i = 0; i <= users.size() - 1; i++) {
            if (Objects.equals(placeId, users.get(i).getRestaurantChosenId())) {
                nbWorkmate++;
            }
        }
        return nbWorkmate;
    }

    /**
     * Fetches all restaurants based on location and user permissions.
     *
     * @param task       The Task for location retrieval.
     * @param permission The permission to access location.
     * @param context    The application context.
     */
    public void fetchAllRestaurants(Task task, Boolean permission, Context context) {
        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", 0);
        boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
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
                                    }

                                    @Override
                                    public void onNext(NearbySearchResults nearbySearchResults) {
                                        ArrayList<User> datasetUsers = new ArrayList<>();
                                        dataset.clear();
                                        dataset.addAll(nearbySearchResults.getResults());
                                        userCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                                }
                                            }
                                        });
                                    }
                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }
            });
        } else {
            dataRestaurants.setValue(mRestaurantRoomDatabase.mRestaurantDao().getAllRestaurants().getValue());
        }
    }

    /**
     * Converts a result object to a Restaurant object.
     *
     * @param result The Result object.
     * @param nbUser The number of users.
     * @return The corresponding Restaurant object.
     */
    private Restaurant resultToRestaurant(Result result, Integer nbUser) {

        Double lat = result.getGeometry().getLocation().getLat();
        Double lng = result.getGeometry().getLocation().getLng();
        String geometry = Utils.formatLocation(lat, lng);
        // OpeningHours
        String isOpenNow = Utils.isOpenOrNot(result.getOpeningHours());
        String photo = "";
        if (result.getPhotos() != null) {
            // We can't put url in strings because we need context
//            photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
//                    result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;
            photo = BuildConfig.RESTAURANT_PHOTO_URL +
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


    /**
     * Gets the location in real-time.
     *
     * @param permission The permission to access location.
     * @param task       The Task for location retrieval.
     * @return MutableLiveData containing the location data.
     */
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

