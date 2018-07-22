package com.ksapps.shelfshare;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.PhoneNumber;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextView tvProfileName, tvEmail, tvPhone, tvAddress;
    private LinearLayout lvPhone, lvAddress;
    private ImageButton ibProfilePic;
    private final int PICK_IMAGE = 1;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDataRef;
    Button btnPhone, btnAddress;
    EditText etEditText;
    String name, phone, address;
    boolean checkPhone=true, checkAddress=true;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        ibProfilePic = (ImageButton) findViewById(R.id.user_profile_photo);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        lvPhone = (LinearLayout) findViewById(R.id.lvPhone);
        lvAddress = (LinearLayout) findViewById(R.id.lvAddress);


        etEditText = new EditText(getApplicationContext());
        etEditText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnPhone = new Button(getApplicationContext());
        btnPhone.setBackgroundColor(getResources().getColor(R.color.colorHighlight));
        btnPhone.setText("Save");
        btnAddress = new Button(getApplicationContext());
        btnAddress.setBackgroundColor(getResources().getColor(R.color.colorHighlight));
        btnAddress.setText("Save");
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.RIGHT;
        btnPhone.setLayoutParams(params2);
        btnAddress.setLayoutParams(params2);

        ibProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        Intent i = getIntent();
        name = i.getStringExtra("name");
        String email = i.getStringExtra("email");

        tvProfileName.setText(name);
        tvEmail.setText("EmailID:\n" + email);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        mDataRef = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName());

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (btnPhone.isEnabled()) {
                    lvPhone.removeView(etEditText);
                    lvPhone.removeView(btnPhone);
                }
                if (btnAddress.isEnabled()) {
                    lvAddress.removeView(etEditText);
                    lvAddress.removeView(btnAddress);
                }
                phone = dataSnapshot.getValue(UsersInfo.class).getPhone_no();
                if(phone==null){
                    mDataRef.child("phone_no").setValue("");
                }else if(phone.equals("")){
                    checkPhone = true;
                }else{
                    checkPhone = false;
                }
                if(!checkPhone)
                    tvPhone.setText("Phone Number:\n" + phone);
                address = dataSnapshot.getValue(UsersInfo.class).getAddress();
                if(address==null){
                    mDataRef.child("address").setValue("");
                }else if(address.equals("")){
                    checkAddress = true;
                }else{
                    checkAddress = false;
                    Log.v("KSH",String.valueOf(checkAddress));
                }
                if (!checkAddress)
                    tvAddress.setText("Address:\n" + address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("ENABLED", String.valueOf(btnAddress.isInLayout()));
                if (tvAddress.getText().toString().equals("Address:")) {
                    lvAddress.removeView(etEditText);
                    lvAddress.removeView(btnAddress);
                    if (!checkAddress) {
                        tvAddress.setText("Address:\n" + address);
                    } else {
                        tvAddress.setText("Address:\n + Add Address");
                    }
                }
                if (!tvPhone.getText().toString().equals("Phone Number:")) {
                    tvPhone.setText("Phone Number:");
                    etEditText.setHint("Enter your phone no.");
                    if (checkPhone) {
                        etEditText.setText("");
                    } else {
                        etEditText.setText(phone);
                    }
                    etEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                    lvPhone.addView(etEditText);
                    lvPhone.addView(btnPhone);
                }
            }
        });

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("ENABLED", String.valueOf(btnPhone.isInLayout()));
                if (tvPhone.getText().toString().equals("Phone Number:")) {
                    lvPhone.removeView(etEditText);
                    lvPhone.removeView(btnPhone);
                    if (!checkPhone) {
                        tvPhone.setText("Phone Number:\n" + phone);
                    } else {
                        tvPhone.setText("Phone Number:\n + Add Phone Number");
                    }
                }
                if (!tvAddress.getText().toString().equals("Address:")) {
                    tvAddress.setText("Address:");
                    etEditText.setHint("Enter your address");
                    if (checkAddress) {
                        etEditText.setText("");
                    } else {
                        etEditText.setText(address);
                    }
                    etEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    lvAddress.addView(etEditText);
                    lvAddress.addView(btnAddress);
                }
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etPhone = etEditText.getText().toString();
                if (etPhone.length() != 10) {
                    etEditText.setError("Please enter proper phone");
                    etEditText.requestFocus();
                    return;
                } else {
                    mUser = mAuth.getCurrentUser();
                    mDataRef = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName());
                    mDataRef.child("phone_no").setValue(etPhone);
                    Toast.makeText(ProfileActivity.this, "Phone Number Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etAddress = etEditText.getText().toString();
                if (etAddress.length() == 0) {
                    etEditText.setError("Please enter proper address");
                    etEditText.requestFocus();
                    return;
                } else {
                    mDataRef = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName());
                    mDataRef.child("address").setValue(etAddress);
                    Toast.makeText(ProfileActivity.this, "Address Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStorageRef.child("images/" + mUser.getDisplayName()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                RoundedBitmapDrawable roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), bmp);
                roundedBitmapDrawable.setCircular(true);
                ibProfilePic.setImageDrawable(roundedBitmapDrawable);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null,
                    null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            //Log.v("PICTURE",picturePath);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            RoundedBitmapDrawable roundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), thumbnail);
            roundedBitmapDrawable.setCircular(true);
            ibProfilePic.setImageDrawable(roundedBitmapDrawable);

            Uri file = Uri.fromFile(new File(picturePath));
            StorageReference storageReference = mStorageRef.child("images/" + name);
            UploadTask uploadTask = storageReference.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
