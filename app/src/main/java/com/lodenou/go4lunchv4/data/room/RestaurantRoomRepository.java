package com.lodenou.go4lunchv4.data.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lodenou.go4lunchv4.model.Restaurant;

import java.util.List;

public class RestaurantRoomRepository {

//    private RestaurantDao mRestaurantDao;
//    private LiveData<List<Restaurant>> mListLiveData;
//
//    // Note that in order to unit test the WordRepository, you have to remove the Application
//    // dependency. This adds complexity and much more code, and this sample is not about testing.
//
//     public RestaurantRoomRepository(Application application) {
//        RestaurantRoomDatabase db = RestaurantRoomDatabase.getDatabase(application);
//        mRestaurantDao = db.mRestaurantDao();
//        mListLiveData = mRestaurantDao.getAllRestaurants();
//    }
//
//    // Room executes all queries on a separate thread.
//    // Observed LiveData will notify the observer when the data has changed.
//    LiveData<List<Restaurant>> getAllRestaurants() {
//        return mListLiveData;
//    }
//
//    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
//    // that you're not doing any long running operations on the main thread, blocking the UI.
//    void insert(Restaurant restaurant) {
//        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> {
//            mRestaurantDao.insert(restaurant);
//        });
//    }

    RestaurantRoomDatabase mRestaurantRoomDatabase;
    RestaurantDao mRestaurantDao;
    private LiveData<List<Restaurant>> mListRestaurantLiveData;

        public RestaurantRoomRepository(Application application) {
        mRestaurantRoomDatabase = RestaurantRoomDatabase.getDatabase(application);
        mRestaurantDao = mRestaurantRoomDatabase.mRestaurantDao();
        mListRestaurantLiveData = mRestaurantDao.getAllRestaurants();
    }

    public void insertRestaurant(Restaurant restaurant) {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.insert(restaurant));
    }

    public void deleteAllRestaurants(){
            RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> mRestaurantDao.deleteAll());
    }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        return mListRestaurantLiveData;
    }
}

