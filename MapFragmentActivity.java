//package com.example.lucky_rathod.csp3;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.MapsInitializer;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
///**
// * Created by Lucky_Rathod on 02-01-2018.
// */
//
//public class MapFragmentActivity extends Fragment {
//
//    public MapView mMapView;
//    private GoogleMap googleMap;
//    DatabaseReference latlongreference;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        //return super.onCreateView(inflater, container, savedInstanceState);
//        ((MainActivity) getActivity())
//                .setActionBarTitle("Issues on Map");
//        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
//        latlongreference = FirebaseDatabase.getInstance().getReference().child("ReportedIssues");
//
//        mMapView = (MapView) rootView.findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
//
//        mMapView.onResume(); // needed to get the map to display immediately
//
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//                googleMap = mMap;
//
//                // For showing a move to my location button
//                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                googleMap.setMyLocationEnabled(true);
//
//                // For dropping a marker at a point on the Map
////                LatLng sydney = new LatLng(-34, 151);
////                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//
//                latlongreference.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                        LatLng newLocation = new LatLng(
//                                dataSnapshot.child("latitude").getValue(Double.class),
//                                dataSnapshot.child("longitude").getValue(Double.class)
//                        );
//                        googleMap.addMarker(new MarkerOptions()
//                                .position(newLocation)
//                                .title(dataSnapshot.getKey()));
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {}
//                });
//
//
//                // For zooming automatically to the location of the marker
//                LatLng sydney = new LatLng(37.422005,-122.084095);
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        });
//
//        return rootView;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
//
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
////        view.findViewById(R.id.capture_pothole).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Toast.makeText(getActivity(), "CLICK A PHOTO FROM CAMERA", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
////                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
//                        Fragment fragment = new HomeFragmentActivity();
//                        FragmentManager fragmentManager = getFragmentManager();
//                        FragmentTransaction ft = fragmentManager.beginTransaction();
//
//                        //Now in below method 1st argument is container ID
//                        //And we know that our container is FrameLayout
//                        //2nd Argumnet is Our Fragment object which is to replaced with
//                        ft.replace(R.id.screen_area,fragment);
//                        ft.commit();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//    }
//}
