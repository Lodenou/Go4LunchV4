package com.lodenou.go4lunchv4.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lodenou.go4lunchv4.databinding.ActivityDetailBinding;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;


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


        private void initViewModel(){
            mViewModelDetailActivity = new ViewModelProvider(this).get(ViewModelDetailActivity.class);
            mViewModelDetailActivity.init();


            mViewModelDetailActivity.getSelectedRestaurants().observe(this, new Observer<List<SelectedRestaurant>>() {
                @Override
                public void onChanged(List<SelectedRestaurant> selectedRestaurants) {
//                    mAdapter.notifyDataSetChanged();
                }
            });


        }

}
