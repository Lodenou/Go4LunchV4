package com.lodenou.go4lunchv4.model;


import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;


    public class Message {

        private String message;
        private Date dateCreated;
        private User userSender;


        public Message() {
        }

        public Message(String message, User userSender, Date dateCreated) {
            this.message = message;
            this.userSender = userSender;
            this.dateCreated = dateCreated;

        }

        // --- GETTERS ---
        public String getMessage() {
            return message;
        }

        @ServerTimestamp
        public Date getDateCreated() {
            return dateCreated;
        }

        public User getUserSender() {
            return userSender;
        }


        // --- SETTERS ---
        public void setMessage(String message) {
            this.message = message;
        }

        public void setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
        }

        public void setUserSender(User userSender) {
            this.userSender = userSender;
        }
    }

