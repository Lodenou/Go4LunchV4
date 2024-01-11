package com.lodenou.go4lunchv4.ui.activities.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.ActivtySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivtySettingsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivtySettingsBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        setBackButton();
        getPreferences();
    }

    private void setBackButton() {
        mBinding.backArrowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getPreferences(){
        ToggleButton tButton = mBinding.yesButton;
        SharedPreferences sharedPref = getSharedPreferences("1234",Context.MODE_PRIVATE);
        Boolean aBoolean = sharedPref.getBoolean("123", true);
        tButton.setChecked(aBoolean);
        tButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = getSharedPreferences("1234",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("123", b);
                editor.apply();

                if (b) {
                    Toast.makeText(getApplicationContext(), R.string.notifications_activated, Toast.LENGTH_SHORT).show();
                } else  {
                    Toast.makeText(getApplicationContext(), R.string.disable_notifications, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
