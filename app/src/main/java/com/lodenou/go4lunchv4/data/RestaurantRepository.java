package com.lodenou.go4lunchv4.data;

import androidx.lifecycle.MutableLiveData;
import com.lodenou.go4lunchv4.model.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private ArrayList<Restaurant> dataset = new ArrayList<>();
    MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();

    public static RestaurantRepository getInstance(){
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Restaurant>> getRestaurants(){
        dataset.clear();
        setRestaurants();
        data.setValue(dataset);
        return data;
    }

    private void setRestaurants() {
        dataset.add(new Restaurant("0", "12 rue blabla", "bloblo"
                ,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        dataset.add(new Restaurant("1", "12 rue blabl7777", "blo442blo"
                ,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        dataset.add(new Restaurant("2", "12 rue blabla5555", "bl2424oblo"
                ,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        dataset.add(new Restaurant("3", "12 rue blabla222", "blo422424blo"
                ,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        dataset.add(new Restaurant("4", "12 rue blabla111", "bl2424oblo"
                ,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
    }


}
