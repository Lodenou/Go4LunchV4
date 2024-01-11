package com.lodenou.go4lunchv4.data.chat;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.model.Message;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public interface IChatRepository {

    MutableLiveData<List<Message>> getAllMessageForChat();

    MutableLiveData<User> getUser();

    void createNewMessage(String message);

}
