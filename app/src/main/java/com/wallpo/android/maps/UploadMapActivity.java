package com.wallpo.android.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.transition.TransitionManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.R;
import com.wallpo.android.utils.Common;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UploadMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    AppCompatTextView maplocation;
    TextInputEditText searchtext;
    AppCompatImageView searchimg;
    ConstraintLayout mainlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_map);

        Common.latlongdata = "";
        maplocation = findViewById(R.id.maplocation);
        searchimg = findViewById(R.id.searchimg);
        mainlay = findViewById(R.id.mainlay);
        searchtext = findViewById(R.id.searchtext);
        searchtext.setVisibility(View.GONE);

        searchimg.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(mainlay);

            if (searchtext.getVisibility() == View.GONE) {

                searchtext.setVisibility(View.VISIBLE);
                maplocation.setVisibility(View.GONE);

            } else {

                maplocation.setVisibility(View.VISIBLE);

                searchtext.setVisibility(View.GONE);

            }
        });

        searchtext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //search
                String location = searchtext.getText().toString();

                List<Address> addressList = null;

                if (location != null || !location.isEmpty()) {

                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        mMap.clear();

                        Common.latlongdata = latLng.latitude + "/" + latLng.longitude;

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Location Selected"));

                        CameraPosition cameraPosition = new CameraPosition.Builder().
                                target(latLng).tilt(56).zoom(15).bearing(0).
                                build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(this, "Enter a vaild location.", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
            return false;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setOnMapClickListener(latLng -> {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                if (knownName != null) {
                    maplocation.setText(knownName);
                }

                if (city != null) {
                    maplocation.setText(maplocation.getText().toString() + ", " + city);
                }

                if (state != null) {
                    maplocation.setText(maplocation.getText().toString() + ", " + state);
                }

                if (country != null) {
                    maplocation.setText(maplocation.getText().toString() + ", " + country);
                }

                if (maplocation.getText().toString().isEmpty()) {
                    maplocation.setText("Unknown Location to show address, Location added to post.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.clear();

            Common.latlongdata = latLng.latitude + "/" + latLng.longitude;

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Location Selected: " + maplocation));

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(latLng).tilt(56).zoom(15).bearing(0).
                    build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng gotloc = new LatLng(location.getLatitude(), location.getLongitude());

                        Common.latlongdata = location.getLatitude() + "/" + location.getLongitude();

                        mMap.addMarker(new MarkerOptions()
                                .position(gotloc)
                                .title("Here you are"));

                        CameraPosition cameraPosition = new CameraPosition.Builder().
                                target(gotloc).tilt(56).zoom(15).bearing(0).
                                build();

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            String address = addresses.get(0).getAddressLine(0);
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();

                            if (knownName != null) {
                                maplocation.setText(knownName);
                            }

                            if (city != null) {
                                maplocation.setText(maplocation.getText().toString() + ", " + city);
                            }

                            if (state != null) {
                                maplocation.setText(maplocation.getText().toString() + ", " + state);
                            }

                            if (country != null) {
                                maplocation.setText(maplocation.getText().toString() + ", " + country);
                            }

                            if (maplocation.getText().toString().isEmpty()) {
                                maplocation.setText("Unknown Location to show address, Location added to post.");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } else {
                        Log.d("MapsFragment", "onMapReady: ");
                    }

                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}