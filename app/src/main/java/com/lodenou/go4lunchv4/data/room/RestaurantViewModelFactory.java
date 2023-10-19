package com.lodenou.go4lunchv4.data.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.lodenou.go4lunchv4.ui.activities.main.ViewModelMainActivity;

public class RestaurantViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    public RestaurantViewModelFactory(Application myApplication) {
        application = myApplication;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ViewModelMainActivity(application);
    }
}
