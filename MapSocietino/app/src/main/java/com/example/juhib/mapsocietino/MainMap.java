package com.example.juhib.mapsocietino;

import android.content.Intent;
//import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMap extends AppCompatActivity {
    private Button map,info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        map=(Button)findViewById(R.id.buttonmap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I1=new Intent(MainMap.this,MapsActivity.class);
                startActivity(I1);

            }
        });
        info=findViewById(R.id.buttoninfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I2=new Intent(MainMap.this,InfoActivity.class);
                startActivity(I2);

            }
        });
    }
}
