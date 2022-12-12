package com.lodenou.go4lunchv4.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.resources.TextAppearance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.UserRepository;
import com.lodenou.go4lunchv4.databinding.ActivityMainBinding;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.ui.fragment.listview.ListViewFragment;
import com.lodenou.go4lunchv4.ui.fragment.listview.ViewModelListView;
import com.lodenou.go4lunchv4.ui.fragment.map.MapFragment;
import com.lodenou.go4lunchv4.ui.fragment.workmates.WorkmatesFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ViewModelMainActivity mViewModelMainActivity;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        setBottomNavigationView();
        setToolBar();
        setDrawerLayout();
        setNavigationViewClickListener();
        initViewModel();
        setNavigationView();
        Log.d("123", getCurrentUser().getUid());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            reload();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logOut();
    }

    // restart connexion activity if the user isn't connected
    private void reload(){
            // User not logged in
            startActivity(new Intent(this, ConnexionActivity.class));
            finish();
    }

    private void setBottomNavigationView(){
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
    private void setToolBar(){
        this.toolbar = mBinding.activityMainToolbar;
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void setDrawerLayout(){
        this.drawerLayout = mBinding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void setNavigationViewClickListener(){
        this.navigationView = mBinding.activityMainNavView;
//        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_yourlunch:
                        Intent myIntent = new Intent(MainActivity.this, DetailActivity.class);
                        startActivity(myIntent);
                        break;
                    case R.id.nav_settings:
                        //todo onclicklistener
                        break;
                    case R.id.nav_logout:
                        logOut();
                        break;
                    case R.id.chat:
                        //todo onclicklistener
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setNavigationView(){
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
        // FACEBOOK LOGOUT
        LoginManager.getInstance().logOut();
    }

    private void initViewModel(){
            mViewModelMainActivity = new ViewModelProvider(this).get(ViewModelMainActivity.class);
            mViewModelMainActivity.init();
    }

    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
