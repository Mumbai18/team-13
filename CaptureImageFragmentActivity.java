package com.example.lucky_rathod.csp3;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.example.lucky_rathod.csp3.MainActivity.email_id;
import static com.example.lucky_rathod.csp3.MainActivity.uid;


/**
 * Created by Lucky_Rathod on 03-01-2018.
 */

public class CaptureImageFragmentActivity extends Fragment {
    //    private ImageView imageHolder;
//    private final int requestCode = 20;
    private Button btn, continueButton, get_gps_location ;
    ImageButton address_button;
    private TextView final_address;
    private ImageView imageview;
    private int CAMERA = 2;
    private int PICK_IMAGE_REQUEST = 1;
    String latitude,longitude,longitudeAndLatitude,myUrl,imageUrl,imageName;
    //For volley library i-e fast intenet
    RequestQueue requestQueue;

    //For current date and time
    String formattedDate,formattedTime;

    //Image address
    String imageAddress;

    //We will set default value of image width and height as 0
    double width = 0;
    double height = 0;

    //For converting  co-ordinates into address
    public static final String GEOCODING_LOCATION_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    public static final String GEOCODING_API_KEY = "AIzaSyCC5IvJinK9fP4CTgfZ80lLu-s2WSKsU9w";


    //Now we will add our storage and database reference
    StorageReference storageReference;
    DatabaseReference imageAndEmailReference,rootReference;

    //Constants for Storage path and database path which will be used in getting reference till there
    public static final String STORAGE_PATH = "images/";

    //Uri for image to be stored
    Uri uri;

    //Converting uri to string for bundle
    String stringUri;

//    //Counter to inc image no
//    int counter = 1;

    //Now we store userid when continue button is clicked
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    //No of images in particular parent
    //long imageCounter,imageCounterPreference;


    //Shared Preferences for imageCounter which we are receiving from database
    //we will store it in shared preferences because when we close our app our imagecounter varaible
    //will get  0  if we open it again and image will be set to 1 on wards again
    SharedPreferences sharedPreferences;

    //Bitmap image which will be set on Imageview and send to the other fragment
    Bitmap bitmap;

    //There is a solution to create file (on external cache dir or anywhere else) and put this
    //file's uri as output extra to camera intent - this will define path where taken picture will be stored.
    File file;

    String mCurrentPhotoPath;

    Double lat,lon;


    String realPath;

    String imageOutputFromModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity())
                .setActionBarTitle("Capture your issue ");
        return inflater.inflate(R.layout.capture_image_fragment, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn = view.findViewById(R.id.capture_image_button);
        continueButton = view.findViewById(R.id.continue_button);
        imageview = view.findViewById(R.id.captured_image);
        get_gps_location = view.findViewById(R.id.get_gps_location);
        final_address = view.findViewById(R.id.address);
        address_button = view.findViewById(R.id.address_button);
        address_button.setAlpha(0.7f);
        address_button.setEnabled(false);
        continueButton.setAlpha(0.7f);
        continueButton.setEnabled(false);



        //For converting into address
        requestQueue = Volley.newRequestQueue(getActivity());

        //Now we will get our reference of FireBase Storage
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_PATH);
        imageAndEmailReference = FirebaseDatabase.getInstance().getReference().child("users");
        rootReference = FirebaseDatabase.getInstance().getReference();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());



        //Show Dialog box when these button is clicked
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(photoCaptureIntent,requestCode);
                showPictureDialog();
            }
        });

        //Now we will perform our gps module
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Location mlocation = location;
                Log.d("Location Changes", location.toString());
                latitude =String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                lat = location.getLatitude();
                lon = location.getLongitude();
                longitudeAndLatitude = location.getLatitude() + "," + location.getLongitude();
//                Toast.makeText(getActivity(), "Location Fetched" +longitudeAndLatitude, Toast.LENGTH_SHORT).show();
                Log.e("Latitude and Lngitude", longitudeAndLatitude);
                address_button.setEnabled(true);
                address_button.setAlpha(1.0f);
//                continueButton.setEnabled(true);
//                continueButton.setAlpha(1.0f);


                //Run time permissions are remaining add it in  onProviderDisabled method

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                  //These method is called when GPS is turned OFF
//            //So in these method we will create an Intent that will take user to setting in android to enable Gps
//            Intent settingGpsLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(settingGpsLocation);

                final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "Do you want open GPS setting?";

                builder.setMessage(message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        getActivity().startActivity(new Intent(action));
                                        d.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        d.cancel();
                                    }
                                });
                builder.create().show();
            }
        };
        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        // Now create a location manager
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // This is the Best And IMPORTANT part
        final Looper looper = null;

        // Now whenever the button is clicked fetch the location one time
        get_gps_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 10);
                    }
                    return;
                }
                locationManager.requestSingleUpdate(criteria, locationListener, looper);
            }
        });

        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueButton.setEnabled(true);
                continueButton.setAlpha(1.0f);
                //Now here we will make a new JSONObjectRequest with following url that contains
                //http request to geocoding site with query of longitude and latitude with key

                //Volley does all networking part of background thread easier

                //correct requesr code is
                //After url its null
                //JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>(){},Response.ErrorListener(){})


                //Make these url but with user defined longitude and latitude
                //"https://maps.googleapis.com/maps/api/geocode/json?latlng=19.2281606,72.8614862&key=AIzaSyCC5IvJinK9fP4CTgfZ80lLu-s2WSKsU9w"
                Uri baseUri = Uri.parse(GEOCODING_LOCATION_URL);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("latlng", longitudeAndLatitude);
                uriBuilder.appendQueryParameter("key", GEOCODING_API_KEY);
                myUrl = uriBuilder.toString();
                Log.e("HTTP REQUEST ", myUrl);
                JsonObjectRequest request = new JsonObjectRequest(myUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Now response comes in JSON array and it contains JSONObject
                        //In the first JSONObject there is formatted address
                        try {
                             imageAddress = response.getJSONArray("results").getJSONObject(0).getString
                                    ("formatted_address");
                            final_address.setText(imageAddress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(request);

                //Now we will get our current date and time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("LLL dd, yyyy");
                formattedDate = df.format(c.getTime());
//                date.setText("Current Date " + formattedDate);
                SimpleDateFormat time = new SimpleDateFormat("h:mm a");
                 formattedTime = time.format(c.getTime());
//                current_time.setText("Current Time " + formattedTime);


            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                //Now we will insert our image in our Firebase storage
//                if(uri != null){
//
//                    //Now we will also show the progress of our uploading task
//                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setTitle("Uploading Your Image");
//                    progressDialog.show();
//                    //insert data
//
//
////
//                    StorageReference reference = storageReference.child(STORAGE_PATH + System.currentTimeMillis() + ". " + getActualImage(uri));
//                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            //Now we will create POJO class for Images to get uploaded in Database
//                           imageUrl =  taskSnapshot.getDownloadUrl().toString();
//                           //Now we will retreive our value
//                         //  imageCounterPreference = sharedPreferences.getLong("ImageCounter",0);
//
//                            //Now here we will check that whether our database is empty or not
//
////                            rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    if(dataSnapshot.getValue()==null){
////                                        imageCounter = 0;
////                                    }
////                                }
////
////                                @Override
////                                public void onCancelled(DatabaseError databaseError) {
////
////                                }
////                            });
//
//                            imageAndEmailReference.child(currentFirebaseUser.getUid()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    long imageCounter =  dataSnapshot.getChildrenCount();
//
//                                    //Now we will store these counter in sharedPreferences beacause if user close and opens the app then from these value
//                                    //image should be uploaded
////                                    SharedPreferences.Editor editor = sharedPreferences.edit();
////                                    editor.putLong("ImageCounter",imageCounter);
////                                    editor.commit();
//                                    //Now before or after adding image in database we will not insert a user emailid because there comes the big error of overriding
//                                    imageAndEmailReference.child(currentFirebaseUser.getUid()).child("images").child(String.valueOf(++imageCounter))
//                                            .setValue(new Images(imageUrl,formattedDate,formattedTime,imageAddress,longitudeAndLatitude,width,height));
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//
////                           //Now before or after adding image in database we will not insert a user emailid because there comes the big error of overriding
////                           imageAndEmailReference.child(currentFirebaseUser.getUid()).child("images").child(String.valueOf(++imageCounter))
////                                   .setValue(new Images(imageUrl,formattedDate,formattedTime,imageAddress,longitudeAndLatitude,width,height));
//
//                           uri=null;
//                           imageview.setImageResource(R.drawable.noimage);
//
//
//                            //Now when data gets stored on database
//                            //We will dismiss our progress dialog
//
//                            progressDialog.dismiss();
//                            Toast.makeText(getActivity(), "Data uploaded successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                    //Now when it is interrupted we will show the message in Toast
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                    double totalProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                                    progressDialog.setMessage("Uploaded % " + (int)totalProgress);
//                                }
//                            });
//                }
//                else{
//                    //show message
//                    Toast.makeText(getActivity(), "Choose the image first", Toast.LENGTH_SHORT).show();
//                }

                if(uri!=null) {

                    Fragment fragment = new TensorFlowIntegrationFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();

                    //using Bundle to send data
                    Bundle bundle = new Bundle();
                    bundle.putString("imageDate", formattedDate);
                    bundle.putString("imageTime", formattedTime);
                    bundle.putString("address", imageAddress);
                    bundle.putString("addressValues", longitudeAndLatitude);
                    bundle.putParcelable("bitmap", bitmap);

                    bundle.putDouble("latitude", lat);
                    bundle.putDouble("longitude", lon);

                    //Now we will convert our uri to string and pass it in bundle
                    //And at receiver fragment we will convert string to uri

                    bundle.putString("stringUri", stringUri);
                    bundle.putString("realPath",realPath);



                    fragment.setArguments(bundle); //data being send to SecondFragment
                    ft.replace(R.id.screen_area, fragment);
                    ft.commit();

                }
                else
                {
                    Toast.makeText(getActivity(), "Choose the image first", Toast.LENGTH_SHORT).show();
                }
            }

        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new HomeFragmentActivity();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();

                        //Now in below method 1st argument is container ID
                        //And we know that our container is FrameLayout
                        //2nd Argumnet is Our Fragment object which is to replaced with
                        ft.replace(R.id.screen_area,fragment);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });


    }

/*


/*    //These method is called when GPS is turned OFF
    //So in these method we will create an Intent that will take user to setting in android to enable Gps
    Intent settingGpsLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    startActivity(settingGpsLocation);*/





    public void showPictureDialog(){
        //Now when these button is created we will make a Alert Dialog
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"
        };

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary(){
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void takePhotoFromCamera(){
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent,CAMERA);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = new File(getActivity().getExternalCacheDir(),
//                String.valueOf(System.currentTimeMillis()) + ".jpg");
//        uri = Uri.fromFile(file);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        getActivity().startActivityForResult(intent, CAMERA);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            file = createImageFile();
            uri = Uri.fromFile(file);
            stringUri = uri.toString();
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(takePhotoIntent, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            realPath = ImageFilePath.getPath(getActivity(), data.getData());
Log.d("Selected image",realPath);
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));


                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


//            //Now after these line we will show our continue button
//            continueButton.setVisibility(View.VISIBLE);
        }/* else if (this.CAMERA == requestCode && resultCode == RESULT_OK) {

*//*            bitmap = (Bitmap) data.getExtras().get("data");
                imageview.setImageBitmap(bitmap);
                Toast.makeText(getActivity(), "Successfully Captured image from Camera", Toast.LENGTH_SHORT).show();*//*
//                continueButton.setVisibility(View.VISIBLE);

            //Now we have to send uri to another intent

            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = extras.getParcelable("data");
                // display image in ImageView.
                imageview.setImageBitmap(bitmap);
                // saveBitmapToFile("/sdcard/crop/cropped_img.jpg", photo);
            }



        }*/
        if (requestCode == 20 && resultCode == RESULT_OK) {

            // set the dimensions of the image
            int targetW =100;
            int targetH = 100;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            // stream = getContentResolver().openInputStream(data.getData());
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);
            imageview.setImageBitmap(bitmap);
        }
    }

    public String getActualImage(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
