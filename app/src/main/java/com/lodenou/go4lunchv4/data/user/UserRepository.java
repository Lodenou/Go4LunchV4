package com.lodenou.go4lunchv4.data.user;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.model.User;
import java.util.ArrayList;
import java.util.List;


/**
 * Repository class for user-related operations.
 */
public class UserRepository implements IUserRepository{

    private ArrayList<User> datasetUsers = new ArrayList<>();
    MutableLiveData<List<User>> dataUsers = new MutableLiveData<>();
    private ArrayList<String> datasetChosenRestaurantId = new ArrayList<>();
    MutableLiveData<List<String>> dataChosenRestaurantId = new MutableLiveData<>();
    User mUser;


    private UserCallData userCallData;

    /**
     * Constructor for UserRepository.
     *
     * @param userCallData The UserCallData instance for data access.
     */
    public UserRepository(UserCallData userCallData) {
        this.userCallData = userCallData;
    }


    /**
     * Get a MutableLiveData containing a list of users.
     *
     * @return MutableLiveData containing a list of users.
     */
    public MutableLiveData<List<User>> getUsers(){

        userCallData.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    datasetUsers.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                        datasetUsers.add(mUser);
                    }
                    dataUsers.setValue(datasetUsers);
                }
            }
        });
         return dataUsers;
    }


    /**
     * Get a MutableLiveData containing a list of chosen restaurant IDs.
     *
     * @return MutableLiveData containing a list of chosen restaurant IDs.
     */
    public MutableLiveData<List<String>> getRestaurantChosenId(){
        userCallData.getAllUsers().whereNotEqualTo("restaurantChosenId", "").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    datasetChosenRestaurantId.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                        datasetChosenRestaurantId.add(mUser.getRestaurantChosenId());
                    }
                    dataChosenRestaurantId.setValue(datasetChosenRestaurantId);
                }
            }
        });
        return dataChosenRestaurantId;
    }
    /**
     * Get the current Firebase user.
     *
     * @return The current Firebase user.
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
