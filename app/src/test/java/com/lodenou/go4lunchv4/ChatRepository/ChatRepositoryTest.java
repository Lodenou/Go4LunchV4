package com.lodenou.go4lunchv4.ChatRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.data.chat.ChatCallData;
import com.lodenou.go4lunchv4.data.chat.ChatRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.data.user.UserRepository;
import com.lodenou.go4lunchv4.model.Message;
import com.lodenou.go4lunchv4.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class ChatRepositoryTest {

    @Mock
    private UserCallData userCallData;
    @Mock
    private FirebaseFirestore mockFirebaseFirestore;
    @Mock
    private CollectionReference mockCollectionReference;
    @Mock
    private Task<QuerySnapshot> mockTask;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    @Mock
    private Task<DocumentSnapshot> mockTaskDs;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private Query mockQuery;
    private ChatRepository chatRepository;



    @Test
    public void testGetAllMessageForChat() {
        // Set up
        MockitoAnnotations.initMocks(this);

        // Mocks setup FirebaseFirestore
        when(mockFirebaseFirestore.collection(anyString())).thenReturn(mockCollectionReference);

        // set up the query mock
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockQuery.getFirestore()).thenReturn(mockFirebaseFirestore);

        // Simulate ChatCallData.getAllMessages()
        ChatCallData mockChatCallData = mock(ChatCallData.class);
        when(mockChatCallData.getAllMessages()).thenReturn(mockQuery);

        // set up the task mock
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);

        chatRepository = new ChatRepository(userCallData, mockChatCallData, "sdqdsqqsd");


        MutableLiveData<List<Message>> result = chatRepository.getAllMessageForChat();
        assertNotNull(result);
        verify(mockQuery).get();
    }

    @Test
    public void testGetUser() {
        MockitoAnnotations.initMocks(this);
        String userId = "someUserId";
        ChatCallData mockChatCallData = mock(ChatCallData.class);
        chatRepository = new ChatRepository(userCallData, mockChatCallData, userId );

        // Mock the behavior
        when(userCallData.getUser(userId)).thenReturn(mockTaskDs);
        when(mockTaskDs.isSuccessful()).thenReturn(true);
        when(mockTaskDs.getResult()).thenReturn(mockDocumentSnapshot);
        User expectedUser = new User("uid", "userName", "userAvatarUrl",
                "userEmail", "FavoriteRestaurantId",
                "restaurantChosenId", "restaurantChosenName");
        when(mockDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);

        MutableLiveData<User> resultLiveData = chatRepository.getUser();

        // Add an observer to the LiveData to test the value
        resultLiveData.observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Verify that the returned user is as expected
                assertEquals(expectedUser, user);
            }
        });
    }

    @Test
    public void testCreateNewMessage() {
        MockitoAnnotations.initMocks(this);

        // Mocks for UserCallData and DocumentSnapshot for getUser method
        Task<DocumentSnapshot> mockUserTask = mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockUserTask.isSuccessful()).thenReturn(true);
        when(mockUserTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.toObject(User.class)).thenReturn(new User());
        when(userCallData.getUser("userId")).thenReturn(mockUserTask);

        // Mocks for ChatCallData and CollectionReference for createNewMessage
        ChatCallData mockChatCallData = mock(ChatCallData.class);
        CollectionReference mockCollectionReference = mock(CollectionReference.class);
        Task<DocumentReference> mockAddMessageTask = mock(Task.class);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);
        when(mockChatCallData.getMessagesCollection()).thenReturn(mockCollectionReference);
        when(mockCollectionReference.add(any(Message.class))).thenReturn(mockAddMessageTask);

        // Simulate triggering onComplete callback for getUser
        when(mockUserTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockUserTask);
            return null;
        });

        // Configure mocks to simulate adding a message and triggering onSuccess callback
        when(mockAddMessageTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentReference> listener = invocation.getArgument(0);
            listener.onSuccess(mockDocumentReference);
            return null;
        });

        // Mocks for getAllMessageForChat because we call it inside the method
        FirebaseFirestore mockFirebaseFirestore = mock(FirebaseFirestore.class);
        Query mockQuery = mock(Query.class);
        Task<QuerySnapshot> mockTask = mock(Task.class);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockFirebaseFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockQuery.getFirestore()).thenReturn(mockFirebaseFirestore);
        when(mockChatCallData.getAllMessages()).thenReturn(mockQuery);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);


        ChatRepository chatRepository = new ChatRepository(userCallData, mockChatCallData, "userId");
        String testMessage = "New message";
        chatRepository.createNewMessage(testMessage);

        // Verify that the add method was called on mockCollectionReference
        verify(mockCollectionReference).add(any(Message.class));

        // Verify that getAllMessageForChat is correctly called
        verify(mockQuery).get();
    }
}
