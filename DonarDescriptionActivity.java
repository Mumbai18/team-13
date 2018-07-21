package com.ksapps.shelfshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DonarDescriptionActivity extends AppCompatActivity {

    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_description);

        Intent i = getIntent();
        String desc = i.getExtras().get("desc").toString();

        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDescription.setText(desc);



    }
}
