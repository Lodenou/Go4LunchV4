package com.lodenou.go4lunchv4.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.room.RestaurantRoomDatabase;
import com.lodenou.go4lunchv4.data.user.UserCallData;
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

/**
 * This class represents the main activity of the application.
 * It handles navigation between fragments, configures the toolbar,
 * and the navigation drawer
 */
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

    /**
     * Called when the activity is starting.
     * It checks if the user is already signed in and updates the UI accordingly.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            reload();
        }
    }

    /**
     * Reset share pref when app start for fetchAllRestaurants() in RestaurantRepository
     */
    private void resetSharePref() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        editor.clear();
        editor.apply();
    }


    /**
     * Redirects the user to the login screen if they are not authenticated.
     */
    private void reload() {
        // User not logged in
        startActivity(new Intent(this, ConnexionActivity.class));
        finish();
    }

    /**
     * Sets up the bottom navigation view and initializes the default fragment.
     */
    private void setBottomNavigationView() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(mBinding.fragmentContainer.getId(), new MapFragment()).commit();
    }

    /**
     * Handles bottom navigation item selection and replaces the current fragment accordingly.
     */
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

    /**
     * Configures the toolbar at the top of the main activity.
     */
    private void setToolBar() {
        this.toolbar = mBinding.activityMainToolbar;
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    /**
     * Configures the drawer layout, enabling the navigation drawer.
     */
    private void setDrawerLayout() {
        this.drawerLayout = mBinding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Configures click listeners for items in the navigation drawer.
     */
    private void setNavigationViewClickListener() {
        this.navigationView = mBinding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_yourlunch:
                        clickYourLunch();
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

    /**
     * Initializes the view model for the main activity.
     */
    private void initViewModel() {
        mViewModelMainActivity = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ViewModelMainActivity.class);
        mViewModelMainActivity.init();
    }


    /**
     * Handles cleanup of room db when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Delete restaurants from db to avoid getting wrong restaurant list if the user change his location
        mViewModelMainActivity.deleteAllRestaurants();
    }


    /**
     * Observes changes in the user data and updates the UI accordingly.
     */
    private void observeGetUser() {
        mViewModelMainActivity.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                bool = Objects.equals(user.getRestaurantChosenId(), "");
                mUser = user;
            }
        });
    }

    /**
     * Resumes the activity, used to get updated user info after changing it in DetailActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Use to get updated info for user after changing it in DetailActivity
        observeGetUser();
    }

    /**
     * Handles the "Your Lunch" button click, navigating to the DetailActivity if a restaurant is chosen.
     * Displays a message if no restaurant is chosen.
     */
    private void clickYourLunch() {
        if (bool) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Oops")
                    .setMessage(R.string.nav_you_didnt_chose_restaurant)
                    .show();
        } else {

            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            if (mUser != null) {
                intent.putExtra("idrestaurant", mUser.getRestaurantChosenId());
            }
            startActivity(intent);
        }
    }


    /**
     * Sets up the navigation drawer header with user information.
     */
    private void setNavigationView() {
        this.navigationView = mBinding.activityMainNavView;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        UserRepository userRepository = new UserRepository(userCallData);
        FirebaseUser user = userRepository.getCurrentUser();
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

    /**
     * Logs the user out by signing out from Firebase and Google accounts.
     * Redirects to the login screen.
     */
    private void logOut() {
        // FIREBASE LOGOUT
        FirebaseAuth.getInstance().signOut();

        // GOOGLE LOGOUT
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        googleSignInClient.signOut();

        Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);

        // To avoid unwanted behavior from singleton variables like idUser in detail activity
        DetailRepository.resetInstance();

        finish();

        startActivity(intent);
    }
}
