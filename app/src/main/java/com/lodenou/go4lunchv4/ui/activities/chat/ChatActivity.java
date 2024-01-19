package com.lodenou.go4lunchv4.ui.activities.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.ActivityChatBinding;
import com.lodenou.go4lunchv4.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatActivity is responsible for managing the chat interface and user interactions.
 */
public class ChatActivity extends AppCompatActivity  {

    ActivityChatBinding mBinding;
    TextInputEditText editTextMessage;
    private ChatAdapter mChatAdapter;
    @Nullable
    ViewModelChat mViewModelChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        setBackButton();
        configureRecyclerView();
        initViewModel();
        onClickSendMessages();
    }

    /**
     * Set up the back button to finish the activity when clicked.
     */
    private void setBackButton(){
        mBinding.activityChatBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * Handle the logic for sending messages when the send button is clicked.
     */
    public void onClickSendMessages() {
        Button mButton = findViewById(R.id.activity_chat_send_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextMessage = mBinding.activityChatMessageEditText;
                // 1 - Check if text field is not empty and current user properly downloaded from Firestore
                if (!TextUtils.isEmpty(editTextMessage.getText()) &&  getCurrentUser() != null) {
                    // 2 - Create a new Message to Firestore
                    mViewModelChat.createNewMessage(editTextMessage.getText().toString());
                    getListOfMessages();
                    // 3 - Reset text field
                    editTextMessage.setText("");
                }
            }
        });
    }

    /**
     * Get the current Firebase user.
     *
     * @return The current Firebase user.
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Configure the RecyclerView for displaying chat messages.
     */
    private void configureRecyclerView() {
        //Track current chat name
        //Configure Adapter & RecyclerView
        this.mChatAdapter = new ChatAdapter(this);
        mBinding.activityChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.activityChatRecyclerView.setAdapter(this.mChatAdapter);
    }

    /**
     * Initialize the ViewModel for managing chat data.
     */
    private void initViewModel(){
        mViewModelChat = new ViewModelProvider(this).get(ViewModelChat.class);
        mViewModelChat.init();
        getListOfMessages();
    }

    /**
     * Get the list of chat messages and update the UI accordingly.
     */
    private void getListOfMessages(){
        mViewModelChat.getAllMessageForChat().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                mChatAdapter.updateMessages(messages);
                mBinding.activityChatRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() -1);
            }
        });
    }
}
