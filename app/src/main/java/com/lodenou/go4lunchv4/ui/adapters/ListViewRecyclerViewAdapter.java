package com.lodenou.go4lunchv4.ui.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.Utils;
import com.lodenou.go4lunchv4.ui.activities.DetailActivity;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Result> mRestaurants = new ArrayList<>();
    private Context mContext;

    public ListViewRecyclerViewAdapter(Context context, List<Result> restaurants) {
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
        final Result restaurant = mRestaurants.get(position);


        ImageView restaurantImage = ((ListViewViewHolder) holder).mRestaurantImage;

        // Restaurant address
        ((ListViewViewHolder) holder).mRestaurantAddress.setText(restaurant.getVicinity());
        // Restaurant photo
        if (restaurant.getPhotos() != null) {
            String restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
                    restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;
            if (restaurant.getPhotos() != null && restaurant.getPhotos().size() > 0) {
                Glide.with(mContext).load(restaurantPhoto)
                        .into(restaurantImage);
            }
        }
        // Restaurant name
        ((ListViewViewHolder) holder).mContentView.setText(restaurant.getName());

        // Distance
        ((ListViewViewHolder) holder).mDistance.setText(restaurant.getPlaceId());

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Location locationResto = new Location(LocationManager.GPS_PROVIDER);
                locationResto.setLatitude(restaurant.getGeometry().getLocation().getLat());
                locationResto.setLongitude(restaurant.getGeometry().getLocation().getLng());
                Float distance1 = location.distanceTo(locationResto);
                int i = distance1.intValue();
                ((ListViewViewHolder) holder).mDistance.setText(i + " m");
            }
        });

        // 3 stars version
        if (restaurant.getRating() != null) {
            double ratingstars3 = restaurant.getRating();
            ratingstars3 = Math.round(ratingstars3 * 3 / 5 * 100.0) / 100.0;
            ((ListViewViewHolder) holder).mRatingStars.setRating((float) ratingstars3);
        }

        // Opening hours
        ((ListViewViewHolder) holder).mOpeningHours.setText(Utils.isOpenOrNot(restaurant.getOpeningHours()));

        //Workmates number
        //TODO TO DO AFTER THE WORKMATES PART IS DONE

        // click
        ((ListViewViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("idrestaurant", restaurant.getPlaceId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public void setRestaurant(List<Result> restaurants) {
        this.mRestaurants = restaurants;
        notifyDataSetChanged();
    }

    private static class ListViewViewHolder extends RecyclerView.ViewHolder {

        public final TextView mContentView;
        public final TextView mRestaurantAddress;
        public final TextView mDistance;
        public final ImageView mRestaurantImage;
        public final RatingBar mRatingStars;
        public final TextView mOpeningHours;


        public ListViewViewHolder(@NonNull View itemView) {
            super(itemView);


            mContentView = (TextView) itemView.findViewById(R.id.restaurant_name);
            mRestaurantAddress = itemView.findViewById(R.id.restaurant_address);
            mDistance = itemView.findViewById(R.id.distance);
            mRestaurantImage = itemView.findViewById(R.id.restaurant_image);
            mRatingStars = itemView.findViewById(R.id.rating_stars);
            mOpeningHours = itemView.findViewById(R.id.opening_hours);

        }


    }
}
