package com.lodenou.go4lunchv4.DetailRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.detail.Result;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class IsRestaurantChosenTest {

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
    private MutableLiveData<Boolean> dataCurrent;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // FirebaseApp init
        Context context = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:538588114262:android:77c470c115da1cdb67faf7")
                    .setApiKey(BuildConfig.API_KEY)
                    .build();
            FirebaseApp.initializeApp(context, options);
        }

        // Mocks setup FirebaseFirestore
        when(mockFirebaseFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // Mocks setup UserCallData
        Query mockUserCallResult = mock(Query.class);
        when(userCallData.getAllUsers()).thenReturn(mockUserCallResult);
        when(mockUserCallResult.getFirestore()).thenReturn(mockFirebaseFirestore);

        // Configuration pour mockGetTask
        when(mockDocumentReference.get()).thenReturn(mockGetTask);
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDocumentSnapshot);

        // Configurez mockDocumentSnapshot pour retourner les données nécessaires pour votre test
        when(mockDocumentSnapshot.getString("restaurantChosenId")).thenReturn("testRestaurantId");

        // Mocks setup mockSetTask
        Task<Void> mockSetTask = Tasks.forResult(null);
        when(mockDocumentReference.set(anyMap(), any(SetOptions.class))).thenReturn(mockSetTask);


        MutableLiveData<Result> mockDataDetail = new MutableLiveData<>();
        com.lodenou.go4lunchv4.model.detail.Result mockResult = new com.lodenou.go4lunchv4.model.detail.Result();
        mockResult.setName("Test Restaurant");
        mockDataDetail.setValue(mockResult);
        detailRepository = DetailRepository.getInstance(userCallData, "userUid","username", "userPhotoUrl", "UserEmail", mockDataDetail);
    }

    @Test
    public void testIsRestaurantChosen() {
        // Call to the tested method
        detailRepository.isRestaurantChosen("testRestaurantId");

        // Vérification
        verify(mockDocumentReference).get();
    }
}
