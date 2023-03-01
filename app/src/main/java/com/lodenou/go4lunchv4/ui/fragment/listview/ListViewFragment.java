package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.FragmentListViewBinding;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.activities.viewmodels.ViewModelMainActivity;
import com.lodenou.go4lunchv4.ui.adapters.ListViewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener{

    FragmentListViewBinding mBinding;
    ListViewRecyclerViewAdapter mAdapter;
    ViewModelListView mViewModelListView;
    ViewModelMainActivity mViewModelMainActivity;
    List<Restaurant> restaurantList = new ArrayList<>();

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mBinding = FragmentListViewBinding.inflate(inflater,container,false);
        initRecyclerView();
        initViewModel(getPermission(),getTask());
        return mBinding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }



    private void initRecyclerView(){
        mAdapter = new ListViewRecyclerViewAdapter(getContext(), new ArrayList<>());
        mBinding.recyclerViewListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.recyclerViewListView.setAdapter(this.mAdapter);
    }

    private void initViewModel(Boolean permission, Task task){
        initViewModelMain();
        mViewModelListView = new ViewModelProvider(this).get(ViewModelListView.class);
        mViewModelMainActivity.fetchAllRestaurants(task, permission, getContext());

        mViewModelMainActivity.getAllRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (restaurants != null) {
                    mAdapter.setRestaurant(restaurants);
                    restaurantList = restaurants;
                }
            }
        });
    }

    private void initViewModelMain(){
        mViewModelMainActivity = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelMainActivity.class);
        mViewModelMainActivity.init();
    }

    // Can't be in the repository because of the context requirement
   private Boolean getPermission() {

       Boolean isPermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
               PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
               PackageManager.PERMISSION_GRANTED;

       Boolean isPermissionOk = !(isPermission);
       return isPermissionOk;
   }

    private Task getTask() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient.getLastLocation();
    }




    @Override
    public void onResume() {
        super.onResume();
        initViewModel(getPermission(),getTask());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.searchview_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ArrayList<Restaurant> restaurantToFetch = new ArrayList<>();
        if (s.length() > 2) for (int i = 0; i <= restaurantList.size() -1; i++) {
                String restaurantName = Objects.requireNonNull(restaurantList).get(i).getName().toLowerCase(Locale.ROOT);
                if (restaurantName.contains(s.toLowerCase(Locale.ROOT))){
                    restaurantToFetch.add(restaurantList.get(i));
                }
                mAdapter.setRestaurant(restaurantToFetch);
            }
        else {
            mAdapter.setRestaurant(restaurantList);
        }
        return true;
    }
}