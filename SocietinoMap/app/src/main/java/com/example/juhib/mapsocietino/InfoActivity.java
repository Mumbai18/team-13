package com.example.juhib.mapsocietino;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.juhib.mapsocietino.Adapter.ListItemAdapter;
import com.example.juhib.mapsocietino.Model.ToDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;


public class InfoActivity extends AppCompatActivity {

    List<ToDo> toDoList = new ArrayList<>();
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton floatingActionButton1;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    public TextInputEditText name, contact, type,call;
    public TextInputEditText mobile;
    private Button submit;
    private FirebaseFirestore db;
    //private String socName, ename, econtact, etype;
    ListItemAdapter adapter;
    public boolean isUpdate = false;//flag to check is update or add new
    public String idUpdate = "";
    View v1;
    //public SpotsDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        db = FirebaseFirestore.getInstance();


        listItem = (RecyclerView) findViewById(R.id.recycleView);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);


        v1 = getLayoutInflater().inflate(R.layout.dialog_vendor, null);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floatingbutton);
        // builder = new AlertDialog.Builder(InfoActivity.this);
        //final View v1 = getLayoutInflater().inflate(R.layout.dialog_vendor, null);
        name = (TextInputEditText) v1.findViewById(R.id.Name);
        //contact = (TextInputEditText) v1.findViewById(R.id.Contact);
        type = (TextInputEditText) v1.findViewById(R.id.Type);
        //call=(TextInputEditText)v1.findViewById(R.id.Call);
        mobile=(TextInputEditText)v1.findViewById(R.id.Mobile);
        submit = (Button) v1.findViewById(R.id.buttonok);
        //alertDialog = new SpotsDialog(this);


        builder = new AlertDialog.Builder(InfoActivity.this);

        builder.setView(v1);
        dialog = builder.create();

        loadData();
        //submit = (Button) v1.findViewById(R.id.buttonok);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Load data from firestore
                if (!isUpdate) {
                    setData(name.getText().toString(), type.getText().toString(),mobile.getText().toString());
                } else {
                    updateData(name.getText().toString(), type.getText().toString(),mobile.getText().toString());
                    isUpdate = !isUpdate;//reset flag
                }
                dialog.dismiss();
                dialog.cancel();


                /*`socName = "mysoc";
                ename = name.getText().toString();
                econtact = contact.getText().toString();
                etype = type.getText().toString();
                Map<String, Object> user = new HashMap<>();
                user.put("name", ename);
                user.put("contact", econtact);
                user.put("type", etype);

                db = FirebaseFirestore.getInstance();
                db.collection("Vendors").add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(InfoActivity.this,"Vendor added",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this,"error",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });*/


            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("DELETE"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("Vendors")
                .document(toDoList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String name,  String type,String mobile) {
        db.collection("Vendors")
                .document(idUpdate)
                .update("name", name,  "type", type,"mobile",mobile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    }
                });
        //Real time update refresh
        db.collection("Vendors").document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });

    }

    private void setData(String name, String type,String mobile) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> todo = new HashMap<>();
        todo.put("id", id);
        todo.put("name", name);
        //todo.put("mobile",mobile);
        //todo.put("contactDetails", contact);
        todo.put("type", type);
        todo.put("mobile",mobile);
        //todo.put("contact",call);
        db.collection("Vendors").document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });


    }

    private void loadData() {
        //alertDialog.show();
        if (toDoList.size() > 0)
            toDoList.clear();
        db.collection("Vendors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            ToDo todo = new ToDo(doc.getString("id"),
                                    doc.getString("name"),
                                    //doc.getString("contact"),
                                    doc.getString("type"),
                                    doc.getString("mobile"));
                                    //doc.getString("call"));
                            toDoList.add(todo);

                        }
                        adapter = new ListItemAdapter(InfoActivity.this, toDoList);
                        listItem.setAdapter(adapter);
                        //alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
