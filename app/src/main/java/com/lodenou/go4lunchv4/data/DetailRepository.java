package com.lodenou.go4lunchv4.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.DetailResult;
import com.lodenou.go4lunchv4.model.detail.Result;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
    An use case repository which aim
    to combine data from
    restaurant & user repositories
 **/

public class DetailRepository {

    private static DetailRepository instance;
    private ArrayList<User> datasetUsers = new ArrayList<>();
    MutableLiveData<List<User>> dataUsers = new MutableLiveData<>();
    User mUser;

    MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> dataDetail =
            new MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result>();
    private com.lodenou.go4lunchv4.model.detail.Result mRestaurant;


    public static DetailRepository getInstance(){
        if (instance == null) {
            instance = new DetailRepository();
        }
        return instance;
    }

    public MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> getRestaurantDetails(String restaurantId){
        Go4LunchApi.retrofit.create(Go4LunchApi.class).getPlaceDetails(restaurantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DetailResult detailResult) {
                        mRestaurant  = detailResult.getResult();
                        dataDetail.setValue(mRestaurant);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return dataDetail;
    }


    public MutableLiveData<List<User>> getUsersEatingHere(String restaurantId){
        datasetUsers.clear();

        UserCallData.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        mUser = document.toObject(User.class);
                        if (mUser.getRestaurantChosen() == restaurantId){
                            datasetUsers.add(mUser);
                        }
                    }
                    dataUsers.setValue(datasetUsers);
                }
            }
        });
        return dataUsers;
    }

































}
