package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.model.User;

public class ViewModelMainActivity extends ViewModel {

    private UserRepository mUserRepository;
    private LiveData<User> mUserLiveData;

    public void init() {
//        if (mUserLiveData != null) {
//            return;
//        }
        mUserRepository = UserRepository.getInstance();
        mUserLiveData = mUserRepository.getUser();
    }

    public LiveData<User> getUser() {
        return mUserLiveData;
    }

    public void fetchUser() {
        mUserRepository.getUser();
    }
}
