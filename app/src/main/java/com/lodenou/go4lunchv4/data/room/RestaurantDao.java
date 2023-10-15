package com.lodenou.go4lunchv4.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.lodenou.go4lunchv4.model.Restaurant;
import java.util.List;


@Dao
public interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Restaurant restaurant);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Restaurant> restaurants);

    @Query("DELETE FROM restaurant_table")
    void deleteAll();

    @Query("SELECT * FROM restaurant_table")
    LiveData<List<Restaurant>> getAllRestaurants();

    @Query("SELECT * FROM restaurant_table WHERE placeId = :placeId")
    LiveData<Restaurant> getRestaurantById(String placeId);

    //    @Update
    @Query("UPDATE restaurant_table SET restaurantUserNumber=:restaurantUserNumber WHERE placeId = :placeId")
    void updateRestaurant(int restaurantUserNumber, String placeId);

}
