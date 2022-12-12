package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.lodenou.go4lunchv4.databinding.FragmentListViewBinding;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.adapters.ListViewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    FragmentListViewBinding mBinding;
    ListViewRecyclerViewAdapter mAdapter;
    ViewModelListView mViewModelListView;




    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mBinding = FragmentListViewBinding.inflate(inflater,container,false);
        initViewModel(getPermission(),getTask());
        initRecyclerView();
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
        mViewModelListView = new ViewModelProvider(this).get(ViewModelListView.class);
        mViewModelListView.init(permission, task);
        mViewModelListView.getLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String loc = lat + "," + lng;
                mViewModelListView.fetchNearbyRestaurants(loc);
            }
        });


            mViewModelListView.getNearbyRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Result>>() {
                @Override
                public void onChanged(List<Result> restaurants) {

                    if (mAdapter != null) {
                        mAdapter.setRestaurant(restaurants);
                    }

                }
            });
    }

    // Can't be in the repository because of the context requirement
   private Boolean getPermission() {

       Boolean isPermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

       Boolean isPermissionOk = !(isPermission);
       return isPermissionOk;
   }

    private Task getTask() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient.getLastLocation();
    }


}