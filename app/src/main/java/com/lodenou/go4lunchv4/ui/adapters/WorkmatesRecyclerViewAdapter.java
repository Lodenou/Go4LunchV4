package com.lodenou.go4lunchv4.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.ui.activities.DetailActivity;
import com.lodenou.go4lunchv4.ui.activities.MainActivity;

import java.util.List;
import java.util.Objects;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> mUsers;
    Context mContext;

    public WorkmatesRecyclerViewAdapter(Context context, List<User> users) {
        this.mUsers = users;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        // Return a new holder instance
        WorkmatesViewHolder viewHolder = new WorkmatesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final User mUser = mUsers.get(position);

        ImageView userImage = ((WorkmatesViewHolder) holder).mAvatarUser;
        TextView userName = ((WorkmatesViewHolder) holder).mUsername;
        TextView userRestaurant = ((WorkmatesViewHolder) holder).mUserRestaurant;

        // User avatar
        String imageUrl = mUser.getUserAvatarUrl();
        Glide.with(this.mContext)
                .load(imageUrl)
                .circleCrop()
                .dontAnimate()
                .into(userImage);

        // User name
        userName.setText(mUser.getUserName());

        // User restaurant

        // click on workmate
        ((WorkmatesViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.getRestaurantChosenId() != null && mUser.getRestaurantChosenId() != "") {
                    userRestaurant.setText(String.format("%s%s", mContext.getString(R.string.eats_at),
                            mUser.getRestaurantChosenName()));
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("idrestaurant", mUser.getRestaurantChosenId());
                    context.startActivity(intent);

                } else {
                    userRestaurant.setText(R.string.has_not_yet_chosen);
                    userRestaurant.setTextColor(Color.rgb(138, 133, 132));
                    userRestaurant.setTypeface(null, Typeface.ITALIC);
                    userName.setTextColor(Color.rgb(138, 133, 132));
                    userName.setTypeface(null, Typeface.ITALIC);
                }

            }
        });

        // Set the text without clicking on item
        if (!Objects.equals(mUser.getRestaurantChosenName(), "") && mUser.getRestaurantChosenName() != null) {
            userRestaurant.setText(String.format("%s%s", mContext.getString(R.string.eats_at), mUser.getRestaurantChosenName()));
            userRestaurant.setTextColor(Color.rgb(0, 0, 0));
            userRestaurant.setTypeface(null, Typeface.NORMAL);
            userName.setTextColor(Color.rgb(0, 0, 0));
            userName.setTypeface(null, Typeface.NORMAL);
        } else {
            userRestaurant.setText(R.string.has_not_yet_chosen);
            userRestaurant.setTextColor(Color.rgb(138, 133, 132));
            userRestaurant.setTypeface(null, Typeface.ITALIC);
            userName.setTextColor(Color.rgb(138, 133, 132));
            userName.setTypeface(null, Typeface.ITALIC);
        }
    }


    public void setUsersWorkmates(List<User> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mAvatarUser;
        public final TextView mUsername;
        public final TextView mUserRestaurant;

        public WorkmatesViewHolder(View itemView) {
            super(itemView);
            mAvatarUser = itemView.findViewById(R.id.user_avatar);
            mUsername = itemView.findViewById(R.id.user_name);
            mUserRestaurant = itemView.findViewById(R.id.user_restaurant);
        }

    }
}


