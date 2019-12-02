package com.locationback.locatontrack;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.android.gms.location.LocationListener;

public class secondActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;
    static secondActivity instance;
    LocationRequest locationRequest;
    TextView textView,textView2;
    EditText Name,inputlongitude,inputlatitude;
    DatabaseReference databaseReference;
    Button add,map;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static secondActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = (TextView)findViewById(R.id.tv);
        textView2 = (TextView)findViewById(R.id.tv2);
        Name = (EditText) findViewById(R.id.name);
        inputlongitude =(EditText)findViewById(R.id.longitutude);
        inputlatitude = (EditText)  findViewById(R.id.latitude);
        add = (Button) findViewById(R.id.add);
        map = (Button) findViewById(R.id.map);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        instance=this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        
                        UpdateLocation();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(secondActivity.this,"you must accept the location",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFields();


            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(secondActivity.this,MapsActivity.class));

            }
        });
    }

    private void addFields() {
        String name = Name.getText().toString();
        String longitude = inputlongitude.getText().toString();
        String latitude = inputlatitude.getText().toString();

        if (!TextUtils.isEmpty(name)){
            String id = databaseReference.push().getKey();
            String Id = "Id";
            fields fields = new fields(id,name,latitude,longitude);
            databaseReference.child(id).setValue(fields);
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("my_pref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Id, id);
            editor.commit();


            Toast.makeText(secondActivity.this,"added",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(secondActivity.this,"not added",Toast.LENGTH_SHORT).show();
        }

    }

    private void UpdateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent());
    }



    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);

    }

    public PendingIntent getPendingIntent() {
      Intent intent = new Intent(secondActivity.this,TrackingService.class);
      intent.setAction(TrackingService.Action_Process_Update);
      return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void updateTextView(final String value){
        secondActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);


            }
        });
    }

    public void updateTextView2(final String value){
        secondActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView2.setText(value);

            }
        });
    }


}
