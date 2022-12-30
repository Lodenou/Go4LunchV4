package com.lodenou.go4lunchv4.data;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class ChatCallData {
    private static final String MESSAGES_COLLECTION = "messages";

    private ChatCallData() { }


    public static CollectionReference getMessagesCollection(){
        return FirebaseFirestore.getInstance().collection(MESSAGES_COLLECTION);
    }

    public static Query getAllMessages() {
        return ChatCallData.getMessagesCollection()
                .orderBy("dateCreated")
                .limit(50);
    }

}
