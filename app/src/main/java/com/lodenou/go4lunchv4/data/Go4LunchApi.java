package com.lodenou.go4lunchv4.data;

import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.model.detail.DetailResult;
import com.lodenou.go4lunchv4.model.nearbysearch.NearbySearchResults;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Go4LunchApi {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
            .build();

    @GET("nearbysearch/json?type=restaurant&key="+ BuildConfig.API_KEY )
    Observable<NearbySearchResults  > getNearbyPlaces(@Query("location") String location, @Query("radius") int radius);

    @GET("details/json?fields=name,vicinity,international_phone_number,website,photo,rating,geometry,place_id,opening_hours&key="+ BuildConfig.API_KEY)
    Observable<DetailResult> getPlaceDetails(@Query("place_id") String placeId);

//    @GET("autocomplete/json?types=establishment&radius=5000&strictbounds&key=" + BuildConfig.API_KEY)
//    Observable<Autocomplete> getAutocomplete(@Query("input") String input, @Query("location") String location, @Query("radius") int radius);
}
