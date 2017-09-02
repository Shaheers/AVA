package srlabs.com.ava;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shrey on 30-09-2016.
 **/
public class account_activity extends AppCompatActivity {
    DatabaseReference ref;
    fragment_account fragment_account = new fragment_account();
    Bundle bundle = new Bundle();
    private Uri profilepicUri;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_layout);
        fragment_account.setArguments(bundle);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (null == getIntent() || null == getIntent().getStringExtra("call_check"))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        ref = mDatabase.getReference();
        ref.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.getDisplayName() != null) {
                        myaccountView();

                        File imgFile = new File(String.valueOf(getExternalCacheDir()) + "/User_" + user.getUid());
                        if (!imgFile.exists() && user.getPhotoUrl() != null) {
                            new DownloadProfile().execute(String.valueOf(user.getPhotoUrl()), "User_" + user.getUid());
                        }

                    } else cnnmView();
                } else {
                    loginView();
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void myaccountView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "myaccount");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void progressView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "progress");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void loginView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "login");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void cemailView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "cemail");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void cpassView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "cpass");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void cnnmView() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(fragment_account).commit();
        bundle.putString("layout", "cnnm");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.saa_framelayout, fragment_account).commit();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.profile_img) {
            // Determine Uri of camera image to save.

            final File root = new File(String.valueOf(getExternalCacheDir()));
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String fname;

            fname = "/User_" + user.getUid();

            final File sdImageMainDirectory = new File(root, fname);
            profilepicUri = Uri.fromFile(sdImageMainDirectory);

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, profilepicUri);
                cameraIntents.add(intent);
            }

            // Filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, 53);


        } else if (v.getId() == R.id.nnm_view) cnnmView();

        else if (v.getId() == R.id.change_nnm_btn) {
            android.support.design.widget.TextInputEditText nickname_edittext = findViewById(R.id.nickname_edittext);
            FirebaseUser user = mAuth.getCurrentUser();

            if (nickname_edittext.getText().toString().trim().length() > 0) {
                progressView();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nickname_edittext.getText().toString())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Nickname updated successfully!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                }
                                myaccountView();
                            }
                        });

            } else nickname_edittext.setError("Enter a valid name");

        } else if (v.getId() == R.id.email_view) cemailView();

        else if (v.getId() == R.id.change_email_btn) {
            final android.support.design.widget.TextInputEditText email_edittext = findViewById(R.id.email_edittext);
            android.support.design.widget.TextInputEditText password_edittext = findViewById(R.id.password_edittext);
            final android.support.design.widget.TextInputEditText new_email_edittext = findViewById(R.id.new_email_edittext);

            if (email_edittext.getText().toString().trim().length() > 0 && email_edittext.getText().toString().contains("@") && new_email_edittext.getText().toString().trim().length() > 0 && new_email_edittext.getText().toString().contains("@") && password_edittext.getText().toString().trim().length() > 5) {

                progressView();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email_edittext.getText().toString(), password_edittext.getText().toString());

                assert user != null;
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.updateEmail(email_edittext.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "E-mail updated successfully!", Toast.LENGTH_SHORT).show();

                                                } else
                                                    Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                                myaccountView();
                                            }
                                        });
                            }
                        });
            } else {
                if (email_edittext.getText().toString().trim().length() == 0 || !email_edittext.getText().toString().contains("@"))
                    email_edittext.setError("Enter valid E-mail");
                if (new_email_edittext.getText().toString().trim().length() == 0 || !new_email_edittext.getText().toString().contains("@"))
                    new_email_edittext.setError("Enter valid E-mail");
                if (password_edittext.getText().toString().trim().length() < 6)
                    password_edittext.setError("Enter correct password");
            }
        } else if (v.getId() == R.id.pass_edit) cpassView();

        else if (v.getId() == R.id.change_pass_btn) {
            android.support.design.widget.TextInputEditText email_edittext = findViewById(R.id.email_edittext);
            android.support.design.widget.TextInputEditText password_edittext = findViewById(R.id.password_edittext);
            final android.support.design.widget.TextInputEditText new_password_edittext = findViewById(R.id.new_password_edittext);

            if (email_edittext.getText().toString().trim().length() > 0 && email_edittext.getText().toString().contains("@") && password_edittext.getText().toString().trim().length() > 5 && new_password_edittext.getText().toString().trim().length() > 5) {

                progressView();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email_edittext.getText().toString(), password_edittext.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.updatePassword(new_password_edittext.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                                myaccountView();
                                            }
                                        });
                            }
                        });
            } else {
                if (email_edittext.getText().toString().trim().length() == 0 || !email_edittext.getText().toString().contains("@"))
                    email_edittext.setError("Enter valid E-mail");
                if (password_edittext.getText().toString().trim().length() < 6)
                    password_edittext.setError("Enter correct password");
                if (new_password_edittext.getText().toString().trim().length() < 6)
                    new_password_edittext.setError("Password should be at least of 6 chars");
            }
        } else if (v.getId() == R.id.logout_btn) {
            Button btn = (Button) v;
            if (btn.getText().toString().equals(getResources().getString(R.string.logout))) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            } else if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null)
                runMainActivity();
            else
                Toast.makeText(getApplicationContext(), "Update your profile pic!", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.cont_btn) {
            final android.support.design.widget.TextInputEditText email_edittext = findViewById(R.id.email_edittext);
            final android.support.design.widget.TextInputEditText password_edittext = findViewById(R.id.password_edittext);

            if (email_edittext.getText().toString().trim().length() > 0 && email_edittext.getText().toString().contains("@") && password_edittext.getText().toString().trim().length() > 5) {


                progressView();

                mAuth.signInWithEmailAndPassword(email_edittext.getText().toString(), password_edittext.getText().toString())
                        .addOnCompleteListener(account_activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    mAuth.createUserWithEmailAndPassword(email_edittext.getText().toString(), password_edittext.getText().toString())
                                            .addOnCompleteListener(account_activity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                                        loginView();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {

                if (email_edittext.getText().toString().trim().length() == 0 || !email_edittext.getText().toString().contains("@"))
                    email_edittext.setError("Enter valid E-mail");
                if (password_edittext.getText().toString().trim().length() < 6)
                    password_edittext.setError("Password must be at least of 6 chars");

            }

        } else if (v.getId() == R.id.forgot_p_btn) {
            android.support.design.widget.TextInputEditText email_edittext = findViewById(R.id.email_edittext);
            if (email_edittext.getText().toString().trim().length() > 0 && email_edittext.getText().toString().contains("@")) {

                progressView();

                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(email_edittext.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Password reset E-mail has been sent to you successfully!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                }
                                loginView();
                            }
                        });
            } else {
                if (email_edittext.getText().toString().trim().length() == 0 || !email_edittext.getText().toString().contains("@"))
                    email_edittext.setError("Enter valid E-mail ID");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 53) {
                final boolean isCamera;
                if (data == null || (data.getData() == null && data.getClipData() == null)) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }

                final Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = profilepicUri;
                } else {
                    selectedImageUri = data.getData();
                }

                try {
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (selectedImageUri == profilepicUri)
                        cropIntent.setDataAndType(FileProvider.getUriForFile(this, "srlabs.com.ava.fileProvider", new File(selectedImageUri.getPath())), "image/*");
                    else cropIntent.setDataAndType(selectedImageUri, "image/*");
                    cropIntent.putExtra("crop", "true");

                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 200);
                    cropIntent.putExtra("outputY", 200);

                    cropIntent.putExtra("return-data", true);
                    startActivityForResult(cropIntent, 96);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No app to crop image!", Toast.LENGTH_SHORT).show();
                    profilePicUpdate(selectedImageUri);
                }

            } else if (requestCode == 96) {
                //Bundle extras = data.getExtras();
                // Bitmap bmp = extras.getParcelable("data");
                //
                Bitmap bmp = null;
                if (null != data && null != data.getExtras()) {
                    Bundle extras = data.getExtras();
                    if (extras.getParcelable("data") != null) bmp = extras.getParcelable("data");
                }
                //
                if (bmp != null) {
                    try {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String fname;
                        fname = "User_" + user.getUid();

                        File f = new File(getExternalCacheDir(), fname);
                        if (!f.exists()) f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                        fo.close();
                        profilePicUpdate(Uri.fromFile(f));

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to save crop image! Try again", Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Crop failed! Try again", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Failed to select the pic!", Toast.LENGTH_LONG).show();
        }
    }

    private void profilePicUpdate(Uri imageURI) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-ava-a5c57.appspot.com/User_Profile_Images/");

        Toast.makeText(getApplicationContext(), "Uploading selected pic. Please wait...", Toast.LENGTH_LONG).show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String ref = "/User_" + user.getUid();

        StorageReference Ref = storageRef.child(ref);
        UploadTask uploadTask = Ref.putFile(imageURI);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Profile pic upload failed! Try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(taskSnapshot.getDownloadUrl())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Profile pic updated successfully!", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "There was an error! Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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

    public void injectcounter() {
        String PRIVATE_PREF_VER_STORE = "AVA Version", VERSION_KEY = "version_number";
        int currentVersionNumber = 0;
        SharedPreferences sharedPref = getSharedPreferences(PRIVATE_PREF_VER_STORE, Context.MODE_PRIVATE);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception ignored) {
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(VERSION_KEY, currentVersionNumber);
        editor.apply();

    }

    public void runMainActivity() {
        if (null != getIntent() && null != getIntent().getStringExtra("call_check")) {
            SharedPreferences sharedPref = getSharedPreferences("AVA Version", Context.MODE_PRIVATE);
            String VERSION_KEY = "version_number";
            if (!sharedPref.contains(VERSION_KEY) || sharedPref.getInt(VERSION_KEY, 0) == 0)
                injectcounter();
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*   private void setupShortcuts() {
          if (android.os.Build.VERSION.SDK_INT >= 25) {

             ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
              ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "EC")
                      .setShortLabel(getResources().getString(R.string.chat_activity_header))
                      .setLongLabel(getResources().getString(R.string.chat_activity_header))
                      .setIcon(Icon.createWithResource(this, R.drawable.eee_central_icon))
                      .setIntent(new Intent("com.sr.eeecompanion.eeecentral"))
                      .build();
              ShortcutInfo shortcut1 = new ShortcutInfo.Builder(this, "TT")
                      .setShortLabel(getResources().getString(R.string.timetable_header))
                      .setLongLabel(getResources().getString(R.string.timetable_header))
                      .setIcon(Icon.createWithResource(this, R.drawable.time_table_icon))
                      .setIntent(new Intent("com.sr.eeecompanion.timetable"))
                      .build();
              ShortcutInfo shortcut2 = new ShortcutInfo.Builder(this, "ES")
                      .setShortLabel(getResources().getString(R.string.syllabus_activity_header))
                      .setLongLabel(getResources().getString(R.string.syllabus_activity_header))
                      .setIcon(Icon.createWithResource(this, R.drawable.syllabus_icon))
                      .setIntent(new Intent("com.sr.eeecompanion.syllabus"))
                      .build();
              shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut, shortcut1, shortcut2));
          }
      }
  */


    public void onBackPressed() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (findViewById(R.id.change_pass_btn) != null || findViewById(R.id.change_email_btn) != null || findViewById(R.id.change_nnm_btn) != null)
            myaccountView();

        else if (findViewById(R.id.progress_saa) != null) {
            if (user != null) myaccountView();
            else loginView();
        } else {
            if (user != null && ((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon() != null)
                runMainActivity();
            else finish();
        }
    }

    public static class fragment_account extends Fragment {
        String layout = "";
        int colors[] = new int[2];

        public fragment_account() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) layout = getArguments().getString("layout");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment


            if (layout != null && layout.equals("myaccount"))
                return inflater.inflate(R.layout.fragment_myaccount, container, false);
            else if (layout != null && layout.equals("login"))
                return inflater.inflate(R.layout.fragment_login, container, false);
            else if (layout != null && layout.equals("cemail"))
                return inflater.inflate(R.layout.fragment_change_email, container, false);
            else if (layout != null && layout.equals("cpass"))
                return inflater.inflate(R.layout.fragment_change_pass, container, false);
            else if (layout != null && layout.equals("cnnm"))
                return inflater.inflate(R.layout.fragment_change_name, container, false);
            else return inflater.inflate(R.layout.fragment_progress, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            Glide.with(this)
                    .load(R.drawable.splash)
                    .listener(GlidePalette.with(String.valueOf(R.drawable.splash))
                            .intoCallBack(new GlidePalette.CallBack() {
                                @Override
                                public void onPaletteLoaded(Palette p) {
                                    int c;

                                    if (p.getVibrantColor(0) != 0) c = p.getVibrantColor(0);
                                    else if (p.getLightVibrantColor(0) != 0)
                                        c = p.getLightVibrantColor(0);
                                    else if (p.getDarkVibrantColor(0) != 0)
                                        c = p.getDarkVibrantColor(0);
                                    else if (p.getMutedColor(0) != 0) c = p.getMutedColor(0);
                                    else if (p.getLightMutedColor(0) != 0)
                                        c = p.getLightMutedColor(0);
                                    else if (p.getDarkMutedColor(0) != 0)
                                        c = p.getDarkMutedColor(0);
                                    else
                                        c = ContextCompat.getColor(getContext(), R.color.chat_toolbar);

                                    if (isColorDark(c)) {

                                        colors[1] = ContextCompat.getColor(getContext(), R.color.white);
                                    } else {

                                        colors[1] = ContextCompat.getColor(getContext(), R.color.black);
                                    }
                                }
                            }))
                    .into((ImageView) getActivity().findViewById(R.id.cover_img));

            if (layout.equals("myaccount")) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();

                getActivity().findViewById(R.id.info).setVisibility(View.GONE);

                final CollapsingToolbarLayout collapsingToolbar = getActivity().findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.transparent));

                CircleImageView profile_img = getActivity().findViewById(R.id.profile_img);
                profile_img.setVisibility(View.VISIBLE);
                CircleImageView dp = getActivity().findViewById(R.id.dp);
                dp.setVisibility(View.VISIBLE);

                //    Glide.with(getActivity())
                //           .load(new File(String.valueOf(getActivity().getExternalCacheDir())+"/User_" + user.getUid()))
                //           .diskCacheStrategy(DiskCacheStrategy.NONE)
                //          .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))

                //           .error(R.drawable.ic_account_circle_black_36dp)
                //          .into(profile_img);

                File f = new File(String.valueOf(getActivity().getExternalCacheDir()) + "/User_" + user.getUid());
                Glide.with(this)
                        .load(f)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .error(R.drawable.ic_account_circle_black_36dp)
                                .fallback(R.drawable.ic_account_circle_black_36dp)
                        )
                        .into(dp);
                Glide.with(getActivity())
                        .load(f)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .error(R.drawable.ic_account_circle_black_36dp)
                                .fallback(R.drawable.ic_account_circle_black_36dp)
                        )
                        .listener(GlidePalette.with(f.getAbsolutePath())
                                .intoCallBack(new GlidePalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(Palette p) {
                                        //specific task
                                        int c;

                                        if (p.getVibrantColor(0) != 0) c = p.getVibrantColor(0);
                                        else if (p.getLightVibrantColor(0) != 0)
                                            c = p.getLightVibrantColor(0);
                                        else if (p.getDarkVibrantColor(0) != 0)
                                            c = p.getDarkVibrantColor(0);
                                        else if (p.getMutedColor(0) != 0) c = p.getMutedColor(0);
                                        else if (p.getLightMutedColor(0) != 0)
                                            c = p.getLightMutedColor(0);
                                        else if (p.getDarkMutedColor(0) != 0)
                                            c = p.getDarkMutedColor(0);
                                        else
                                            c = ContextCompat.getColor(getContext(), R.color.chat_toolbar);

                                        collapsingToolbar.setContentScrimColor(c);

                                        if (isColorDark(c)) {
                                            collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                            colors[0] = ContextCompat.getColor(getContext(), R.color.white);
                                        } else {
                                            collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getContext(), R.color.black));
                                            colors[0] = ContextCompat.getColor(getContext(), R.color.black);
                                        }

                                    }
                                }))
                        .into(profile_img);

                TextView user_text = view.findViewById(R.id.user_text);
                if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
                    user_text.setText(user.getDisplayName());
                    collapsingToolbar.setTitle(user.getDisplayName().split(" ")[0]);
                } else {
                    user_text.setText(getResources().getString(R.string.usr));
                    collapsingToolbar.setTitle(getResources().getString(R.string.usr));
                }

                TextView email_text = view.findViewById(R.id.email_text);
                email_text.setText(user.getEmail());

                if (toolbar.getNavigationIcon() != null) {
                    Button btn = view.findViewById(R.id.logout_btn);
                    btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.logout));
                    btn.setText(R.string.logout);
                } else {
                    view.findViewById(R.id.chpass_view).setVisibility(View.GONE);
                    Button btn = view.findViewById(R.id.logout_btn);
                    btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.login));
                    btn.setText(R.string.cont);
                }
                AppBarLayout appBar = getActivity().findViewById(R.id.appbar);
                appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (toolbar.getNavigationIcon() != null) {
                            if ((collapsingToolbar.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbar))) {
                                //  findViewById(R.id.profile_img).setVisibility(View.GONE);
                                //  findViewById(R.id.dp).setVisibility(View.VISIBLE);
                                toolbar.getNavigationIcon().setColorFilter(colors[0], PorterDuff.Mode.SRC_ATOP);
                            } else {
                                // findViewById(R.id.dp).setVisibility(View.GONE);
                                toolbar.getNavigationIcon().setColorFilter(colors[1], PorterDuff.Mode.SRC_ATOP);
                                //  findViewById(R.id.profile_img).setVisibility(View.VISIBLE);
                            }
                        }
                        int range = appBarLayout.getTotalScrollRange();
                        // float percentComplete = -translationY / dependency.getHeight();
                        float scaleFactor = -((appBarLayout.getY() / range) - 0.09290325f);

                        if (scaleFactor < 0) scaleFactor = 0f;

                        getActivity().findViewById(R.id.dp).setScaleX(scaleFactor);
                        getActivity().findViewById(R.id.dp).setScaleY(scaleFactor);

                        // float percentComplete = -translationY / dependency.getHeight();
                        float scaleFactor1 = 1 + (((appBarLayout.getY() * 2) / range) - 0.1858064f);

                        if (scaleFactor1 < 0) scaleFactor1 = 0f;

                        getActivity().findViewById(R.id.profile_img).setScaleX(scaleFactor1);
                        getActivity().findViewById(R.id.profile_img).setScaleY(scaleFactor1);

                    }


                });


            } else {
                getActivity().findViewById(R.id.profile_img).setVisibility(View.GONE);
                getActivity().findViewById(R.id.dp).setVisibility(View.GONE);
                TextView info = getActivity().findViewById(R.id.info);
                info.setVisibility(View.VISIBLE);
                switch (layout) {
                    case "login":
                        info.setText(R.string.reg_login);
                        break;
                    case "cemail":
                        info.setText(R.string.cemail);
                        break;
                    case "cpass":
                        info.setText(R.string.cpass);
                        break;
                    case "cnnm":
                        info.setText(R.string.cnnm);
                        break;
                }

                final CollapsingToolbarLayout collapsingToolbar = getActivity().findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.black));
                collapsingToolbar.setTitle(getResources().getString(R.string.account_activity_header));

                AppBarLayout appBar = getActivity().findViewById(R.id.appbar);
                appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (toolbar.getNavigationIcon() != null) {
                            if ((collapsingToolbar.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbar)))
                                toolbar.getNavigationIcon().setColorFilter(colors[0], PorterDuff.Mode.SRC_ATOP);
                            else
                                toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                });
            }


        }

        public boolean isColorDark(int color) {
            double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;

            return darkness >= 0.4 && darkness < 1.0;

        }

    }

    private class DownloadProfile extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getWindow().getDecorView() != null && !isFinishing()) progressView();
        }

        @Override
        protected String doInBackground(String... params) {

            profile_download(params[0], params[1]);


            return "exec";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (getWindow().getDecorView() != null && !isFinishing()) myaccountView();
        }
    }
}
