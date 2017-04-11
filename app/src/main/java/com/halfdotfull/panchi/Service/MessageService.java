package com.halfdotfull.panchi.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.halfdotfull.panchi.DataBase.ContactDataBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * Created by 15103068 on 28-03-2017.
 */

public class MessageService extends Service implements SensorEventListener,LocationListener{

    String contact,name;
    Integer serial;
    String add,cit,stat;
    SharedPreferences sharedpreferences;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravit
    ContactDataBase db;

    public MessageService() {
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sharedpreferences = getSharedPreferences("panchi", Context.MODE_PRIVATE);//To display MESSAGE
        Log.d("service","onstart service");
        SensorManager sManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL); // or other delay
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        try {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            db=new ContactDataBase(MessageService.this);

            if (mAccel > 60) {
                String msg = null;

                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Geocoder geocoder;

                        geocoder = new Geocoder(MessageService.this, Locale.getDefault());


                        List<Address> addresses=null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        add = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        cit = addresses.get(0).getLocality();
                        stat = addresses.get(0).getAdminArea();

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                SmsManager smsmanager = SmsManager.getDefault();
                Cursor res=db.getAllData();
                if(res.getCount()==0) Toast.makeText(this, "No contacts given", Toast.LENGTH_SHORT).show();
                else{
                    while (res.moveToNext()) {
                        msg=sharedpreferences.getString("Message","");
                        if(msg=="")
                        {
                            msg=" Please help me ";
                        }
                        smsmanager.sendTextMessage(res.getString(0), null, msg +" I am at"+" H-046,Asian highway "+ "Noida" +"", null, null);
                        Log.d("service",msg);
                        Toast.makeText(getApplicationContext(), "Emergency Message sent to " + res.getString(1), Toast.LENGTH_LONG).show();
                    }
                }
                File file=getFilesDir();
                File in=new File(file,"ContactsList");
                FileInputStream fin=null;
                fin=new FileInputStream(in);
                InputStreamReader isr=new InputStreamReader(fin);
                BufferedReader bufRdr=new BufferedReader(isr);
                String str="";
                Toast.makeText(this, sharedpreferences.getString("Message",""), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}


