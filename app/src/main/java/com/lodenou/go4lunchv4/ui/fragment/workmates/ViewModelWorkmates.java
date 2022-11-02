package com.lodenou.go4lunchv4.ui.fragment.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.model.User;
import java.util.List;

public class ViewModelWorkmates extends ViewModel {

    private MutableLiveData<List<User>> mMutableLiveData;
    private UserRepository mUserRepository;

    public void init() {
        if (mMutableLiveData != null) {
            return;
        }
        mUserRepository = mUserRepository.getInstance();
        mMutableLiveData = mUserRepository.getUsers();
    }

    public LiveData<List<User>> getUsers(){
        return mMutableLiveData;
    }


}
