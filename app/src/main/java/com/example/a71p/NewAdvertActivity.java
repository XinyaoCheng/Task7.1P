package com.example.a71p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewAdvertActivity extends AppCompatActivity {

    ItemModel itemModel;
    RadioGroup post_type_view;
    EditText name_view, phone_view,description_view, data_view,location_view;
    Button save_button;
    String post_type, name, phone, description, data, location;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);
        post_type_view = findViewById(R.id.radioGroup);
        name_view = findViewById(R.id.editTextTextPersonName);
        phone_view = findViewById(R.id.editTextPhone);
        description_view = findViewById(R.id.editTextTextDescription);
        data_view = findViewById(R.id.editTextDate);
        location_view = findViewById(R.id.editTextTextLocation);
        save_button = findViewById(R.id.save_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
                saveInFirebase();
            }
        });



    }

    private void saveInFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef= firebaseDatabase.getReference("items");
        itemId = databaseRef.push().getKey();
        itemModel.setItem_id(itemId);
        Log.v("新插入的item的id", itemId);
        Log.v("新插入的item",itemModel.toString());
        databaseRef.child(itemId).setValue(itemModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NewAdvertActivity.this, "success to create your item", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewAdvertActivity.this, ShowItemsActivity.class));
                    }
                });

    }

    private void getInfo() {
        int selectedRadioButtonId = post_type_view.getCheckedRadioButtonId();
        if(selectedRadioButtonId!=-1){
            RadioButton seletedRadioButton = findViewById(selectedRadioButtonId);
            post_type = seletedRadioButton.getText().toString();
        }
        name = name_view.getText().toString();
        phone = phone_view.getText().toString();
        description = description_view.getText().toString();
        data = data_view.getText().toString();
        location = location_view.getText().toString();
        itemModel = new ItemModel(itemId,post_type,name,phone,description,data,location);
    }
}