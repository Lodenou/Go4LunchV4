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
        fetchNearbyRestaurants();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentListViewBinding.inflate(inflater,container,false);
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

    private void initViewModel(String location){
        mViewModelListView = new ViewModelProvider(this).get(ViewModelListView.class);
        mViewModelListView.init(location);
        mViewModelListView.getNearbyRestaurants().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> restaurants) {

                if (mAdapter != null) {
                    mAdapter.setRestaurant(restaurants);
                }

            }
        });
    }

   private void fetchNearbyRestaurants() {

       if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

       }
       Task<Location> task = getFusedLocation().getLastLocation();
       task.addOnSuccessListener(new OnSuccessListener<Location>() {

           @SuppressLint("CheckResult")
           @Override
           public void onSuccess(Location location) {
               if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                       && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   return;
               }
               if (location != null) {
                   double lat = location.getLatitude();
                   double lng = location.getLongitude();
                   String loc = lat + "," + lng;
                   initViewModel(loc);
                   //fixme pb
                   initRecyclerView();
               }
           }
       });
    }

    private FusedLocationProviderClient getFusedLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient;
    }


}