package com.lodenou.go4lunchv4.data.detail;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lodenou.go4lunchv4.data.Go4LunchApi;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.DetailResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * An use case repository which aim
 * to combine data from
 * restaurant & user repositories
 **/

public class DetailRepository implements IDetailRepository {

    private static DetailRepository instance;
    private ArrayList<User> datasetUsers = new ArrayList<>();
    MutableLiveData<List<User>> dataUsers = new MutableLiveData<>();
    MutableLiveData<User> dataUser = new MutableLiveData<>();
    User mUser;
    MutableLiveData<Boolean> dataCurrent = new MutableLiveData<>();
    MutableLiveData<Boolean> dataIsFav = new MutableLiveData<>();
    MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> dataDetail =
            new MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result>();
    private com.lodenou.go4lunchv4.model.detail.Result mRestaurant;

    private ArrayList<String> datasetColleagues = new ArrayList<>();
    MutableLiveData<List<String>> dataColleagues = new MutableLiveData<>();

    private UserCallData userCallData;

    private String idUser;

    private String userName;
    private String userPhotoUrl;
    private String userEmail;


    /**
     * Gets an instance of DetailRepository with injection.
     *
     * @param userCallData The UserCallData instance for user-related operations.
     * @param idUser       The user's unique ID.
     * @param userName     The user's name.
     * @param userPhotoUrl The user's photo URL.
     * @param userEmail    The user's email.
     * @param data         MutableLiveData for restaurant details.
     */
    private DetailRepository(UserCallData userCallData, String idUser, String userName, String userPhotoUrl, String userEmail,
                             MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> data) {
        this.userCallData = userCallData;
        this.idUser = idUser;
        this.dataDetail = data;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.userEmail = userEmail;
    }

    /**
     * Gets an instance of DetailRepository with injection.
     *
     * @param userCallData The UserCallData instance for user-related operations.
     * @param idUser       The user's unique ID.
     * @param userName     The user's name.
     * @param userPhotoUrl The user's photo URL.
     * @param userEmail    The user's email.
     * @param dataDetail   MutableLiveData for restaurant details.
     * @return An instance of DetailRepository.
     */
    public static synchronized DetailRepository getInstance(UserCallData userCallData, String idUser, String userName,
                                                            String userPhotoUrl, String userEmail,
                                                            MutableLiveData<com.lodenou.go4lunchv4.
                                                                    model.detail.Result> dataDetail) {
        if (instance == null) {
            instance = new DetailRepository(userCallData, idUser, userName, userPhotoUrl, userEmail, dataDetail);
        }
        return instance;
    }

    /**
     * Resets the instance of DetailRepository.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Retrieves restaurant details for a given restaurant ID.
     *
     * @param restaurantId The ID of the restaurant.
     * @return MutableLiveData containing the restaurant details.
     */
    public MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> getRestaurantDetails(String restaurantId) {
        Go4LunchApi.retrofit.create(Go4LunchApi.class).getPlaceDetails(restaurantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DetailResult detailResult) {
                        mRestaurant = detailResult.getResult();
                        dataDetail.setValue(mRestaurant);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return dataDetail;
    }

    /**
     * Retrieves user information.
     *
     * @return MutableLiveData containing the user information.
     */
    public MutableLiveData<User> getUser() {

        userCallData.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mUser = documentSnapshot.toObject(User.class);
                dataUser.setValue(mUser);
            }
        });
        return dataUser;
    }


    /**
     * Retrieves a list of users eating at a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return MutableLiveData containing the list of users.
     */
    public MutableLiveData<List<User>> getUsersEatingHere(String restaurantId) {

        datasetUsers.clear();
        userCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                        if (Objects.equals(mUser.getRestaurantChosenId(), restaurantId)) {
                            datasetUsers.add(mUser);
                        }
                    }
                    dataUsers.setValue(datasetUsers);
                }
            }
        });
        return dataUsers;
    }

    /**
     * Adds the user's choice of a restaurant to the database.
     *
     * @param restaurantId The ID of the chosen restaurant.
     */
    public void addUserChoiceToDatabase(String restaurantId) {
        if (idUser != null) {

            String restaurantName = Objects.requireNonNull(dataDetail.getValue()).getName();

            Map<String, Object> chosenRestaurant = new HashMap<>();
            chosenRestaurant.put("restaurantChosenId", restaurantId);
            chosenRestaurant.put("restaurantChosenName", restaurantName);
            DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users")
                    .document(idUser);
            docRef.set(chosenRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //UPDATE USER
                    getUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    /**
     * Removes the user's choice of a restaurant from the database.
     */
    public void removeUserChoiceFromDatabase() {
        if (idUser != null) {
            Map<String, Object> chosenRestaurant = new HashMap<>();
            chosenRestaurant.put("restaurantChosenId", "");
            chosenRestaurant.put("restaurantChosenName", "");
            DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users").document(idUser);
            docRef.set(chosenRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //UPDATE USER
                    getUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
    /**
     * Checks if the current user has chosen a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return MutableLiveData indicating whether the current user has chosen the restaurant.
     */
    public MutableLiveData<Boolean> isCurrentUserHasChosenThisRestaurant(String restaurantId) {

        DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users").document(idUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    isRestaurantChosen(restaurantId);
                }
            }
        });
        return dataCurrent;
    }

    /**
     * Updates the user list based on the current user's presence.
     */
    public void updateUserList() {
        User currentUserMapped = new User(idUser, userName, userPhotoUrl, userEmail, "", "", "");
        DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users")
                .document(idUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Boolean isCurrentUserPresent = datasetUsers.stream()
                            .filter(user -> Objects.equals(user.getUid(), currentUserMapped.getUid())).findFirst().isPresent();
                    if (isCurrentUserPresent) {
                        // Find the index of the current user in the list
                        int index = -1;
                        for (int i = 0; i < datasetUsers.size(); i++) {
                            if (Objects.equals(datasetUsers.get(i).getUid(), currentUserMapped.getUid())) {
                                index = i;
                                break;
                            }
                        }
                        if (index >= 0) {
                            datasetUsers.remove(index);
                        }
                    } else {
                        datasetUsers.add(currentUserMapped);
                    }
                    dataUsers.setValue(datasetUsers);
                }
            }
        });
    }


    /**
     * Checks if a restaurant has been chosen by the current user.
     *
     * @param restaurantId The ID of the restaurant.
     */
    public void isRestaurantChosen(String restaurantId) {
        DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users").document(idUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userRestaurantChosenId = task.getResult().getString("restaurantChosenId");
                    if (Objects.equals(userRestaurantChosenId, restaurantId)) {
                        dataCurrent.setValue(true);
                    } else {
                        dataCurrent.setValue(false);
                    }
                }
            }
        });
    }

    /**
     * Adds a user's favorite restaurant to the database.
     *
     * @param restaurantId The ID of the favorite restaurant.
     */
    public void addUserFavoriteToDatabase(String restaurantId) {
        if (idUser != null) {
            Map<String, Object> favoriteRestaurant = new HashMap<>();
            favoriteRestaurant.put("favoritesRestaurant", restaurantId);
            DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users").document(idUser);
            docRef.set(favoriteRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    /**
     * Removes a user's favorite restaurant from the database.
     */
    public void removeUserFavoriteFromDatabase() {
        if (idUser != null) {
            Map<String, Object> favoriteRestaurant = new HashMap<>();
            favoriteRestaurant.put("favoritesRestaurant", "");
            DocumentReference docRef = userCallData.getAllUsers().getFirestore().collection("users").document(idUser);
            docRef.set(favoriteRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }


    /**
     * Checks if a restaurant is equal to the user's favorite restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return MutableLiveData indicating whether the restaurant is the user's favorite.
     */
    public MutableLiveData<Boolean> isRestaurantEqualToUserFavorite(String restaurantId) {
        if (Objects.equals(Objects.requireNonNull(getUser().getValue()).getFavoritesRestaurant(), restaurantId)) {
            dataIsFav.setValue(true);
        } else {
            dataIsFav.setValue(false);
        }
        return dataIsFav;
    }

    /**
     * Gets a list of colleagues who are eating at the same restaurant as the current user.
     *
     * @param restaurantId The ID of the restaurant.
     * @return MutableLiveData containing the list of colleague names.
     */
    public MutableLiveData<List<String>> getListOfColleaguesWhoEatWithCurrentUser(String restaurantId) {
        datasetColleagues.clear();
        userCallData.getAllUsers().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            datasetColleagues.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User mUser = document.toObject(User.class);
                                // if resto id != provided && user != current user to avoid adding current user to the list
                                if (Objects.equals(mUser.getRestaurantChosenId(), restaurantId) &&
                                        !Objects.equals(mUser.getUid(), Objects.requireNonNull(idUser))) {
                                    datasetColleagues.add(mUser.getUserName());
                                }
                            }
                            if (datasetColleagues != null) {
                                dataColleagues.setValue(datasetColleagues);
                            } else {
                                List<String> noOne = new ArrayList<>();
                                noOne.add("no one");
                                dataColleagues.setValue(noOne);
                            }
                        }
                    }
                });
        return dataColleagues;
    }
}
