package com.lodenou.go4lunchv4.ui.activities.detail;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.NotificationReceiver;
import com.lodenou.go4lunchv4.databinding.ActivityDetailBinding;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.detail.Result;
import com.lodenou.go4lunchv4.ui.activities.main.ViewModelMainActivity;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
/**
 * This activity displays detailed information about a restaurant, including its name, address,
 * photo, opening hours, and user reviews.
 */
public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private ViewModelDetailActivity mViewModelDetailActivity;
    private ViewModelMainActivity mViewModelMainActivity;
    private DetailActivityAdapter mAdapter;
    private static int PERMISSION_CODE = 100;
    private final String CHANNEL_ID = "122";
    private Boolean bool;
    private Result mResult;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        initRecyclerView();
        initViewModel();
        initMainViewModel();
        getUser();
        createNotificationChannel();
        addUserToNotificationsCalls();
    }

    /**
     * Get the restaurant ID from the intent's extras.
     *
     * @return The restaurant ID
     */
    private String getRestaurantId() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("idrestaurant");
    }

    /**
     * Initialize the RecyclerView and its adapter.
     */
    private void initRecyclerView() {
        mAdapter = new DetailActivityAdapter(this);
        mBinding.myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.myRecyclerView.setAdapter(this.mAdapter);
    }

    /**
     * Initialize the ViewModel for this activity.
     */
    private void initViewModel() {
        mViewModelDetailActivity = new ViewModelProvider(this).get(ViewModelDetailActivity.class);
        mViewModelDetailActivity.init(getRestaurantId());

        // Get the restaurant info & fill it into the ui
        mViewModelDetailActivity.getRestaurantsDetail().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                // phone purpose
                mResult = result;

                fillWithRestaurantInfo(result);
                setOnClickOnCallButton(result);
                setOnClickOnWebsiteButton(result);
            }
        });
        // observe the user list eating here & set it to the recycler view
        observeUsersList();
        // observe boolean , set click & update the ui with it
        observeIfCurrentUserHasChosenThisRestaurant();
    }

    /**
     * Initialize the ViewModel for the main activity.
     */
    private void initMainViewModel() {
        mViewModelMainActivity = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ViewModelMainActivity.class);
        mViewModelMainActivity.init();
    }

    /**
     * Observe if the current user has chosen this restaurant.
     */
    private void observeIfCurrentUserHasChosenThisRestaurant() {
        mViewModelDetailActivity.isCurrentUserHasChosenThisRestaurant().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setClickChosenRestaurantButton(aBoolean);
                if (aBoolean) {
                    mBinding.fab.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    mBinding.fab.setColorFilter(Color.argb(250, 25, 255, 25));
                } else {
                    mBinding.fab.setImageResource(R.drawable.ic_baseline_crop_din_24);
                }
            }
        });
    }


    /**
     * Observe the list of users who are eating at this restaurant and update the RecyclerView.
     */
    private void observeUsersList() {
        mViewModelDetailActivity.getUsersEatingHere().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter.setUsersDetail(users);
            }
        });
    }

    /**
     * Fill the UI with restaurant information.
     *
     * @param result The Result object containing restaurant details.
     */
    private void fillWithRestaurantInfo(Result result) {
        String address = result.getVicinity();
        String name = result.getName();
        mBinding.restaurantAddress.setText(address);
        mBinding.restaurantNameYourlunch.setText(name);

        if (result.getPhotos() != null) {
            String restaurantPhoto =  BuildConfig.RESTAURANT_PHOTO_URL+
                    result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;
            if (result.getPhotos() != null && result.getPhotos().size() > 0) {
                Glide.with(getApplicationContext()).load(restaurantPhoto)
                        .into(mBinding.restaurantImage);
            }
        }
    }

    /**
     * Set the click listener for the "Chosen Restaurant" button.
     *
     * @param bool A boolean indicating whether the current user has chosen this restaurant.
     */
    private void setClickChosenRestaurantButton(Boolean bool) {
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabButtonSetting(bool);
                mViewModelDetailActivity.setIsThisRestaurantChosen(getRestaurantId());
            }
        });
    }

    private void fabButtonSetting(Boolean isAdded) {
        if (!isAdded) {
            mViewModelDetailActivity.addUserChoiceToDatabase(getRestaurantId());
            mBinding.fab.setImageResource(R.drawable.ic_baseline_check_circle_24);
            mBinding.fab.setColorFilter(Color.argb(250, 25, 255, 25));
        }

        else {
            removeUserFromNotificationCall();
            mViewModelDetailActivity.removeUserChoiceFromDatabase();
            mBinding.fab.setImageResource(R.drawable.ic_baseline_crop_din_24);
        }
    }

    // END OF FAB SETTING PART
    /**
     * Set the click listener for the "Call" button.
     *
     * @param result The Result object containing restaurant details.
     */
    private void setOnClickOnCallButton(Result result) {
        mBinding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DetailActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted => make the call
                    makeCall(result.getInternationalPhoneNumber());
                } else {
                    // Ask for permission
                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]
                            {Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
                }
            }
        });
    }

    /**
     * Make a phone call with the given phone number.
     *
     * @param phoneNumber The phone number to call.
     */
    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Handle the result of permission requests.
     *
     * @param requestCode  The request code for the permission request.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(mResult.getInternationalPhoneNumber());
            } else {
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Set the click listener for the "Website" button.
     *
     * @param result The Result object containing restaurant details.
     */
    private void setOnClickOnWebsiteButton(Result result) {
        mBinding.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.getWebsite() != null) {
                    String website = result.getWebsite();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(website));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "No website available", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // FAVORITE BUTTON PART
    /**
     * Get the current user's information.
     */
    private void getUser() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mViewModelDetailActivity.getUser(userId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setOnClickFavoriteButton();
                setUserFavStarUi(user);
            }
        });
    }

    /**
     * Set the click listener for the "Favorite" button.
     */
    private void setOnClickFavoriteButton() {
        mBinding.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserFav();
            }
        });
    }

    /**
     * Check if the user has marked this restaurant as a favorite and update UI accordingly.
     */
    private void isUserFav() {
        // We need a boolean which uses data from repository to get the update version of user.getFavoriteRestaurant()
        mViewModelDetailActivity.isRestaurantEgalToUserFavorite(getRestaurantId())
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (aBoolean) {
                            mViewModelDetailActivity.removeUserFavoriteFromDatabase();
                            mBinding.imageStar.setVisibility(View.INVISIBLE);
                        } else {
                            mViewModelDetailActivity.addUserFavoriteToDatabase(getRestaurantId());
                            mBinding.imageStar.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Update the UI based on whether the user has marked this restaurant as a favorite.
     *
     * @param user The current user's information.
     */
    private void setUserFavStarUi(User user) {
        // We don't need a boolean from repository because the user is already updated when we go back to previous activity
        if (Objects.equals(user.getFavoritesRestaurant(), getRestaurantId())) {
            mBinding.imageStar.setVisibility(View.VISIBLE);
        } else {
            mBinding.imageStar.setVisibility(View.INVISIBLE);
        }
    }

    // END OF FAVORITE BUTTON PART


    //Notifications
    /**
     * Set up notifications for the chosen restaurant.
     *
     * @param restaurantName    The name of the restaurant.
     * @param restaurantAddress The address of the restaurant.
     * @param colleagues        List of colleagues to include in the notification.
     */
    private void setNotifications(String restaurantName, String restaurantAddress, List<String> colleagues) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bool = true;

        mViewModelDetailActivity.getUser(currentUser.getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (Objects.equals(Objects.requireNonNull(user).getRestaurantChosenId(), getRestaurantId())) {
                    bool = false;
                    createPendingIntent(restaurantName, restaurantAddress, colleagues);
                }
            }
        });
    }


    /**
     * Create a notification channel for this app (API 26+).
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 1";
            String description = "Channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create a pending intent for restaurant notifications.
     *
     * @param restaurantName    The name of the restaurant.
     * @param restaurantAddress The address of the restaurant.
     * @param colleagues        List of colleagues to include in the notification.
     */
    private void createPendingIntent(String restaurantName, String restaurantAddress, List<String> colleagues) {
        ArrayList<String> colleaguesArray = new ArrayList<>(colleagues);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.setAction("com.example.notification.NOTIFICATION_ACTION");
        notificationIntent.putExtra("restaurantName", restaurantName);
        notificationIntent.putExtra("restaurantAddress", restaurantAddress);
        notificationIntent.putStringArrayListExtra("colleagues", colleaguesArray);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get an instance of the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar notificationTime = Calendar.getInstance();

        notificationTime.set(Calendar.HOUR_OF_DAY, 1);
        notificationTime.set(Calendar.MINUTE, 40);
        notificationTime.set(Calendar.SECOND, 0);

        // Check if the Calendar time is in the past
        if (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
            notificationTime.add(Calendar.DAY_OF_YEAR, 1); // it will tell to run to next day
        }

        long triggerTime = notificationTime.getTimeInMillis();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedTime = formatter.format(new Date(triggerTime));
        // Set the alarm to trigger the pending intent at the specified time
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    /**
     * Get restaurant details and set up notifications.
     *
     * @param colleagues List of colleagues who are eating at the same restaurant.
     */
    private void getRestaurantDetail(List<String> colleagues) {
        mViewModelDetailActivity.getRestaurantsDetail().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                setNotifications(result.getName(), result.getVicinity(), colleagues);
            }
        });
    }

    /**
     * Add the current user to notification calls.
     */
    private void addUserToNotificationsCalls() {
        mViewModelDetailActivity.getListOfColleaguesWhoEatWithCurrentUser(getRestaurantId())
                .observe(this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        getRestaurantDetail(strings);
                    }
                });
    }

    /**
     * Remove the user from notification calls.
     */
    private void removeUserFromNotificationCall() {
        bool = true;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}
