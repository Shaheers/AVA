package srlabs.com.ava;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


public class ava_getter {

    private String name;
    private String status;
    private Long timestamp;
    private String type;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private ava_getter() {
    }

    ava_getter(String name, String status, Long timestamp, String type) {
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }


    public java.util.Map<String, String> gettimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long gettimestampLong() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

}