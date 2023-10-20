package com.lodenou.go4lunchv4.data.user;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public interface IUserRepository {

    MutableLiveData<List<User>> getUsers();

    MutableLiveData<User> getUser();

    MutableLiveData<List<String>> getRestaurantChosenId();

    FirebaseUser getCurrentUser();
}
