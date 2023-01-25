package com.lodenou.go4lunchv4.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.User;

import java.util.List;

public class DetailActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<User> mUsers;
    Context mContext;

    public DetailActivityAdapter(Context context,List<User> users) {
        this.mUsers = users;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_detail, parent, false);
        // Return a new holder instance
        DetailViewHolder detailViewHolder = new DetailViewHolder(view);
        return detailViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final User mUser = mUsers.get(position);

        ImageView userImage = ((DetailActivityAdapter.DetailViewHolder) holder).mAvatarUser;
        TextView userName = ((DetailActivityAdapter.DetailViewHolder) holder).mUsername;
        TextView userRestaurant = ((DetailActivityAdapter.DetailViewHolder) holder).mUserRestaurant;

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
        userRestaurant.setText(R.string.eats_here);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void setUsersDetail(List<User> users){
        this.mUsers = users;
        notifyDataSetChanged();
    }


    private static class DetailViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mAvatarUser;
        public final TextView mUsername;
        public final TextView mUserRestaurant;


        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarUser = itemView.findViewById(R.id.user_avatar);
            mUsername = itemView.findViewById(R.id.user_name);
            mUserRestaurant = itemView.findViewById(R.id.user_restaurant);
        }
    }
}
