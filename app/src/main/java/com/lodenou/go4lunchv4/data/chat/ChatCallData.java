package com.lodenou.go4lunchv4.data.chat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * This class provides access to the Firestore database for chat messages.
 */
public class ChatCallData {

    private final FirebaseFirestore firebaseFirestore;
    private static final String MESSAGES_COLLECTION = "messages";

    /**
     * Constructs a new ChatCallData instance.
     *
     * @param firebaseFirestore The instance of FirebaseFirestore to be injected.
     */
    public ChatCallData(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    /**
     * Gets the collection reference for chat messages.
     *
     * @return The CollectionReference for chat messages.
     */
    public  CollectionReference getMessagesCollection() {
        return firebaseFirestore.collection(MESSAGES_COLLECTION);
    }

    /**
     * Retrieves a query for fetching all chat messages sorted by dateCreated in ascending order.
     *
     * @return A Query object for fetching all chat messages.
     */
    public Query getAllMessages() {
        return getMessagesCollection()
                .orderBy("dateCreated")
                .limit(50);
    }
}
