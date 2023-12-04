package com.lodenou.go4lunchv4.ui.fragment.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.data.user.UserRepository;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public class ViewModelWorkmates extends ViewModel {

    private MutableLiveData<List<User>> mMutableLiveData;
    private UserRepository mUserRepository;

    public void init() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        mUserRepository = new UserRepository(userCallData);
        mMutableLiveData = mUserRepository.getUsers();
    }

    public LiveData<List<User>> getUsers(){
        return mMutableLiveData;
    }


}
