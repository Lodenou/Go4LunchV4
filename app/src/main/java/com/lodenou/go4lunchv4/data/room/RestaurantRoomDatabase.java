package com.lodenou.go4lunchv4.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lodenou.go4lunchv4.model.Restaurant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Database class for the RestaurantRoomDatabase.
 * Database annotation specifies the entities and version of the database.
 */
@Database(entities = {Restaurant.class}, version = 2, exportSchema = false)
public abstract class RestaurantRoomDatabase extends RoomDatabase {

    /**
     * Provides access to the RestaurantDao.
     *
     * @return RestaurantDao instance for database operations.
     */
    public abstract RestaurantDao mRestaurantDao();

    private static volatile RestaurantRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Gets an instance of the RestaurantRoomDatabase.
     *
     * @param context The application context.
     * @return RestaurantRoomDatabase instance.
     */
    public static RestaurantRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RestaurantRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RestaurantRoomDatabase.class, "restaurant_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
