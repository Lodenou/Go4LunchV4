package com.lodenou.go4lunchv4.data;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static UserRepository instance;
    private ArrayList<User> dataset = new ArrayList<>();
    MutableLiveData<List<User>> data = new MutableLiveData<>();
    User mUser;


    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }



    public MutableLiveData<List<User>> getUsers(){

        dataset.clear();
        UserCallData.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        mUser = document.toObject(User.class);
                        dataset.add(mUser);
                    }
                    data.setValue(dataset);
                } else {
                    Log.d("123", "Error getting documents: ", task.getException());
                }
            }
        });
         return data;
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void createUserInFirestore() {
        if (this.getCurrentUser() != null) {
            final String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            final String username = this.getCurrentUser().getDisplayName();
            final String uid = this.getCurrentUser().getUid();
            final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            UserCallData.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);
                    if (mUser == null) {
                        UserCallData.createUser(uid, username, urlPicture, " ", "").addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Firestore Error 1", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "onFailure: firestore error 1 ");
                            }
                        });
                    }
                }
            });
        }
    }
}
