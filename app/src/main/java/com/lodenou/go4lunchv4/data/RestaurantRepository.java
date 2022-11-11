package com.lodenou.go4lunchv4.data;


import androidx.lifecycle.MutableLiveData;

import com.lodenou.go4lunchv4.model.detail.DetailResult;
import com.lodenou.go4lunchv4.model.nearbysearch.NearbySearchResults;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private ArrayList<Result> dataset = new ArrayList<>();
    private com.lodenou.go4lunchv4.model.detail.Result mRestaurant;
    MutableLiveData<List<Result>> dataNearby = new MutableLiveData<>();
    MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> dataDetail =
            new MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result>();

    public static RestaurantRepository getInstance(){
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

//    public MutableLiveData<List<Restaurant>> getRestaurants(){
//        dataset.clear();
//        setRestaurants();
//        data.setValue(dataset);
//        return data;
//    }
//
//    private void setRestaurants() {
//        dataset.add(new Restaurant("0", "12 rue blabla", "bloblo"
//                ,"https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"));
//        dataset.add(new Restaurant("1", "12 rue blabl7777", "blo442blo"
//                ,"https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"));
//        dataset.add(new Restaurant("2", "12 rue blabla5555", "bl2424oblo"
//                ,"https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"));
//        dataset.add(new Restaurant("3", "12 rue blabla222", "blo422424blo"
//                ,"https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"));
//        dataset.add(new Restaurant("4", "12 rue blabla111", "bl2424oblo"
//                ,"https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"));
//    }

    public MutableLiveData<List<Result>> getNearbyRestaurants(String location){
        Go4LunchApi.retrofit.create(Go4LunchApi.class).getNearbyPlaces(location, 3000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NearbySearchResults>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NearbySearchResults nearbySearchResults) {
                        dataset.clear();
                        dataset.addAll(nearbySearchResults.getResults());
                        dataNearby.setValue(dataset);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return dataNearby;
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
}
