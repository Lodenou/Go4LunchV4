package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.ViewModel;
import com.lodenou.go4lunchv4.data.UserRepository;

public class ViewModelMainActivity extends ViewModel {

    private UserRepository mUserRepository;

    public void init(){
        mUserRepository = mUserRepository.getInstance();
        mUserRepository.createUserInFirestore();
    }
}
