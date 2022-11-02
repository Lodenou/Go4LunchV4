package com.lodenou.go4lunchv4.data;

import androidx.lifecycle.MutableLiveData;

import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static UserRepository instance;
    private ArrayList<User> dataset = new ArrayList<>();
    MutableLiveData<List<User>> data = new MutableLiveData<>();


    public static UserRepository getInstance(){
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public MutableLiveData<List<User>> getUsers(){
        dataset.clear();
        setUsers();
        data.setValue(dataset);
        return data;
    }

    private void setUsers() {
        dataset.add(new User("0", "Pablo", "https://i.picsum.photos/id/783/200/300.jpg?hmac=dWaIjCNc0MrS2mpEkUX5DxYsTp7vfpipFOlnODFMmfo"
                ,""));
        dataset.add(new User("1", "Pablo2", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
                ,""));
        dataset.add(new User("2", "Pablo3", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
                ,""));
        dataset.add(new User("3", "Pablo4", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
                ,""));
        dataset.add(new User("4", "Pablo5", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"
                ,""));
    }
}
