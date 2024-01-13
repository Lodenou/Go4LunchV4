package com.lodenou.go4lunchv4.ConnexionRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.connexion.ConnexionRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class ConnexionRepositoryTest {

    @Mock
    private FirebaseAuth mockFirebaseAuth;
    @Mock
    private AuthCredential mockGoogleAuthCredential;
    @Mock
    private Task<AuthResult> mockAuthResultTask;
    @Mock
    private FirebaseUser mockFirebaseUser;
    private ConnexionRepository connexionRepository;
    @Mock
    private UserCallData mUserCallData;
    @Before
    public void setUp() {

    }

    @Test
    public void testFirebaseSignInWithGoogle() {
        MockitoAnnotations.initMocks(this);
        connexionRepository = new ConnexionRepository(mUserCallData, mockFirebaseAuth);

        when(mockFirebaseAuth.signInWithCredential(mockGoogleAuthCredential)).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.isSuccessful()).thenReturn(true);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        // Set up user
        when(mockFirebaseUser.getUid()).thenReturn("uid");
        when(mockFirebaseUser.getDisplayName()).thenReturn("name");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(Uri.parse("https://photo.url"));
        when(mockFirebaseUser.getEmail()).thenReturn("email@example.com");

        MutableLiveData<User> result = connexionRepository.firebaseSignInWithGoogle(mockGoogleAuthCredential);

        // Simulate onComplete
        when(mockAuthResultTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            listener.onComplete(mockAuthResultTask);
            return null;
        });

        result.observeForever(user -> {
            assertNotNull(user);
            assertEquals("uid", user.getUid());
            assertEquals("name", user.getUserName());
            assertEquals("https://photo.url", user.getUserAvatarUrl());
            assertEquals("email@example.com", user.getUserEmail());
        });


        verify(mockFirebaseAuth).signInWithCredential(mockGoogleAuthCredential);
        verify(mockAuthResultTask).addOnCompleteListener(any());
    }

    @Test
    public void testCreateUserInFirestoreIfNotExists() {
        // Mocks init directly here to avoid problem
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);
        UserCallData mockUserCallData = mock(UserCallData.class);
        Task<DocumentSnapshot> mockDocumentSnapshotTask = mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        Task<Void> mockVoidTask = mock(Task.class);

        // set up firebase auth
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("uid");
        when(mockFirebaseUser.getDisplayName()).thenReturn("username");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(Uri.parse("https://photo.url"));
        when(mockFirebaseUser.getEmail()).thenReturn("email@example.com");

        // Set up usercalldata
        when(mockUserCallData.getUser(anyString())).thenReturn(mockDocumentSnapshotTask);
        when(mockDocumentSnapshotTask.isSuccessful()).thenReturn(true);
        when(mockDocumentSnapshotTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.toObject(User.class)).thenReturn(null); // Simuler qu'aucun utilisateur n'est trouvé

        when(mockUserCallData.createUser(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(mockVoidTask);

        // Set up callbacks
        when(mockDocumentSnapshotTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockDocumentSnapshot);
            return null;
        });
        when(mockVoidTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Firestore error"));
            return null;
        });

        ConnexionRepository connexionRepository = new ConnexionRepository(mockUserCallData, mockFirebaseAuth);
        connexionRepository.createUserInFirestoreIfNotExists();

        verify(mockUserCallData).getUser("uid");
        verify(mockUserCallData).createUser(eq("uid"), eq("username"),
                eq("https://photo.url"), eq("email@example.com"), eq(" "),
                eq(""), eq(""));
    }
}
