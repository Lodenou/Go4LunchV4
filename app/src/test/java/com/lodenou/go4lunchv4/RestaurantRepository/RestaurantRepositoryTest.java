package com.lodenou.go4lunchv4.RestaurantRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lodenou.go4lunchv4.data.Go4LunchApi;
import com.lodenou.go4lunchv4.data.room.RestaurantDao;
import com.lodenou.go4lunchv4.data.room.RestaurantRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRoomDatabase;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Location;
import com.lodenou.go4lunchv4.model.nearbysearch.NearbySearchResults;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class RestaurantRepositoryTest {

    @Test
    public void testGetUser() {
        // Configuration des mocks
        UserCallData mockUserCallData = mock(UserCallData.class);
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        String userId = "someUserId";

        // Simuler le comportement des mocks
        when(mockUserCallData.getUser(userId)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        User expectedUser = new User("uid", "userName", "userAvatarUrl",
                "userEmail", "FavoriteRestaurantId",
                "restaurantChosenId", "restaurantChosenName");
        when(mockDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);

        // Initialisation du RestaurantRepository
        Application application = ApplicationProvider.getApplicationContext();
        RestaurantRepository restaurantRepository = new RestaurantRepository(application, mockUserCallData, userId);

        // Appel de la méthode getUser()
        MutableLiveData<User> resultLiveData = restaurantRepository.getUser();

        // Ajout d'un observateur au LiveData pour tester la valeur
        resultLiveData.observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Vérification que l'utilisateur retourné est celui attendu
                assertEquals(expectedUser, user);
            }
        });
    }
}


