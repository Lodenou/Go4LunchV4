package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.lodenou.go4lunchv4.data.ChatRepository;
import com.lodenou.go4lunchv4.data.DetailRepository;
import com.lodenou.go4lunchv4.model.Message;

import java.util.List;

public class ViewModelChat extends ViewModel {

    private ChatRepository mChatRepository;
    private MutableLiveData<List<Message>> mQueryLiveData;

    public void init(){
//        if(mMutableLiveDataUsers != null && mMutableLiveDataRestaurantDetail != null
//                && mMutableLiveDataBooleanFab != null && mMutableLiveDataUser != null){
//            return;
//        }
//        DetailRepository detailRepository = DetailRepository.getInstance();
//        mMutableLiveDataRestaurantDetail = detailRepository.getRestaurantDetails(restaurantId);
//        mMutableLiveDataUsers = detailRepository.getUsersEatingHere(restaurantId);
//        mMutableLiveDataBooleanFab = detailRepository.isCurrentUserHasChosenThisRestaurant(restaurantId);
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
