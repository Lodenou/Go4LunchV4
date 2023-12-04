package com.lodenou.go4lunchv4;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import androidx.lifecycle.LiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.data.user.UserRepository;
import com.lodenou.go4lunchv4.model.User;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserRepositoryTest {




    @Test
    public void testGetUsers() {
        UserCallData mockUserCallData = mock(UserCallData.class);
        CollectionReference mockCollectionRef = mock(CollectionReference.class);
        Task<QuerySnapshot> mockTask = mock(Task.class);
        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);

        when(mockUserCallData.getUsersCollection()).thenReturn(mockCollectionRef);
        when(mockCollectionRef.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);

        List<User> mockUsers = Collections.singletonList(new User("uid",  "userName",  "userAvatarUrl", "userEmail"
                ,  "FavoriteRestaurantId",  "restaurantChosenId",  "restaurantChosenName"));
        when(mockSnapshot.toObjects(User.class)).thenReturn(mockUsers);

        UserRepository userRepository = new UserRepository(mockUserCallData);

        LiveData<List<User>> result = userRepository.getUsers();
        assertNotNull(result);
    }


    @Test
    public void testGetRestaurantChosenId() {
        UserCallData mockUserCallData = mock(UserCallData.class);
        Query mockQuery = mock(Query.class);
        Task<QuerySnapshot> mockTask = mock(Task.class);
        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);

        when(mockUserCallData.getAllUsers()).thenReturn(mockQuery);
        when(mockQuery.whereNotEqualTo("restaurantChosenId", "")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);

        List<User> mockUsers = Arrays.asList(
                new User( "uid",  "userName",  "userAvatarUrl", "userEmail"
                        ,  "FavoriteRestaurantId",  "restaurantChosenId",  "restaurantChosenName"),
                new User("uidfddf",  "userNamdffdsdfse",  "userAfdsdfsfdvatarUrl", "usfdsfdserEmail"
                        ,  "FavoriteRestdffdsaurantId",  "restaurantChfdsfsdosenId",  "restadfsdfsurantChosenName")
        );
        when(mockSnapshot.toObjects(User.class)).thenReturn(mockUsers);

        UserRepository userRepository = new UserRepository(mockUserCallData);


        LiveData<List<String>> result = userRepository.getRestaurantChosenId();
        assertNotNull(result);
    }
}


