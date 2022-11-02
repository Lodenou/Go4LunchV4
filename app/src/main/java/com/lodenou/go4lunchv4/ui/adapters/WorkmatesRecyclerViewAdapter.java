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

        //FIXME
        String imageUrl = mUser.getUserAvatarUrl();
        Glide.with(this.mContext)
                .load("https://gumlet.assettype.com/dtnext%2F2022-03%2F107be68e-8db3-44e1-ac14-8cd095ad1ee5%2FUntitled_42_.jpg?auto=format%2Ccompress&fit=max&format=webp&w=768&dpr=1.1")
                .circleCrop()
                .dontAnimate()
                .placeholder(R.drawable.ic_marker_green)
                .into(userImage);

        // User name
        userName.setText(mUser.getUserName());

        // User restaurant
        userRestaurant.setText(" Restaurant numéro" +mUser.getUid());
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


