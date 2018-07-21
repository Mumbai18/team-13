package com.example.lucky_rathod.loactioapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity {

    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=(TextView) findViewById(R.id.text);

        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                final LatLng point = place.getLatLng();
                Location location = new Location("Test");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                Double lt=location.getLatitude();
                Double lg = location.getLongitude();
                //lt.toString();
                //tv1.setText(lt.toString());
                Log.v("Longitude is", "" + point.longitude);

                //textLong.setText( "Long: " + location.getLongitude() );
                Toast.makeText(getApplicationContext(),lt.toString()+"\t"+lg.toString(),Toast.LENGTH_SHORT).show();
            }

            public void onError(Status status) {

                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });


    }
}
