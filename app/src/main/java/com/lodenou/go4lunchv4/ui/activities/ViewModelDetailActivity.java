package com.lodenou.go4lunchv4.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodenou.go4lunchv4.data.DetailRepository;
import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.Result;

import java.util.List;

public class ViewModelDetailActivity extends ViewModel {


    private MutableLiveData<List<User>> mMutableLiveDataUsers;
    private MutableLiveData<Result> mMutableLiveDataRestaurantDetail;
    private MutableLiveData<Boolean> mMutableLiveDataBooleanFab;
    private MutableLiveData<Boolean> mMutableLiveDataBooleanFav;
    private MutableLiveData<User> mMutableLiveDataUser;
    private LiveData<List<String>> mListColleaguesLiveData;

    public void init(String restaurantId){
        if(mMutableLiveDataUsers != null && mMutableLiveDataRestaurantDetail != null
                && mMutableLiveDataBooleanFab != null && mMutableLiveDataUser != null){
            return;
        }
        DetailRepository detailRepository = DetailRepository.getInstance();
        mMutableLiveDataRestaurantDetail = detailRepository.getRestaurantDetails(restaurantId);
        mMutableLiveDataUsers = detailRepository.getUsersEatingHere(restaurantId);
        mMutableLiveDataBooleanFab = detailRepository.isCurrentUserHasChosenThisRestaurant(restaurantId);

    }


    public LiveData<Result> getRestaurantsDetail(){
        return mMutableLiveDataRestaurantDetail;
    }

    public LiveData<List<User>> getUsersEatingHere(){
        return mMutableLiveDataUsers;
    }


    // Fab button
    public void addUserChoiceToDatabase(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.addUserChoiceToDatabase(restaurantId);
    }

    public void removeUserChoiceFromDatabase(){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.removeUserChoiceFromDatabase();
    }
    public LiveData<Boolean> isCurrentUserHasChosenThisRestaurant(){
        return mMutableLiveDataBooleanFab;
    }

    public void setIsThisRestaurantChosen(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.updateUserList();
        detailRepository.isRestaurantChosen(restaurantId);
    }

    // Favorite button
    public void addUserFavoriteToDatabase(String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.addUserFavoriteToDatabase(restaurantId);
    }

    public void removeUserFavoriteFromDatabase(){
        DetailRepository detailRepository = DetailRepository.getInstance();
        detailRepository.removeUserFavoriteFromDatabase();
    }

    public LiveData<User> getUser(String userId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        mMutableLiveDataUser = detailRepository.getUser(userId);
        return mMutableLiveDataUser;
    }

    public LiveData<Boolean> isRestaurantEgalToUserFavorite(String userId, String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        mMutableLiveDataBooleanFav = detailRepository.isRestaurantEgalToUserFavorite(userId, restaurantId);
        return mMutableLiveDataBooleanFav;
    }

    public LiveData<List<String>> getListOfColleaguesWhoEatWithCurrentUser( String restaurantId){
        DetailRepository detailRepository = DetailRepository.getInstance();
        mListColleaguesLiveData = detailRepository.getListOfColleaguesWhoEatWithCurrentUser(restaurantId);
        return mListColleaguesLiveData;
    }
}
