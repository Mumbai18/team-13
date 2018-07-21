package com.ksapps.shelfshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddBooksActivity extends AppCompatActivity {

    private EditText title, adDetails, KeepListedFor, PickupTime, etLocation;
    private Button Submit, btnLocation;
    private ImageView AdPhoto;
    private TextView upload;
    private MapView location;
//    private RadioGroup rg;
//    private RadioButton rd;
    private CheckBox needRef,needPack;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private EditText landMark;
    double longitude, latitude;
    private FirebaseUser mUser;
    GoogleApiClient gac;
    StorageReference mStorageRef;
    String postDetails, adTitle, pickUp, keepListed, landmark;
    String type;
    Bitmap bm;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        setTitle("Donor");

        adDetails = (EditText) findViewById(R.id.Food_name);
        KeepListedFor = (EditText) findViewById(R.id.KeepListed);
        PickupTime = (EditText) findViewById(R.id.PickupTime);
       // title = (EditText) findViewById(R.id.title);
        Submit = (Button) findViewById(R.id.Submit);
        AdPhoto = (ImageView) findViewById(R.id.myImgView);
        landMark = (EditText) findViewById(R.id.address);
        upload = (TextView) findViewById(R.id.Upload);
        needRef = (CheckBox)findViewById(R.id.needRef);
        needPack = (CheckBox)findViewById(R.id.needPack);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference().child("ad_info");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ad_image");

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                rg = (RadioGroup) findViewById(R.id.rg);
//                int id = rg.getCheckedRadioButtonId();
//                rd = (RadioButton) findViewById(id);
//                r1 = rd.getText().toString();

                postDetails = adDetails.getText().toString();
                pickUp = PickupTime.getText().toString();
                keepListed = KeepListedFor.getText().toString();
                //adTitle = title.getText().toString();
                landmark = landMark.getText().toString();

                if (postDetails.length() == 0) {
                    adDetails.setError("Please enter proper name");
                    adDetails.requestFocus();
                    return;
                }
                if (landmark.length() == 0) {
                    landMark.setError("Please enter proper landmark");
                    landMark.requestFocus();
                    return;
                }
                if (adTitle.length() == 0) {
                    title.setError("please enter proper title");
                    title.requestFocus();
                    return;
                }
                if (keepListed.length() == 0) {
                    KeepListedFor.setError("please enter proper cost");
                    KeepListedFor.requestFocus();
                    return;
                }

                if (pickUp.length() == 0) {
                    PickupTime.setError("please enter proper pickup time");
                    PickupTime.requestFocus();
                    return;
                }
               if(needRef.isChecked())
                    type="Referigeration";
//                else if(needPack.isChecked())
//                    type = "Packaging";
//                else if(needRef.isChecked()==true && needPack.isChecked()==true)
//                    type="Referigeration And Packaging ";



                HashMap<String, String> map = new HashMap();
                //map.put("ad_title", adTitle);
                map.put("description", postDetails);
                //map.put("free_donate", r1);
                map.put("requirements",type);
                map.put("days", keepListed);
                map.put("pick_up", pickUp);
                map.put("username", mUser.getDisplayName());
                map.put("landmark", landmark);
                mDataRef.push().setValue(map);

                mStorageRef = FirebaseStorage.getInstance().getReference().child("ad_image");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                UploadTask uploadTask = mStorageRef.putBytes(data1);
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
//                if (r1.equals("Free")) {
//                    Toast.makeText(AddBooksActivity.this, "Ad Submitted", Toast.LENGTH_SHORT).show();
//                } else {
                    Intent intent = new Intent(AddBooksActivity.this, NgoActivity.class);
                    startActivity(intent);
                }
            //}
        });
        AdPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            bm = (Bitmap) data.getExtras().get("data");
            AdPhoto.setImageBitmap(bm);
        }
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, adTitle, null);
        return path;
    }
}
