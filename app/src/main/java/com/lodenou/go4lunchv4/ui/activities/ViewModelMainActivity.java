package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.lodenou.go4lunchv4.data.DetailRepository;
import com.lodenou.go4lunchv4.model.User;

public class ViewModelMainActivity extends ViewModel {

    private DetailRepository mDetailRepository;
    private LiveData<User> mUserLiveData;



    public void init() {

        mDetailRepository = DetailRepository.getInstance();
        mUserLiveData = mDetailRepository.getUser();

    }

    public LiveData<User> getUser() {
        return mUserLiveData;
    }
}
