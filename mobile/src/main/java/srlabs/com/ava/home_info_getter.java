package srlabs.com.ava;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


public class home_info_getter {

    private String name;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private home_info_getter() {
    }

    home_info_getter(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}