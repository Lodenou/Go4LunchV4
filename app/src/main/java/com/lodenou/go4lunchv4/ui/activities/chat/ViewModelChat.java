package com.lodenou.go4lunchv4.ui.activities.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.lodenou.go4lunchv4.data.chat.ChatRepository;
import com.lodenou.go4lunchv4.model.Message;

import java.util.List;

public class ViewModelChat extends ViewModel {

    private ChatRepository mChatRepository;
    private MutableLiveData<List<Message>> mQueryLiveData;

    public void init(){
        if (mQueryLiveData != null){
            return;
        }
        mChatRepository = ChatRepository.getInstance();
        mQueryLiveData = mChatRepository.getAllMessageForChat();
    }

    public LiveData<List<Message>> getAllMessageForChat(){
        return mQueryLiveData;
    }

    public void createNewMessage(String message) {
        mChatRepository.createNewMessage(message);
    }


}
