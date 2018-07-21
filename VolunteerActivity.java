package com.ksapps.shelfshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class VolunteerActivity extends AppCompatActivity
{
    private RecyclerView allDonationsList;
    private DatabaseReference allDatabaseDonationsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        allDonationsList = (RecyclerView) findViewById(R.id.allDonationList);
        allDonationsList.setHasFixedSize(true);
        allDonationsList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseDonationsReference = FirebaseDatabase.getInstance().getReference().child("ad_info");




    }

    @Override
    protected void onStart() {
        super.onStart();




        //we need to have a firebase recycler adapter to display the users
        FirebaseRecyclerAdapter<AllDonations,AllDonationsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<AllDonations, AllDonationsViewHolder>(AllDonations.class,R.layout.volunteer_display_layout,AllDonationsViewHolder.class,allDatabaseDonationsReference) {
            @Override
            protected void populateViewHolder(AllDonationsViewHolder viewHolder, AllDonations model, final int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setLocation(model.getLandmark());

                final String desc = model.getDescription();

                //which view(user) is been clicked, set an on click listener
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we will get the position of the user which has been clicked and with that position we will retrieve the ID

                        Intent donarDescriptionIntent = new Intent(VolunteerActivity.this,DonarDescriptionActivity.class);
                        donarDescriptionIntent.putExtra("desc",desc);
                        startActivity(donarDescriptionIntent);
                    }
                });

            }
        };


        allDonationsList.setAdapter(firebaseRecyclerAdapter);



    }

    public static class AllDonationsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public AllDonationsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setDescription(String description) {
            TextView name = (TextView) mview.findViewById(R.id.tvDonationDescription);
            name.setText(description);
        }
        public void setLocation(String location) {
            TextView name = (TextView) mview.findViewById(R.id.tvDonationLocation);
            name.setText(location);
        }
    }
}
