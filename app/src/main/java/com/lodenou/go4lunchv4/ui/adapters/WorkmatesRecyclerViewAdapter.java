package com.lodenou.go4lunchv4.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.collection.LLRBNode;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.ui.fragment.workmates.WorkmatesFragment;

import java.util.List;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> mUsers;
    Context mContext;

    public WorkmatesRecyclerViewAdapter(Context context,List<User> users) {
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
        if (mUser.getRestaurantChosen() != null && mUser.getRestaurantChosen() != "") {
            userRestaurant.setText(" mange à " + mUser.getRestaurantChosen());
            //TODO RENDRE CLICKABLE ET ENVOYER VERS LA PAGE DETAIL DU RESTAURANT
        }
        else {
            userRestaurant.setText(" n'a pas encore choisi de restaurant");
            userRestaurant.setTextColor(Color.rgb(138, 133, 132));
            userRestaurant.setTypeface(null, Typeface.ITALIC);
            userName.setTextColor(Color.rgb(138, 133, 132));
            userName.setTypeface(null, Typeface.ITALIC);

        }
    }

    public void setUsersWorkmates(List<User> users){
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


