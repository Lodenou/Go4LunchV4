package com.lodenou.go4lunchv4.ui.fragment.workmates;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.FragmentWorkmatesBinding;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.ui.adapters.WorkmatesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {



    FragmentWorkmatesBinding mBinding;
    WorkmatesRecyclerViewAdapter mAdapter;
    ViewModelWorkmates mViewModelWorkmates;
    Boolean bool = true;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        initRecyclerView();
        return mBinding.getRoot();
    }

    private void initRecyclerView(){
        mAdapter = new WorkmatesRecyclerViewAdapter(getContext(), new ArrayList<>());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.recyclerView.setAdapter(this.mAdapter);
    }

    private void initViewModel() {
        mViewModelWorkmates = new ViewModelProvider(this).get(ViewModelWorkmates.class);
        mViewModelWorkmates.init();
        mViewModelWorkmates.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter.setUsersWorkmates(users);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


    @Override
    public void onResume() {
        Log.d("123","OnResume");
        initViewModel();

        super.onResume();
    }
}