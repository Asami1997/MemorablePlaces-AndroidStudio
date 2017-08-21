package com.example.asami.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String type;
    Bundle bundle;
    Geocoder geocoder;
    String addressInfo;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = this.getSharedPreferences("com.example.asami.memorableplaces",Context.MODE_PRIVATE);


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Intent intent2 = getIntent();

        bundle = intent2.getExtras();

        type = bundle.getString("types");




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                mMap.clear();
                try {

                    List<Address>  address= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);


                    addressInfo = "";
                    if(address != null && address.size() > 0)
                       {


                                  if(address.get(0).getThoroughfare()!=null)
                                  {

                                  if (address.get(0).getSubThoroughfare() != null)
                                  {
                                      addressInfo += address.get(0).getSubThoroughfare() + " ";


                                  }
                                      addressInfo += address.get(0).getThoroughfare() + "";
                              }

                       }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //id we dident get the throughfare and subthroufare then we will get the time and date insted

                if(addressInfo == "")
                   {

                       SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mmss");
                        addressInfo = sdf.format(new Date());



                   }

                mMap.addMarker(new MarkerOptions().position(latLng).title(addressInfo));

                 //Update the Arraylists


                  MainActivity.arrayList.add(addressInfo);
                  MainActivity.arrayAdapter.notifyDataSetChanged();
                  MainActivity.latlngs.add(latLng);

                try {
                    //For latlngs i cant just save it as strings , i need to convert them as Strings , So create two arraylist one for latitiudes and one for longtitudes and add each for of latlng to the respective arraylist
                   // One Variable of Latlng contains both Latitudes and Longitudes
                    ArrayList<String> lats = new ArrayList<String>();
                    ArrayList<String> longs = new ArrayList<String>();

                    //One LatLing contains both lattiueds and longitudes
                    for(LatLng corrd : MainActivity.latlngs)

                       {
                           lats.add(Double.toString(corrd.latitude));
                           longs.add(Double.toString(corrd.longitude));
                       }

                    //Now EveryThing is saved perme
                    sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(lats)).apply();
                    sharedPreferences.edit().putString("longs",ObjectSerializer.serialize(longs)).apply();
                    sharedPreferences.edit().putString("address",ObjectSerializer.serialize(MainActivity.arrayList)).apply();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();



            }


        });



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng userLocation = new LatLng(location.getLongitude(),location.getLatitude());
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,13));

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

        if(Build.VERSION.SDK_INT < 25)
           {
              mMap.clear();
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
           }else
               {

                   if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                   } else
                       {



                                    if(type.equals("add"))
                                       {
                                           mMap.clear();
                                           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                           Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                           LatLng userLocationlastknown = new LatLng(lastKnown.getLongitude(),lastKnown.getLatitude());
                                           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationlastknown,13));

                                       }else
                                              {

                                                  //retrieving

                                                  mMap.addMarker(new MarkerOptions().position(MainActivity.latlngs.get(bundle.getInt("memo"))).title(MainActivity.arrayList.get(bundle.getInt("memo"))));
                                                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.latlngs.get(bundle.getInt("memo")),13));

                                              }

                       }
               }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
           {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                   {
                       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                   }

           }

    }



}