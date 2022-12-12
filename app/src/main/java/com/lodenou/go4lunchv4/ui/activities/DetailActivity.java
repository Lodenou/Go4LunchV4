package com.lodenou.go4lunchv4.ui.activities;

import static java.security.AccessController.getContext;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        initRecyclerView();
        initViewModel();
//        setClickChosenRestaurantButton();

    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                Log.d("123", firebaseAuth.getCurrentUser().getUid());
            }
        });
        super.onStart();
    }

    private String getRestaurantId() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("idrestaurant");
    }

    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initRecyclerView(){
        mAdapter = new DetailActivityAdapter(this, new ArrayList<>());
        mBinding.myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.myRecyclerView.setAdapter(this.mAdapter);
    }


    private void initViewModel() {
        mViewModelDetailActivity = new ViewModelProvider(this).get(ViewModelDetailActivity.class);
        mViewModelDetailActivity.init(getRestaurantId());


        mViewModelDetailActivity.getRestaurantsDetail().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                fillWithRestaurantInfo(result);
            }
        });

       observeUsersList();

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
                //FIXME NE MET PAS A JOUR LA LISTE DES QUE LE USER CLIQUE
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
            mViewModelDetailActivity.removeUserFromDatabase();
            mBinding.fab.setImageResource(R.drawable.ic_baseline_crop_din_24);

        }
    }
//    private void setClickChosenRestaurantButton(){
//        mBinding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                        Map<String, Object> chosenRestaurant = new HashMap<>();
//                        chosenRestaurant.put("restaurantChosen", getRestaurantId());
//                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                        DocumentReference docRef = UserCallData.getAllUsers().getFirestore().collection("users").document(firebaseUser.getUid());
//                        docRef.set(chosenRestaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Log.d("123", "DocumentSnapshot successfully written!");
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d("123", "Error writing document", e);
//                            }
//                        });
//                    }
//                    else {
//                        Log.d("123", "pb with firebaseauth!");
//                    }
//
//            }
//        });
//    }


}
