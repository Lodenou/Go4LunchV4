package com.lodenou.go4lunchv4.ui.activities;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.UserCallData;
import com.lodenou.go4lunchv4.databinding.ActivityDetailBinding;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.DetailResult;
import com.lodenou.go4lunchv4.model.detail.Result;
import com.lodenou.go4lunchv4.ui.adapters.DetailActivityAdapter;
import com.lodenou.go4lunchv4.ui.adapters.WorkmatesRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;
    ViewModelDetailActivity mViewModelDetailActivity;
    DetailActivityAdapter mAdapter;
    static int PERMISSION_CODE = 100;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        initRecyclerView();
        initViewModel();
    }


    private String getRestaurantId() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("idrestaurant");
    }

    private void initRecyclerView(){
        mAdapter = new DetailActivityAdapter(this, new ArrayList<>());
        mBinding.myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.myRecyclerView.setAdapter(this.mAdapter);
    }


    private void initViewModel() {
        mViewModelDetailActivity = new ViewModelProvider(this).get(ViewModelDetailActivity.class);
        mViewModelDetailActivity.init(getRestaurantId());

        // Get the restaurant info & fill it into the ui
        mViewModelDetailActivity.getRestaurantsDetail().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                fillWithRestaurantInfo(result);
                setOnClickOnCallButton(result);
                setOnClickOnWebsiteButton(result);
            }
        });

        // observe the user list & set it to the recycler view
       observeUsersList();

       // observe boolean , set click & update the ui with it
       observeIfCurrentUserHasChosenThisRestaurant();
    }

    private void observeIfCurrentUserHasChosenThisRestaurant(){
        mViewModelDetailActivity.isCurrentUserHasChosenThisRestaurant().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setClickChosenRestaurantButton(aBoolean);
                if (aBoolean){
                    mBinding.fab.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    mBinding.fab.setColorFilter(Color.argb(250, 25, 255, 25));
                    Log.d("123", aBoolean.toString() + " true");
                }
                else {
                    mBinding.fab.setImageResource(R.drawable.ic_baseline_crop_din_24);
                    Log.d("123", aBoolean.toString() +" false");
                }

            }
        });
    }

    private void observeUsersList(){
        mViewModelDetailActivity.getUsersEatingHere().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter.setUsersDetail(users);
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



    private void setClickChosenRestaurantButton(Boolean bool){
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabButtonSetting(bool);
                mViewModelDetailActivity.setIsThisRestaurantChosen(getRestaurantId());
            }
        });
    }

    private void fabButtonSetting(Boolean isAdded){
        if (!isAdded){
            mViewModelDetailActivity.addUserChoiceToDatabase(getRestaurantId());
            mBinding.fab.setImageResource(R.drawable.ic_baseline_check_circle_24);
            mBinding.fab.setColorFilter(Color.argb(250, 25, 255, 25));
            Log.d("123", isAdded.toString());

        }
        if (isAdded){
            Log.d("123", isAdded.toString());
            mViewModelDetailActivity.removeUserChoiceFromDatabase();
            mBinding.fab.setImageResource(R.drawable.ic_baseline_crop_din_24);

        }
    }

    private void setOnClickOnCallButton(Result result) {
        mBinding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FIXME CRASH QUAND DEMANDE 1ERE FOIS AUTORISATION 
                if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
                }
                    String phoneNumber = result.getInternationalPhoneNumber();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
            }
        });
    }

    private void setOnClickOnWebsiteButton(Result result) {
        mBinding.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String website = result.getWebsite();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(website));
                startActivity(i);
            }
        });
    }




}
