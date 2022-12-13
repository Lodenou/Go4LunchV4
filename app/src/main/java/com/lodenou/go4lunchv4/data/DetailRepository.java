package com.lodenou.go4lunchv4.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
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

public class DetailRepository {

    private static DetailRepository instance;
    private ArrayList<User> datasetUsers = new ArrayList<>();
    MutableLiveData<List<User>> dataUsers = new MutableLiveData<>();
    User mUser;
    MutableLiveData<Boolean> dataCurrent = new MutableLiveData<>();
    MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> dataDetail =
            new MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result>();
    private com.lodenou.go4lunchv4.model.detail.Result mRestaurant;


    public static DetailRepository getInstance() {
        if (instance == null) {
            instance = new DetailRepository();
        }
        return instance;
    }

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


    public MutableLiveData<List<User>> getUsersEatingHere(String restaurantId) {

        datasetUsers.clear();
        UserCallData.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        mUser = document.toObject(User.class);
                        if (Objects.equals(mUser.getRestaurantChosen(), restaurantId)) {
                            datasetUsers.add(mUser);
                            Log.d("123", "onComplete: user add to recyclerView ");
                        }
                    }
                    dataUsers.setValue(datasetUsers);
                }
            }
        });
        return dataUsers;
    }


    public void addUserChoiceToDatabase(String restaurantId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> chosenRestaurant = new HashMap<>();
            chosenRestaurant.put("restaurantChosen", restaurantId);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(firebaseUser.getUid());
            docRef.set(chosenRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("123", "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("123", "Error writing document", e);
                }
            });
        }
    }

    public void removeUserChoiceFromDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> chosenRestaurant = new HashMap<>();
            chosenRestaurant.put("restaurantChosen", "");
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(firebaseUser.getUid());
            docRef.set(chosenRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("123", "DocumentSnapshot successfully unwritten!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("123", "Error writing document", e);
                }
            });
        }
    }

    public MutableLiveData<Boolean> isCurrentUserHasChosenThisRestaurant(String restaurantId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(currentUserId);
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

    public void updateUserList() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User currentUserMapped = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser
                .getPhotoUrl().toString(), currentUser.getEmail(), "", "");
        DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users")
                .document(currentUser.getUid());
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


    public void isRestaurantChosen(String restaurantId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userRestaurantChosenId = task.getResult().getString("restaurantChosen");
                    if (Objects.equals(userRestaurantChosenId, restaurantId)) {
                        dataCurrent.setValue(true);
                        Log.d("123", "onComplete: Value set to true");
                    } else {
                        dataCurrent.setValue(false);
                        Log.d("123", "onComplete: Value set to false");
                    }
                }
            }
        });
    }

    public void addUserFavoriteToDatabase(String restaurantId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> favoriteRestaurant = new HashMap<>();
            favoriteRestaurant.put("favoritesRestaurant", restaurantId);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(firebaseUser.getUid());
            docRef.set(favoriteRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("123", "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("123", "Error writing document", e);
                }
            });
        }
    }


}
