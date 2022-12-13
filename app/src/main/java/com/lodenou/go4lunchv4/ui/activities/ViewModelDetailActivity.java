package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.DetailRepository;
import com.lodenou.go4lunchv4.data.SelectedRestaurantRepository;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.Result;

import java.util.List;

public class ViewModelDetailActivity extends ViewModel {


    private MutableLiveData<List<User>> mMutableLiveDataUsers;
    private MutableLiveData<Result> mMutableLiveDataRestaurantDetail;
    private MutableLiveData<Boolean> mMutableLiveDataBoolean;

    public void init(String restaurantId){
        if(mMutableLiveDataUsers != null && mMutableLiveDataRestaurantDetail != null && mMutableLiveDataBoolean != null){
            return;
        }
        DetailRepository detailRepository = DetailRepository.getInstance();
        mMutableLiveDataRestaurantDetail = detailRepository.getRestaurantDetails(restaurantId);
        mMutableLiveDataUsers = detailRepository.getUsersEatingHere(restaurantId);
        mMutableLiveDataBoolean = detailRepository.isCurrentUserHasChosenThisRestaurant(restaurantId);

    }


    public LiveData<Result> getRestaurantsDetail(){
        return mMutableLiveDataRestaurantDetail;
    }

    public LiveData<List<User>> getUsersEatingHere(){
        return mMutableLiveDataUsers;
    }

    public void addUserChoiceToDatabase(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.addUserChoiceToDatabase(restaurantId);
    }

    public void removeUserChoiceFromDatabase(){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.removeUserChoiceFromDatabase();
    }

    public void addUserFavoriteToDatabase(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.addUserFavoriteToDatabase(restaurantId);
    }

    public LiveData<Boolean> isCurrentUserHasChosenThisRestaurant(){
        return mMutableLiveDataBoolean;
    }

    public void setIsThisRestaurantChosen(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.updateUserList();
        detailRepository.isRestaurantChosen(restaurantId);
    }
}
