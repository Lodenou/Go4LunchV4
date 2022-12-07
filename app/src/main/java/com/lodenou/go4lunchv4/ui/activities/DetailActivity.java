package com.lodenou.go4lunchv4.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.databinding.ActivityDetailBinding;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.detail.Result;


import java.util.List;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;
    ViewModelDetailActivity mViewModelDetailActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        initViewModel();
    }

    private String getRestaurantId() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("idrestaurant");
    }


    private void initViewModel() {
        mViewModelDetailActivity = new ViewModelProvider(this).get(ViewModelDetailActivity.class);
        mViewModelDetailActivity.init(getRestaurantId());


        mViewModelDetailActivity.getRestaurantsDetail(getRestaurantId()).observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                fillWithRestaurantInfo(result);
            }
        });
    }


    private void fillWithRestaurantInfo(Result result){
        String address = result.getVicinity();
        String name = result.getName();
        mBinding.restaurantAddress.setText(address);
        mBinding.restaurantNameYourlunch.setText(name);

        if (result.getPhotos() != null) {
            String restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
                    result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;
            if (result.getPhotos() != null && result.getPhotos().size() > 0) {
                Glide.with(getApplicationContext()).load(restaurantPhoto)
                        .into(mBinding.restaurantImage);
            }
        }
    }
}
