package com.example.lucky_rathod.csp3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Transformation;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    //RC is Request code
    private static final int RC_SIGN_IN = 1;
    TextView mName,mEmailId;
    ImageView mPic;
    public static  String name,email_id,uid;
    private Uri photoUrl;

   //static FirebaseUser user;


//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference userIdReference = database.getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Here we will Initialize Firebase Components

        //.getInstance() means we are refering to the unique url which is used in Firebase for our console

        mFirebaseAuth = FirebaseAuth.getInstance(); //Now we will define AuthStateListener Activity in onCreate()

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //For setting Default Fragment
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));

        //Setting current username and its profile photo and email id in Navigation Drawer
        mName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.username);
        mPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        mEmailId = (TextView)navigationView.getHeaderView(0).findViewById(R.id.email_id);


        //Defining mAuthStateListener()

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //in AuthStateChangeMethod there is firebaseAuth variable these is different from previous one which we declared
               // These specifies that current user is Authenticated or not

               FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    for (UserInfo profile : user.getProviderData()) {
                        // Id of the provider (ex: google.com)
                        String providerId = profile.getProviderId();

                        // UID specific to the provider
                        uid = profile.getUid();

                        // Name, email address, and profile photo Url
                        name = profile.getDisplayName();
                        photoUrl = profile.getPhotoUrl();
                        email_id= user.getEmail();
                        mName.setText(name);



                        Log.d("Photo url ",String.valueOf(photoUrl));

                        Picasso.with(getApplicationContext())
                                .load(String.valueOf(photoUrl))
//                                .placeholder(R.drawable.logo)
                                .resize(200, 200)
                              .transform(new CircleTransform())
                                .centerCrop()
                                .into(mPic);
                        mEmailId.setText(email_id);

//                        //Created a pojo class Users for adding users in database
//
//                        Users users = new Users(email_id);
//                        userIdReference.child(user.getUid()).setValue(users);


                    };

                }

                else
                {
                    //user is signed out
                    //Means Here comes FirebaseUI
                    //Means we will redirect our user to Signin page
                    Toast.makeText(MainActivity.this, "Your are not signed In , Please signIn", Toast.LENGTH_SHORT).show();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home){
            fragment = new HomeFragmentActivity();
        }
        else if (id == R.id.nav_potholes) {
            fragment = new PotholeFragmentActivity();
        } else if (id == R.id.nav_sanitation) {
            fragment = new SanitationFragmentActivity();
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(MainActivity.this,MapLocationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_signout) {
//            AuthUI.getInstance().signOut(this);
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                            Toast.makeText(MainActivity.this, "Sign out successfull", Toast.LENGTH_SHORT).show();
                        }
                    });
            AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
            fragment = new HomeFragmentActivity();


        }else if(id == R.id.nav_resend){
            fragment = new ResendFragmentActivity();
        }
        else  if (id == R.id.nav_got_a_new_issue){
            fragment = new CaptureImageFragmentActivity();
        }

        if(fragment !=null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            //Now in below method 1st argument is container ID
            //And we know that our container is FrameLayout
            //2nd Argumnet is Our Fragment object which is to replaced with
            ft.replace(R.id.screen_area,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In ", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}

class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }

}
