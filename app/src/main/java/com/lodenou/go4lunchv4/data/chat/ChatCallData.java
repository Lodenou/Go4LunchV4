package com.lodenou.go4lunchv4.data.chat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatCallData {

    private final FirebaseFirestore firebaseFirestore;
    private static final String MESSAGES_COLLECTION = "messages";

    // Injectez FirebaseFirestore via le constructeur
    public ChatCallData(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public  CollectionReference getMessagesCollection() {
        return firebaseFirestore.collection(MESSAGES_COLLECTION);
    }

    public Query getAllMessages() {
        return getMessagesCollection()
                .orderBy("dateCreated")
                .limit(50);
    }
}
