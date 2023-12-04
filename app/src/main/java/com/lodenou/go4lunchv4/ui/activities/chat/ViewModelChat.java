package com.lodenou.go4lunchv4.ui.activities.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.chat.ChatRepository;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.Message;

import java.util.List;

public class ViewModelChat extends ViewModel {

    private ChatRepository mChatRepository;
    private MutableLiveData<List<Message>> mQueryLiveData;

    public void init(){
        if (mQueryLiveData != null){
            return;
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        mChatRepository = new ChatRepository(userCallData);
        mQueryLiveData = mChatRepository.getAllMessageForChat();
    }

    public LiveData<List<Message>> getAllMessageForChat(){
        return mQueryLiveData;
    }

    public void createNewMessage(String message) {
        mChatRepository.createNewMessage(message);
    }


}
