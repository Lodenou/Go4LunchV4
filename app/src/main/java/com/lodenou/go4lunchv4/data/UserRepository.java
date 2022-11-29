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

//    public MutableLiveData<List<User>> getUsers(){
//        dataset.clear();
//        setUsers();
//        data.setValue(dataset);
//        return data;
//    }
//
//    private void setUsers() {
//        dataset.add(new User("0", "Pablo", "https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"
//                ,""));
//        dataset.add(new User("1", "Pablo2", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
//                ,""));
//        dataset.add(new User("2", "Pablo3", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
//                ,""));
//        dataset.add(new User("3", "Pablo4", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
//                ,""));
//        dataset.add(new User("4", "Pablo5", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
//                ,""));
//    }

    public MutableLiveData<List<User>> getUsers() {
//FIXME NE VA NI DANS LE ONSUCCESS NI DANS LE ONfAILURE REGARDER LA DOC POUR RECUPERER DES INFOS DE FIRESTORE
        UserCallData.getAllUsers().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> listWorkmates = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot item : listWorkmates) {
                    mUser = item.toObject(User.class);
                    dataset.add(mUser);
                    System.out.println(dataset);
                }
                data.setValue(dataset);
            }
        });

        UserCallData.getAllUsers().get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("123", "" + e);
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
                        UserCallData.createUser(uid, username, urlPicture, " ").addOnFailureListener(new OnFailureListener() {
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
