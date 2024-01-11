package com.lodenou.go4lunchv4.data.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Message;
import com.lodenou.go4lunchv4.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ChatRepository implements IChatRepository {
    private MutableLiveData<List<Message>> dataMessages = new MutableLiveData<>();
    private ArrayList<Message> mDatasetMessages = new ArrayList<>();
    MutableLiveData<User> dataUser = new MutableLiveData<>();
    User mUser;
    Message message;



    private UserCallData userCallData;

    // Constructor for injection
    public ChatRepository(UserCallData userCallData) {
        this.userCallData = userCallData;
    }

    public MutableLiveData<List<Message>> getAllMessageForChat() {

        ChatCallData.getAllMessages().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mDatasetMessages.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        message = document.toObject(Message.class);
                        if (message.getMessage() != null) {
                            mDatasetMessages.add(message);
                        }
                    }
                        dataMessages.setValue(mDatasetMessages);
                }
            }
        });
        return dataMessages;
    }

    public MutableLiveData<User> getUser() {
        String userId = Objects.requireNonNull(getCurrentUser()).getUid();
        userCallData.getUser(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    mUser = document.toObject(User.class);
                    dataUser.setValue(mUser);
                }
            }
        });
        return dataUser;
    }


public void createNewMessage(String message){
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Date date = calendar.getTime();

    String userId = Objects.requireNonNull(getCurrentUser()).getUid();
    userCallData.getUser(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                mUser = document.toObject(User.class);
                dataUser.setValue(mUser);
                ChatCallData.getMessagesCollection().add(new Message(message,mUser, date)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        getAllMessageForChat();
                        Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                });
            }
        }
    });
}

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
