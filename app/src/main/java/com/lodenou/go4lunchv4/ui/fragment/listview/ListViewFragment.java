package com.lodenou.go4lunchv4.ui.fragment.listview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.FragmentListViewBinding;
import com.lodenou.go4lunchv4.model.Restaurant;
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
        initViewModel();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentListViewBinding.inflate(inflater,container,false);
        initRecyclerView();
        return mBinding.getRoot();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("123", "Onresume in listview");
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        mAdapter = new ListViewRecyclerViewAdapter(getContext(), mViewModelListView.getRestaurants().getValue());
        mBinding.recyclerViewListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.recyclerViewListView.setAdapter(this.mAdapter);
    }

    private void initViewModel(){
        mViewModelListView = new ViewModelProvider(this).get(ViewModelListView.class);
        mViewModelListView.init();
        mViewModelListView.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}