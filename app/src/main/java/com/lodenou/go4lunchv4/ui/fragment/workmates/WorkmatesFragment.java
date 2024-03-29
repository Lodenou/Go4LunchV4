package com.lodenou.go4lunchv4.ui.fragment.workmates;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lodenou.go4lunchv4.databinding.FragmentWorkmatesBinding;
import com.lodenou.go4lunchv4.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays a list of workmates.
 */
public class WorkmatesFragment extends Fragment {



    FragmentWorkmatesBinding mBinding;
    WorkmatesRecyclerViewAdapter mAdapter;
    ViewModelWorkmates mViewModelWorkmates;

    /**
     * Default constructor for WorkmatesFragment.
     */
    public WorkmatesFragment() {
        // Required empty public constructor
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

    /**
     * Initializes the RecyclerView for displaying workmates.
     */
    private void initRecyclerView(){
        mAdapter = new WorkmatesRecyclerViewAdapter(getContext(), new ArrayList<>());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.recyclerView.setAdapter(this.mAdapter);
    }

    /**
     * Initializes the ViewModel for retrieving workmate data.
     */
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
        initViewModel();
        super.onResume();
    }
}