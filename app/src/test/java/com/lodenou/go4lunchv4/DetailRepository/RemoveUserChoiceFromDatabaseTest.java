package com.lodenou.go4lunchv4.DetailRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.detail.Result;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class RemoveUserChoiceFromDatabaseTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

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
    private UserCallData userCallData;
    private DetailRepository detailRepository;
    @Test
    public void testRemoveUserChoiceFromDatabase() {
        setUpMocksForRemoveUserChoiceFromDatabase();
        detailRepository.removeUserChoiceFromDatabase();
        verify(mockDocumentReference).set(anyMap(), any(SetOptions.class));
    }

    private void setUpMocksForRemoveUserChoiceFromDatabase() {

        MockitoAnnotations.initMocks(this);

        //FirebaseApp initialisation
        Context context = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:538588114262:android:77c470c115da1cdb67faf7")
                    .setApiKey(BuildConfig.API_KEY)
                    .build();
            FirebaseApp.initializeApp(context, options);
        }

        // Mocks setup FirebaseAuth / FirebaseUser
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("userUid");

        // Mocks setup FirebaseFirestore
        when(mockFirebaseFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // Mocks setup UserCallData
        Query mockUserCallResult = mock(Query.class);
        when(userCallData.getAllUsers()).thenReturn(mockUserCallResult);
        when(mockUserCallResult.getFirestore()).thenReturn(mockFirebaseFirestore);

        // Mocks setup DocumentReference
        Map<String, Object> emptyChoice = new HashMap<>();
        emptyChoice.put("restaurantChosenId", "");
        emptyChoice.put("restaurantChosenName", "");
        Task<Void> mockSetTask = Tasks.forResult(null);
        when(mockDocumentReference.set(eq(emptyChoice), any(SetOptions.class))).thenReturn(mockSetTask);

        MutableLiveData<Result> mockDataDetail = new MutableLiveData<>();
        detailRepository = DetailRepository.getInstance(userCallData, "userUid","username",
                "userPhotoUrl", "UserEmail", mockDataDetail);
    }
}
