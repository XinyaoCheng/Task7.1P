package com.example.a71p;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.Arrays;
import java.util.List;

public class NewAdvertActivity extends AppCompatActivity {

    ItemModel itemModel;
    RadioGroup post_type_view;
    EditText name_view, phone_view, description_view, data_view, location_view;
    Button save_button, get_current_location;
    String post_type, name, phone, description, data, location;
    String itemId;
    PlacesClient placesClient;
    LatLng mLatLng = null;
    private String KEY = "AIzaSyAW_aJx_81VWi7BzPW7cMDACFcv3AKJrgg";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 200;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"};


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
        get_current_location = findViewById(R.id.get_location_button);

        if (!Places.isInitialized()) {
            Places.initialize(this, KEY);
            placesClient = Places.createClient(this);
        }

        location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(),setLocation.class),AUTOCOMPLETE_REQUEST_CODE);


            }
        });

        get_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
                saveInFirebase();
            }
        });


    }



    private void getDeviceLocation() {

        ProgressDialog progressDialog = ProgressDialog.show(NewAdvertActivity.this, "", "getting your location...", true);
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        if (ActivityCompat.checkSelfPermission(this, "android.Manifest.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NewAdvertActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION"},1);
        }
        placesClient = Places.createClient(this);

        final Task<FindCurrentPlaceResponse> placeResult =
                placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if(task.isSuccessful()&&task.getResult()!=null){
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();

                    if (!likelyPlaces.getPlaceLikelihoods().isEmpty()) {

                        Place place = null;
                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            place = placeLikelihood.getPlace();
                        }
                        if (null == place) {
                            Toast.makeText(NewAdvertActivity.this, "can't get your location",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String address = place.getAddress();
                        mLatLng = place.getLatLng();
//                        Intent intent=new Intent(NewAdvertActivity.this,setLocation.class);
//                        intent.putExtra("latitude", mLatLng.latitude);
//                        intent.putExtra("longitude",mLatLng.longitude);
//                        intent.putExtra("address", address);
//                        startActivity(intent);
                        location_view.setText(address);


                        Toast.makeText(NewAdvertActivity.this, "success on getting your location",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(NewAdvertActivity.this, "can't get your location",Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            };

        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if(data!=null){
                    location_view.setText(data.getStringExtra("address"));
                    mLatLng = new LatLng(data.getDoubleExtra("la",0.0),data.getDoubleExtra("lo",0.0));
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);

                Log.i("Autocomplete", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void saveInFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef= firebaseDatabase.getReference("items");
        itemId = databaseRef.push().getKey();
        itemModel.setItem_id(itemId);
        //Log.v("新插入的item的id", itemId);
        //.v("新插入的item",itemModel.toString());
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
        itemModel = new ItemModel(itemId,post_type,name,phone,description,data,location,mLatLng.latitude,mLatLng.longitude);
    }
}