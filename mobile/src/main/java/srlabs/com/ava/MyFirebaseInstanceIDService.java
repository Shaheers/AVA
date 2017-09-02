package srlabs.com.ava;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by shrey on 14-07-2016.
 **/

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    String ID = "";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        ID = " ID=" + refreshedToken;
        Log.d("Firebase instance ID", ID);
    }

}
