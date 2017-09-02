package srlabs.com.ava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by shrey on 30-04-2016.
 **/
public class SplashScreen extends FragmentActivity {
    static boolean fb_call_check = false;

    //  Activity current = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null && !fb_call_check) {
            //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            fb_call_check = true;
        }
        String PRIVATE_PREF_VER_STORE = "AVA Version", VERSION_KEY = "version_number";
        SharedPreferences sharedPref = getSharedPreferences(PRIVATE_PREF_VER_STORE, Context.MODE_PRIVATE);
        if (!sharedPref.contains(VERSION_KEY) | sharedPref.getInt(VERSION_KEY, 0) == 0)
            scheduleRedirectFirstRun();
        else {
            if (mFirebaseUser != null) {
                //  if (current.getIntent().getStringExtra("notification") != null) scheduleRedirectNotif();
                //else
                scheduleRedirectMain();
            } else scheduleRedirectLogin();
        }
    }

    private void scheduleRedirectFirstRun() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                fragment_splash_first_run first_run = new fragment_splash_first_run();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.frame_layout_splashscreen, first_run).commit();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }, GlobalVariables.SPLASH_SCREEN_TIMEOUT);
    }

    private void scheduleRedirectMain() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                runMainActivity();
            }
        }, GlobalVariables.SPLASH_SCREEN_TIMEOUT);
    }

    private void scheduleRedirectLogin() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                runLoginActivity();
            }
        }, GlobalVariables.SPLASH_SCREEN_TIMEOUT);
    }

    /* private void scheduleRedirectNotif() {
         new Handler().postDelayed(new Runnable() {
             public void run() {
                 Intent intent = new Intent(current, notification_activity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                 intent.putExtra("msg_header",current.getIntent().getStringExtra("msg_header"));
                 intent.putExtra("msg_body",current.getIntent().getStringExtra("msg_body"));
                 startActivity(intent);
             }
         }, GlobalVariables.SPLASH_SCREEN_TIMEOUT);
     } */
    public void onClick(View v) {
        if (v.getId() == R.id.fab_firstrun) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_splash_first_run_frag);
            if (viewPager.getAdapter().getCount() == 6) requestPermission();
            else runLoginActivity();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode != 5 | grantResults.length == 0 | grantResults[0] != PackageManager.PERMISSION_GRANTED | grantResults[1] != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getBaseContext(), "Permission denied! App won't work! Please give required permissions!", Toast.LENGTH_LONG).show();
        } else runLoginActivity();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void runMainActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void runLoginActivity() {
        Intent intent = new Intent(this, account_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("call_check", "");
        startActivity(intent);
    }
}