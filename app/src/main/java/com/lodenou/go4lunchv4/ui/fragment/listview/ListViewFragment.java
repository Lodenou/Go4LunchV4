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
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.adapters.ListViewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener{

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
        setHasOptionsMenu(true);
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

            mViewModelListView.getRestaurants(permission, task).observe(getViewLifecycleOwner(),
                    new Observer<List<Result>>() {
                        @Override
                        public void onChanged(List<Result> results) {
                            mAdapter.setRestaurant(results);
                        }
                    });
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
        ArrayList<Result> restaurantToFetch = new ArrayList<>();
        List<Result> resultList = mViewModelListView.getNearbyRestaurants().getValue();
        if (s.length() > 2) {
            for (int i = 0; i <= Objects.requireNonNull(mViewModelListView.getNearbyRestaurants().getValue()).size() -1; i++) {
                String restaurantName = Objects.requireNonNull(resultList).get(i).getName();
                if (restaurantName.contains(s)){
                    restaurantToFetch.add(resultList.get(i));
                }
                mAdapter.setRestaurant(restaurantToFetch);
            }
        }
        else {
            mAdapter.setRestaurant(resultList);
        }
        return true;
    }
}