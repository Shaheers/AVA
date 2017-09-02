package srlabs.com.ava;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shrey on 22-11-2016.
 **/
public class HomeActivity extends AppCompatActivity {
    private static long back_pressed;
    Toolbar toolbar;
    DatabaseReference mDatabase;
    ValueEventListener vel;
    ViewPager viewPager;
    private DrawerLayout drawerLayout;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_tab_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.chat_toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setBackgroundResource(R.color.chat_toolbar);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(1);

        initNavigationDrawer();
        init();




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        mDatabase = FirebaseDatabase.getInstance().getReference();
        else {
            Intent intent = new Intent(getApplicationContext(), account_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        vel = mDatabase.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    if (getWindow().getDecorView() != null) toolbar.setSubtitle(null);
                } else {
                    if (getWindow().getDecorView() != null) toolbar.setSubtitle("Connecting...");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
       // stopService(new Intent(this, harate_background_service.class));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();



    }

    /*  @Override
     public void onStop() {
         super.onStop();
         mDatabase.getRoot().child(".info/connected").removeEventListener(vel);
     } */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.getRoot().child(".info/connected").removeEventListener(vel);
    }

    @Override
    public void onPause() {
        super.onPause();

       // startService(new Intent(this, harate_background_service.class));
    }

    /*@Override
        public void onResume() {
            super.onResume();

        }*/




    public void initNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.help:
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        ImageView cover_img =  header.findViewById(R.id.cover_img);
        CircleImageView profile_img =  header.findViewById(R.id.profile_img);
        final TextView name =  header.findViewById(R.id.name);
        TextView email =  header.findViewById(R.id.email);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            name.setText(mFirebaseUser.getDisplayName());
            email.setText( mFirebaseUser.getEmail());
            String path = String.valueOf(getExternalCacheDir()) + "/User_" + mFirebaseUser.getUid();
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Glide.with(this)
                        .load(imgFile)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .error(R.drawable.ic_account_circle_black_36dp)
                                .fallback(R.drawable.ic_account_circle_black_36dp))
                        .into(profile_img);

                //  Glide.with(this)
                //          .load(new File(String.valueOf(getExternalCacheDir())+"/User_" + mFirebaseUser.getUid()))
                //           .diskCacheStrategy(DiskCacheStrategy.NONE)
                //          .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                //          .error(R.drawable.ic_account_circle_black_36dp)
                //          .into(profile_img);
            } else {
                new DownloadProfile().execute(String.valueOf(mFirebaseUser.getPhotoUrl()), "User_" + mFirebaseUser.getUid());
                profile_img.setImageResource(R.drawable.ic_account_circle_black_36dp);
            }

        } else {
            profile_img.setImageResource(R.drawable.ic_account_circle_black_36dp);
            name.setText(R.string.guest_name);
            email.setText(R.string.guest_email);
        }
        cover_img.setImageResource(R.drawable.splash);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), account_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        drawerLayout =  findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void init() {
        String PRIVATE_PREF_VER_STORE = "AVA Version", VERSION_KEY = "version_number";
        SharedPreferences sharedPref = getSharedPreferences(PRIVATE_PREF_VER_STORE, Context.MODE_PRIVATE);
        int currentVersionNumber = 0;


        int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception ignored) {
        }

        if (currentVersionNumber > savedVersionNumber) {
            showWhatsNewDialog();

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt(VERSION_KEY, currentVersionNumber);
            editor.apply();

        } else {



            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && !checkPermission())
                Snackbar.make(findViewById(R.id.viewpager), "Storage permission required for proper working of app!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("FIX", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermission();
                            }
                        }).show();
            else


                Snackbar.make(findViewById(R.id.viewpager), R.string.wc_snack, Snackbar.LENGTH_SHORT).show();

        }
    }




    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode != 5 | grantResults.length == 0 | grantResults[0] != PackageManager.PERMISSION_GRANTED | grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "Permission denied! App may not work properly! Most functionalities are disabled!", Toast.LENGTH_SHORT).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showWhatsNewDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_changelog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);

        builder.setView(view).setTitle("Changelog - Whats New ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragment_home_cs(), getString(R.string.home_c));
        adapter.addFragment(new fragment_home_a(), getString(R.string.home_a));
        adapter.addFragment(new fragment_home_cs(), getString(R.string.home_s));
        viewPager.setAdapter(adapter);
    }



    public void onClick(View v) {
        if (v.getId() == R.id.start_chat) {
      //      ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
      //      Intent intent = new Intent(this, start_chat.class);
      //      if (viewPager.getCurrentItem() == 0) intent.putExtra("cg", true);
      //      else intent.putExtra("cg", false);
     //       startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press Back again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            fragment.setArguments(bundle);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class DownloadProfile extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            profile_download(params[0], params[1]);

            return "Executed!";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
    private void profile_download(String link, String fname) {
        try {
            URL url = new URL(link);

            // checks the file and if it already exist delete
            File file = new File(getExternalCacheDir(), fname);
            if (file.exists())
                file.delete();

            // Open a connection
            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
