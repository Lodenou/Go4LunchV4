package com.lodenou.go4lunchv4.DetailRepository;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.detail.Result;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class RemoveUserFavoriteToDatabaseTest {

    @Mock
    private UserCallData userCallData;
    @Mock
    private FirebaseAuth mockFirebaseAuth;
    @Mock
    private FirebaseUser mockFirebaseUser;
    @Mock
    private FirebaseFirestore mockFirebaseFirestore;
    @Mock
    private CollectionReference mockCollectionReference;
    @Mock
    private DocumentReference mockDocumentReference;
    @Mock
    private Task<DocumentSnapshot> mockGetTask;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    private DetailRepository detailRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup FirebaseAuth & FirebaseUser
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("currentUserUid");

        // SetupFirebaseFirestore & UserCallData
        when(mockFirebaseFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // setup DocumentReference & Task<DocumentSnapshot>
        Query mockUserCallResult = mock(Query.class);
        when(userCallData.getAllUsers()).thenReturn(mockUserCallResult);
        when(mockUserCallResult.getFirestore()).thenReturn(mockFirebaseFirestore);



        // setup mockDocumentSnapshot
        when(mockDocumentSnapshot.getString("restaurantChosenId")).thenReturn("testRestaurantId");

        // Mocks setup mockSetTask
        when(mockDocumentReference.get()).thenReturn(mockGetTask);
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDocumentSnapshot);
        Task<Void> mockSetTask = Tasks.forResult(null);
        when(mockDocumentReference.set(anyMap(), ArgumentMatchers.any(SetOptions.class))).thenReturn(mockSetTask);


        // DetailRepository setup
        MutableLiveData<Result> mockDataDetail = new MutableLiveData<>();
        com.lodenou.go4lunchv4.model.detail.Result mockResult = new com.lodenou.go4lunchv4.model.detail.Result();
        mockResult.setName("Test Restaurant");
        mockDataDetail.setValue(mockResult);
        detailRepository = DetailRepository.getInstance(userCallData, "currentUserUid", "username", "userPhotoUrl", "UserEmail", mockDataDetail);
    }

    @Test
    public void RemoveUserFavoriteToDatabase_Success() {
        // Act
        detailRepository.addUserFavoriteToDatabase("favoriteRestaurantId");

        // Assert
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("favoritesRestaurant", "favoriteRestaurantId");
        verify(mockDocumentReference).set(expectedData, SetOptions.merge());
    }
}
