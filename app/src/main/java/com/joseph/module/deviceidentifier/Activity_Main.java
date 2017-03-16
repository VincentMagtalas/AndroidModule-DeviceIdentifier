/*
 * Copyright (c) 2017
 *
 * Developed By: Joseph Vincent Lazado Magtalas
 */

package com.joseph.module.deviceidentifier;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class Activity_Main extends AppCompatActivity implements LocationListener {

    TextView tvDeviceID, tvInternetStatus, tvMACAddress, tvIPAddress, tvGPSStatus, tvCurrentLocation,tvCountry;
    Button btnInternet, btnGPS;

    //sa GPS Location to
    LocationManager locationManager;
    String mprovider;
    Criteria criteria;

    /*
    * Note:
    * need mag lagay ng implements LocationListener para mapagana ung sa GPS
    * wag mo rin kakalimutan ung sa AndroidManifest.xml
    *
    * eto ung para sa Internet
    * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    * <uses-permission android:name="android.permission.INTERNET" />
    * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    *
    * eto naman ung para sa  GPS
    * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * Device ID
        * Internet
        * MAC Address
        * IP Address
        * GPS
        * Current Location
        * Country
        * */
        tvDeviceID = (TextView) findViewById(R.id.tvDeviceID);
        tvInternetStatus = (TextView) findViewById(R.id.tvInternetStatus);
        tvMACAddress = (TextView) findViewById(R.id.tvMACAddress);
        tvIPAddress = (TextView) findViewById(R.id.tvIPAddress);
        tvGPSStatus = (TextView) findViewById(R.id.tvGPSStatus);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);
        tvCountry = (TextView) findViewById(R.id.tvCountry);

        btnInternet = (Button) findViewById(R.id.btnInternet);
        btnGPS = (Button) findViewById(R.id.btnGPS);

        String DeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        tvDeviceID.setText(DeviceID);

        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternet();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
            }
        });


    }


    private void checkInternet() {
        //check Internet Connection
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            tvInternetStatus.setText("Enabled");

            getMACAddress();

            getIPAddress();

        } else {
            tvInternetStatus.setText("Disabled");
            tvMACAddress.setText("NULL");
            tvIPAddress.setText("NULL");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("WIFI is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable WIFI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    private void getMACAddress(){
        //get MAC Address
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String macAddress = wm.getConnectionInfo().getMacAddress().toString();
        tvMACAddress.setText(macAddress);
    }

    private void getIPAddress(){
        //get IP Address
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        tvIPAddress.setText(ipAddress);
    }

    private void checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvGPSStatus.setText("Enabled");

            getLocation();
            getCountry();

        } else {
            tvGPSStatus.setText("Disabled");
            tvCurrentLocation.setText("NULL");
            tvCountry.setText("NULL");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }
    }

    //** Start of getting Current Location
    private void getLocation() {

        criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(mprovider);
            //locationManager.requestLocationUpdates(mprovider, 15000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, (float) 10.0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 90 * 1000, (float) 10.0, this);

            if (location != null) {
                onLocationChanged(location);
            } else {
                tvCurrentLocation.setText("Please Wait...");
            }
        }
    }
    public void onLocationChanged(Location location) {
        tvCurrentLocation.setText("Longitude:" + location.getLongitude()+"\n"+"Latitude:" + location.getLatitude());
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}
    //** End of getting Current Location

    public void getCountry(){
        final TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String simCountry = tm.getSimCountryIso();
        if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
            tvCountry.setText(simCountry.toUpperCase(Locale.US));
        }
        else {
            if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    tvCountry.setText(networkCountry.toUpperCase(Locale.US));
                }
            }
        }
    }
}
