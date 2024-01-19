package com.lodenou.go4lunchv4.data.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.model.User;
import com.google.firebase.firestore.Query;


/**
 * Data access class for user-related Firestore operations.
 */
public class UserCallData {
    private static final String COLLECTION_NAME = "users";
    private final FirebaseFirestore firebaseFirestore;

    /**
     * Constructor for UserCallData.
     *
     * @param firebaseFirestore The Firestore instance.
     */
    public UserCallData(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    /**
     * Get a reference to the users collection in Firestore.
     *
     * @return CollectionReference for the users collection.
     */
    public CollectionReference getUsersCollection() {
        return firebaseFirestore.collection(COLLECTION_NAME);
    }

    /**
     * Create a new user document in Firestore.
     *
     * @param uid                 The unique user ID.
     * @param userName            The user's name.
     * @param userAvatarUrl       The URL of the user's avatar.
     * @param userEmail           The user's email.
     * @param favoriteRestaurantId The ID of the user's favorite restaurant.
     * @param restaurantChosenId  The ID of the restaurant chosen by the user.
     * @param restaurantChosenName The name of the restaurant chosen by the user.
     * @return Task representing the completion of the user creation.
     */
    public Task<Void> createUser(String uid, String userName, String userAvatarUrl, String userEmail,
                                 String favoriteRestaurantId, String restaurantChosenId, String restaurantChosenName) {
        User userToCreate = new User(uid, userName, userAvatarUrl, userEmail, favoriteRestaurantId,
                restaurantChosenId, restaurantChosenName);
        return getUsersCollection().document(uid).set(userToCreate);
    }

    /**
     * Get a user document from Firestore based on the user's UID.
     *
     * @param uid The unique user ID.
     * @return Task representing the retrieval of the user document.
     */
    public Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }
    /**
     * Get a query for retrieving all user documents in Firestore.
     *
     * @return Query for retrieving all users.
     */
    public Query getAllUsers() {
        return getUsersCollection();
    }
}
