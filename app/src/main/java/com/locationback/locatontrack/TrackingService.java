package com.locationback.locatontrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class TrackingService extends BroadcastReceiver {

    public static final String Action_Process_Update="com.locationback.locatontrack.Update_Location";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent!=null){
            final String action = intent.getAction();
            if (Action_Process_Update.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    Location location = result.getLastLocation();
                   String location_latitude  = new StringBuilder(""+location.getLatitude())
                            .toString();
                   String location_longitude = new StringBuilder(""+location.getLongitude())
                           .toString();
                    SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy hhmmss");
                    String format = s.format(new Date());
                    try{
                        String id;
                        secondActivity.getInstance().updateTextView(location_latitude);
                        secondActivity.getInstance().updateTextView2(location_longitude);
                        Log.e(TAG, "loc =" +location_longitude);
                        Log.e(TAG, "loc =" +location_latitude);
                        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("my_pref",MODE_PRIVATE);
                        id = preferences.getString("Id","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                        ref.child(id).child("latitude").setValue(location_latitude);
                        ref.child(id).child("longitude").setValue(location_longitude);
                        ref.child(id).child("timeStamp").setValue(format);


                    }catch (Exception ex){

                       Toast.makeText(context,"",Toast.LENGTH_SHORT).show();

                    }
                   }
                }
            }
        }

    }

