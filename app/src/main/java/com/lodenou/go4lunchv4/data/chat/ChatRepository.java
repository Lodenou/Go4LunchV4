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
/**
 * This class implements the ChatRepository interface and provides methods for managing chat messages and users.
 */
public class ChatRepository implements IChatRepository {
    private MutableLiveData<List<Message>> dataMessages = new MutableLiveData<>();
    private ArrayList<Message> mDatasetMessages = new ArrayList<>();
    MutableLiveData<User> dataUser = new MutableLiveData<>();
    User mUser;
    Message message;
    private final ChatCallData chatCallData;
    private String idUser;

    private UserCallData userCallData;

    /**
     * Constructs a ChatRepository instance.
     *
     * @param userCallData  The UserCallData instance for user-related operations.
     * @param chatCallData  The ChatCallData instance for chat-related operations.
     * @param idUser        The unique identifier for the user.
     */
    public ChatRepository(UserCallData userCallData, ChatCallData chatCallData, String idUser) {
        this.userCallData = userCallData;
        this.chatCallData = chatCallData;
        this.idUser = idUser;
    }

    /**
     * Retrieves all chat messages for a chat.
     *
     * @return A MutableLiveData containing a list of chat messages.
     */
    public MutableLiveData<List<Message>> getAllMessageForChat() {

        chatCallData.getAllMessages().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mDatasetMessages.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
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

    /**
     * Retrieves user information.
     *
     * @return A MutableLiveData containing user information.
     */
    public MutableLiveData<User> getUser() {
        userCallData.getUser(idUser).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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


    /**
     * Creates a new chat message.
     *
     * @param message The content of the chat message.
     */
    public void createNewMessage(String message) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date date = calendar.getTime();


        userCallData.getUser(idUser).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    mUser = document.toObject(User.class);
                    dataUser.setValue(mUser);
                    chatCallData.getMessagesCollection().add(new Message(message, mUser, date)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            getAllMessageForChat();
                        }
                    });
                }
            }
        });
    }
}
