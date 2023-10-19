package com.lodenou.go4lunchv4.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.room.RestaurantRoomDatabase;
import com.lodenou.go4lunchv4.data.user.UserRepository;
import com.lodenou.go4lunchv4.databinding.ActivityMainBinding;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.ui.activities.settings.SettingsActivity;
import com.lodenou.go4lunchv4.ui.activities.chat.ChatActivity;
import com.lodenou.go4lunchv4.ui.activities.connexion.ConnexionActivity;
import com.lodenou.go4lunchv4.ui.activities.detail.DetailActivity;
import com.lodenou.go4lunchv4.ui.fragment.listview.ListViewFragment;
import com.lodenou.go4lunchv4.ui.fragment.map.MapFragment;
import com.lodenou.go4lunchv4.ui.fragment.workmates.WorkmatesFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewModelMainActivity mViewModelMainActivity;
    private FirebaseAuth mAuth;
    Boolean bool;
    User mUser;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_IS_FIRST_LAUNCH = "isFirstLaunch";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        RestaurantRoomDatabase.getDatabase(getApplicationContext());
        setBottomNavigationView();
        setToolBar();
        setDrawerLayout();
        setNavigationViewClickListener();
        resetSharePref();
        initViewModel();
        setNavigationView();
        observeGetUser();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            reload();
        }
    }

    private void resetSharePref(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
         editor = settings.edit();
        editor.clear();
        editor.apply();
    }


    // restart connexion activity if the user isn't connected
    private void reload() {
        // User not logged in
        startActivity(new Intent(this, ConnexionActivity.class));
        finish();
    }

    private void setBottomNavigationView() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(mBinding.fragmentContainer.getId(), new MapFragment()).commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.map_view) {
            selectedFragment = new MapFragment();
        } else if (itemId == R.id.list_view) {
            selectedFragment = new ListViewFragment();
        } else if (itemId == R.id.workmates) {
            selectedFragment = new WorkmatesFragment();
        }
        // It will help to replace the
        // one fragment to other.
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };

    // 1 - Configure Toolbar
    private void setToolBar() {
        this.toolbar = mBinding.activityMainToolbar;
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void setDrawerLayout() {
        this.drawerLayout = mBinding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void setNavigationViewClickListener() {
        this.navigationView = mBinding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_yourlunch:

                        clickYourLunch();
//                        mViewModelMainActivity.fetchUser();
                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        logOut();
                        break;
                    case R.id.chat:
                        Intent intentC = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(intentC);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void initViewModel() {
        mViewModelMainActivity = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ViewModelMainActivity.class);
        mViewModelMainActivity.init();
    }

    private void fetchAllRestaurants(){
        // get SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstLaunch = settings.getBoolean(PREFS_IS_FIRST_LAUNCH, true);
        if (isFirstLaunch) {
            mViewModelMainActivity.fetchAllRestaurants(getTask(), getPermission(), getApplicationContext());
            // This is the first launch of the application
            // Save that it's no longer the first launch of the application
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_IS_FIRST_LAUNCH, false);
            editor.apply();
    }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //FIXME the logout actually cause pb with the phone rotation, the app goes to the connexion at each rotation.
        logOut();
        // Delete restaurants from db to avoid getting wrong restaurant list if the user change his location
        mViewModelMainActivity.deleteAllRestaurants();
    }


    private void observeGetUser() {
        mViewModelMainActivity.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                bool = Objects.equals(user.getRestaurantChosenId(), "");
                mUser = user;
            }
        });
    }


    private void clickYourLunch() {
        if (bool) {
            Log.d("123", "onChanged: if");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Oops")
                    .setMessage(R.string.nav_you_didnt_chose_restaurant)
                    .show();
        } else {
            Log.d("123", "onChanged: elseif");
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            if (mUser != null) {
                intent.putExtra("idrestaurant", mUser.getRestaurantChosenId());
            }
            startActivity(intent);
        }
    }


    private void setNavigationView() {
        this.navigationView = mBinding.activityMainNavView;
        FirebaseUser user = UserRepository.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        String userName = user.getDisplayName();
        Uri userPhoto = user.getPhotoUrl();

        View headerView = mBinding.activityMainNavView.getHeaderView(0);

        TextView mUserEmail = headerView.findViewById(R.id.nav_user_mail);
        TextView mUserName = headerView.findViewById(R.id.textview_user_name);
        ImageView mUserPhoto = headerView.findViewById(R.id.user_photo);

        mUserEmail.setText(userEmail);
        mUserName.setText(userName);
        Glide.with(this)
                .load(userPhoto)
                .sizeMultiplier(0.1f)
                .circleCrop()
                .into(mUserPhoto);
    }

    private void logOut() {

        // FIREBASE LOGOUT
        FirebaseAuth.getInstance().signOut();
        // GOOGLE LOGOUT
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
        finish();
        startActivity(intent);
    }


    private Boolean getPermission() {

        Boolean isPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED;

        Boolean isPermissionOk = !(isPermission);
        return isPermissionOk;
    }

    private Task getTask() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient.getLastLocation();
    }
}