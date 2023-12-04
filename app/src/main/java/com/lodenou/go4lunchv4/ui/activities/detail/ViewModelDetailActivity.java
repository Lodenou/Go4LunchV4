package com.lodenou.go4lunchv4.ui.activities.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
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

    private DetailRepository detailRepository;


    public void init(String restaurantId){
        if(mMutableLiveDataUsers != null && mMutableLiveDataRestaurantDetail != null
                && mMutableLiveDataBooleanFab != null && mMutableLiveDataUser != null){
            return;
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        detailRepository = DetailRepository.getInstance(userCallData);
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
        detailRepository.addUserChoiceToDatabase(restaurantId);
    }

    public void removeUserChoiceFromDatabase(){
        detailRepository.removeUserChoiceFromDatabase();
    }
    public LiveData<Boolean> isCurrentUserHasChosenThisRestaurant(){
        return mMutableLiveDataBooleanFab;
    }

    public void setIsThisRestaurantChosen(String restaurantId){
        detailRepository.updateUserList();
        detailRepository.isRestaurantChosen(restaurantId);
    }

    // Favorite button
    public void addUserFavoriteToDatabase(String restaurantId){
        detailRepository.addUserFavoriteToDatabase(restaurantId);
    }

    public void removeUserFavoriteFromDatabase(){
        detailRepository.removeUserFavoriteFromDatabase();
    }

    public LiveData<User> getUser(String userId){
        mMutableLiveDataUser = detailRepository.getUser();
        return mMutableLiveDataUser;
    }

    public LiveData<Boolean> isRestaurantEgalToUserFavorite( String restaurantId){
        mMutableLiveDataBooleanFav = detailRepository.isRestaurantEqualToUserFavorite( restaurantId);
        return mMutableLiveDataBooleanFav;
    }

    public LiveData<List<String>> getListOfColleaguesWhoEatWithCurrentUser( String restaurantId){
        mListColleaguesLiveData = detailRepository.getListOfColleaguesWhoEatWithCurrentUser(restaurantId);
        return mListColleaguesLiveData;
    }
}
