package com.example.a71p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoveItems extends AppCompatActivity {

    TextView item_title, item_date, item_location;
    Button remove_button;
    String item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_items);
        item_title = findViewById(R.id.item_title);
        item_date = findViewById(R.id.item_date);
        item_location = findViewById(R.id.item_location);
        remove_button = findViewById(R.id.button2);

        item_title.setText(getIntent().getStringExtra("item_title"));
        item_date.setText(getIntent().getStringExtra("item_date"));
        item_location.setText(getIntent().getStringExtra("item_location"));

        item_id = getIntent().getStringExtra("item_id");
        //Log.v("要删掉的id", item_id);
        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("items");
                DatabaseReference itemRef = rootRef.child(item_id);
//                itemRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(RemoveItems.this, "success to remove this item", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(RemoveItems.this, ShowItemsActivity.class));
//
//                        }
//                    }
//                });
                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Log.v("即将删除的是", snapshot.toString());
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RemoveItems.this, "success to remove this item", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RemoveItems.this, ShowItemsActivity.class));
                                    ;
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });

            }
        });
    }
    }

