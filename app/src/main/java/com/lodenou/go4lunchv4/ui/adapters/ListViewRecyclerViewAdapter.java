package com.lodenou.go4lunchv4.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.Restaurant;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private Context mContext;

    public ListViewRecyclerViewAdapter(Context context, List<Restaurant> restaurants) {
        mRestaurants = restaurants;
        mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_view, parent, false);
        return new ListViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //TODO CHANGE THE TYPE RESTAURANT TO RESULT WHEN API IS USED
        final Restaurant restaurant = mRestaurants.get(position);


        ImageView restaurantImage = ((ListViewViewHolder) holder).mRestaurantImage;

        // address
        ((ListViewViewHolder) holder).mRestaurantAddress.setText(restaurant.getRestaurantAddress());
        // image
        Glide.with(mContext).load(restaurant.getRestaurantImageUrl())
                .into(restaurantImage);
        // name
        ((ListViewViewHolder) holder).mContentView.setText(restaurant.getRestaurantName());
        ((ListViewViewHolder) holder).mDistance.setText(restaurant.getRestaurantId());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    private static class ListViewViewHolder extends RecyclerView.ViewHolder {

        public final TextView mContentView;
        public final TextView mRestaurantAddress;
        public final TextView mDistance;
        public final ImageView mRestaurantImage;


        public ListViewViewHolder(@NonNull View itemView) {
            super(itemView);


            mContentView = (TextView) itemView.findViewById(R.id.restaurant_name);
            mRestaurantAddress = itemView.findViewById(R.id.restaurant_address);
            mDistance = itemView.findViewById(R.id.distance);
            mRestaurantImage = itemView.findViewById(R.id.restaurant_image);

        }
    }
}
