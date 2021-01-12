package com.wallpo.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.wallpo.android.R;
import com.wallpo.android.explorefragment.risingartistsadapter;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.wallpo.android.fragment.ProfileFragment.getTimestamp;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    SharedPreferences sharepref;
    boolean GpsStatus;
    RelativeLayout locationlay, enablelocation;
    AppCompatTextView browseanyway, maplocation;
    Boolean mapsShow = false;
    Boolean runQuery = true;
    Button buttn;
    ConstraintLayout mainlay;
    int zoomMapLevel = 0;
    LatLng gotloc;
    AppCompatImageView searchimg;
    String latitudes, longitudes = "";
    TextInputEditText searchtext;
    LatLng selectedlocation;
    SpinKitView spin_kit;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        sharepref = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        buttn = view.findViewById(R.id.buttn);
        locationlay = view.findViewById(R.id.locationlay);
        locationlay.setVisibility(View.GONE);

        enablelocation = view.findViewById(R.id.enablelocation);
        maplocation = view.findViewById(R.id.maplocation);

        searchimg = view.findViewById(R.id.searchimg);
        searchtext = view.findViewById(R.id.searchtext);
        spin_kit = view.findViewById(R.id.spin_kit);

        updatecode.analyticsFirebase(getContext(), "map_user", "map_user");

        searchimg.setOnClickListener(view1 -> {
            TransitionManager.beginDelayedTransition(mainlay);

            updatecode.analyticsFirebase(getContext(), "map_searched", "map_searched");

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

                spin_kit.setVisibility(View.VISIBLE);
                String location = searchtext.getText().toString();

                List<Address> addressList = null;

                if (location != null || !location.isEmpty()) {

                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        spin_kit.setVisibility(View.GONE);

                        mMap.clear();

                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                            String addresss = addresses.get(0).getAddressLine(0);
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

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Location: " + maplocation.getText().toString()));

                        mMap.addMarker(new MarkerOptions().position(gotloc).
                                icon(BitmapDescriptorFactory.fromBitmap(
                                        createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwann Sayyed")))).setTitle("Me");


                        selectedlocation = latLng;

                        CameraPosition cameraPosition = new CameraPosition.Builder().
                                target(latLng).tilt(56).zoom(15).bearing(0).
                                build();

                        adddataMap(Double.toString(address.getLatitude()), Double.toString(address.getLongitude()));

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(), "Enter a vaild location.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    spin_kit.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Enter a vaild location.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            return false;
        });

        mainlay = view.findViewById(R.id.mainlay);

        enablelocation.setOnClickListener(view1 -> {

            Dexter.withContext(getActivity())
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Dexter.withContext(getActivity())
                                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                                            mapsShow = true;
                                            startActivity(intent1);
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        });

        buttn.setOnClickListener(view1 -> {
            rundata();
            onMapReady(mMap);
        });

        browseanyway = view.findViewById(R.id.browseanyway);

        browseanyway.setOnClickListener(view1 -> {

            Dexter.withContext(getActivity())
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Dexter.withContext(getActivity())
                                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                            locationlay.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        });

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Dexter.withContext(getActivity())
                                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        rundata();
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getContext(), getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


        rundata();


        new Handler().postDelayed(() -> {
            try {
                buttn.performClick();

                adddataMap(Double.toString(gotloc.latitude), Double.toString(gotloc.longitude));

            } catch (NullPointerException e) {
                Log.d(TAG, "onCreateView: " + e);
            }

        }, 3000);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus) {

            locationlay.setVisibility(View.GONE);

            if (mapsShow) {

                spin_kit.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), getResources().getString(R.string.gettingyourloc), Toast.LENGTH_LONG).show();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            spin_kit.setVisibility(View.GONE);
                            return;
                        }
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(getActivity(), location -> {
                                    if (location != null) {

                                        Toast.makeText(getContext(), "" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                                        mapsShow = false;
                                        runQuery = false;

                                        buttn.performClick();

                                        spin_kit.setVisibility(View.GONE);

                                        adddataMap(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));

                                        new Handler().postDelayed(() -> {

                                            buttn.performClick();

                                        }, 2500);

                                    } else {
                                        runQuery = true;
                                        spin_kit.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "" + getResources().getString(R.string.errorgetlocation), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        if (runQuery) {
                            handler.postDelayed(this, 2500);
                        }
                    }
                };

                handler.postDelayed(runnable, 1500);

            }

        } else {

            mapsShow = false;

            spin_kit.setVisibility(View.GONE);
            locationlay.setVisibility(View.VISIBLE);
        }
    }

    public void rundata() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        mMap.setOnMarkerClickListener(marker -> {

            BottomSheetDialog dialog = new BottomSheetDialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.mapscustomposts);

            final LinearLayoutManager linearLayoutManager;
            final List<Photos> postsList = new ArrayList<>();

            SpinKitView spin_kit =  dialog.findViewById(R.id.spin_kit);

            RecyclerView recyclerView =  dialog.findViewById(R.id.locationposts);

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);

            risingartistsadapter risingartistsadapter = new risingartistsadapter(getContext(), postsList);

            recyclerView.setAdapter(risingartistsadapter);

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder()
                    .add("latlong", marker.getPosition().latitude + "/" + marker.getPosition().longitude).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.mappostslist)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("explorepostsadapter", "onFailure: ", e);

                    if (getActivity() == null){
                        return;
                    }
                    getActivity().runOnUiThread(() -> spin_kit.setVisibility(View.GONE));
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    if (getActivity() == null){
                        return;
                    }
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

                                int photoid = Object.getInt("photoid");
                                String caption = Object.getString("caption");
                                String categoryid = Object.getString("categoryid");
                                String albumid = Object.getString("albumid");
                                String datecreated = Object.getString("datecreated");
                                String dateshowed = Object.getString("dateshowed");
                                String link = Object.getString("link");
                                String userid = Object.getString("userid");
                                String imagepath = Object.getString("imagepath");
                                String likes = Object.getString("likes");
                                int trendingcount = Object.getInt("trendingcount");

                                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                        dateshowed, trendingcount, likes);
                                postsList.add(photo);
                            }

                            spin_kit.setVisibility(View.GONE);
                            risingartistsadapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            dialog.show();

            return false;
        });

        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstylenew));

        if (!success) {
            Log.d("MapFragment", "onMapReady: Error");
        }

        String imagepath = sharepref.getString("profilephotouser", "");
        if (imagepath.isEmpty()) {

            if (!sharepref.getString("wallpouserid", "").isEmpty()) {


                spin_kit.setVisibility(View.VISIBLE);

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("userid", sharepref.getString("wallpouserid", ""))
                        .add("fcm", sharepref.getString("fcmtoken", "")).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.userinfo)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("ProfileFragment", "onFailure: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(() -> {

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String usernametxt = object.getString("username").trim();
                                    String displaynametxt = object.getString("displayname").trim();
                                    String profilephoto = object.getString("profilephoto").trim();

                                    String verifiedtxt = object.getString("verified").trim();
                                    String description = object.getString("description").trim();
                                    String categorytxt = object.getString("category").trim();
                                    String websites = object.getString("websites").trim();
                                    String backphoto = object.getString("backphoto").trim();
                                    String phonenumber = object.getString("phonenumber").trim();
                                    String emailverified = object.getString("emailverified").trim();
                                    String poststxt = object.getString("posts").trim();
                                    String subscribed = object.getString("subscribed").trim();
                                    String subscribers = object.getString("subscribers").trim();
                                    String emailids = object.getString("email").trim();


                                    sharepref.edit().putString("emailid", emailids).apply();
                                    sharepref.edit().putString("usernameuser", usernametxt).apply();
                                    sharepref.edit().putString("displaynameuser", displaynametxt).apply();
                                    sharepref.edit().putString("profilephotouser", profilephoto).apply();
                                    sharepref.edit().putString("verifieduser", verifiedtxt).apply();

                                    sharepref.edit().putString("descriptionuser", description).apply();
                                    sharepref.edit().putString("categoryuser", categorytxt).apply();
                                    sharepref.edit().putString("websitesuser", websites).apply();
                                    sharepref.edit().putString("backphotouser", backphoto).apply();
                                    sharepref.edit().putString("subscribeduser", subscribed).apply();
                                    sharepref.edit().putString("subscribersuser", subscribers).apply();
                                    sharepref.edit().putString("phonenumberuser", phonenumber).apply();
                                    sharepref.edit().putString("emailverifieduser", emailverified).apply();
                                    sharepref.edit().putString("profilepragdata", getTimestamp()).apply();
                                    sharepref.edit().putString("postsusers", poststxt).apply();

                                    spin_kit.setVisibility(View.GONE);
                                    rundata();

                                    createCustomMarker(getContext(), R.mipmap.profilepic, "");

                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                });
            }

        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            spin_kit.setVisibility(View.GONE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        gotloc = new LatLng(location.getLatitude(), location.getLongitude());

                        spin_kit.setVisibility(View.GONE);

                        sharepref.edit().putString("maplat", String.valueOf(location.getLatitude())).apply();
                        sharepref.edit().putString("maplong", String.valueOf(location.getLongitude())).apply();

                        if (!sharepref.getString("wallpouserid", "").isEmpty()) {

                            mMap.addMarker(new MarkerOptions().position(gotloc).
                                    icon(BitmapDescriptorFactory.fromBitmap(
                                            createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwann Sayyed")))).setTitle("Me");

                        } else {

                            mMap.addMarker(new MarkerOptions()
                                    .position(gotloc)
                                    .title("Sign In for More"));
                        }

                        selectedlocation = gotloc;

                        adddataMap(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));

                        CameraPosition cameraPosition = new CameraPosition.Builder().
                                target(gotloc).tilt(56).zoom(15).bearing(0).
                                build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } else {
                        Log.d("MapsFragment", "onMapReady: ");
                        spin_kit.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "" + getResources().getString(R.string.errorgetlocation), Toast.LENGTH_SHORT).show();
                    }
                });

        mMap.setOnMapClickListener(latLng -> {

            int zoomLevel = (int) mMap.getCameraPosition().zoom;

            if (zoomLevel > 2) {
                zoomMapLevel = zoomLevel;

                spin_kit.setVisibility(View.GONE);

                mMap.clear();

                selectedlocation = latLng;

                mMap.addMarker(new MarkerOptions()
                        .position(selectedlocation));

                try {

                    mMap.addMarker(new MarkerOptions().position(gotloc).
                            icon(BitmapDescriptorFactory.fromBitmap(
                                    createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwann Sayyed")))).setTitle("Me");
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "onMapReady: " + e);
                }


                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(selectedlocation).tilt(56).zoom(14).bearing(0).
                        build();


                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                adddataMap(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));

                String maplocation = "";

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(gotloc.latitude, gotloc.longitude, 1);

                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    maplocation = "";

                    if (knownName != null) {
                        maplocation = knownName;
                    }

                    if (city != null) {
                        maplocation = maplocation + ", " + city;
                    }

                    if (state != null) {
                        maplocation = maplocation + ", " + state;
                    }

                    if (country != null) {
                        maplocation = maplocation + ", " + country;
                    }

                    if (maplocation.isEmpty()) {
                        maplocation = "Unknown Location to show address, Location added to post.";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(maplocation));



            } else {
                mMap.clear();

                mMap.addMarker(new MarkerOptions().position(gotloc).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwan Sayyed"))));

                Toast.makeText(getActivity(), "Please zoom more to set location and view posts on map.", Toast.LENGTH_SHORT).show();


                spin_kit.setVisibility(View.GONE);
            }

        });

        mMap.setOnCameraMoveStartedListener(i -> {
            if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                int zoomLevel = (int) mMap.getCameraPosition().zoom;

                if (zoomLevel > 2) {
                    zoomMapLevel = zoomLevel;

                    /*String maplocation = "";

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(gotloc.latitude, gotloc.longitude, 1);

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        maplocation = "";

                        if (knownName != null) {
                            maplocation = knownName;
                        }

                        if (city != null) {
                            maplocation = maplocation + ", " + city;
                        }

                        if (state != null) {
                            maplocation = maplocation + ", " + state;
                        }

                        if (country != null) {
                            maplocation = maplocation + ", " + country;
                        }

                        if (maplocation.isEmpty()) {
                            maplocation = "Unknown Location to show address, Location added to post.";
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMap.addMarker(new MarkerOptions()
                            .position(gotloc)
                            .title(maplocation));
*/

                } else {
                    mMap.clear();

                    mMap.addMarker(new MarkerOptions().position(gotloc).
                            icon(BitmapDescriptorFactory.fromBitmap(
                                    createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwann Sayyed")))).setTitle("Me");

                }

            }
        });

    }

    private void adddataMap(String latitude, String longitude) {

        String lats = latitude.substring(0, latitude.indexOf("."));
        String longts = longitude.substring(0, longitude.indexOf("."));


        if (lats.equals(latitudes) && longts.equals(longitudes)) {

            String mapData = sharepref.getString("mapdata", "");

            if (mapData.equals("[]") || mapData.isEmpty()) {

                getMapData(lats, longts);
            } else {

                updateMapData(lats, longts);

            }

            return;
        }

        getMapData(lats, longts);

    }

    private void updateMapData(String latss, String longtss) {
        String mapData = sharepref.getString("mapdata", "");

        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .position(selectedlocation));

        mMap.addMarker(new MarkerOptions().position(gotloc).
                icon(BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwan Sayyed")))).setTitle("Me");

        try {
            JSONArray array = new JSONArray(mapData);

            for (int i = 0; i < array.length(); i++) {

                JSONObject Object = array.getJSONObject(i);

                String location = Object.getString("location");

                if (location.isEmpty()) {

                    return;
                }

                if (!location.contains("/")) {

                    return;
                }

                String lats = location.substring(0, location.indexOf("/"));
                String longs = location.substring(location.indexOf("/") + 1, location.length());


                LatLng gotlocs = new LatLng(Double.parseDouble(lats), Double.parseDouble(longs));

                mMap.addMarker(new MarkerOptions().position(gotlocs).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                customdataLocation(getActivity(), R.mipmap.profilepic, "Rizwan Sayyed"))));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMapData(String lats, String longts) {


        latitudes = lats;
        longitudes = longts;

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder()
                .add("lats", lats)
                .add("longs", longts).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.mapdata)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "" + getContext().getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {

                    mMap.clear();

                    mMap.addMarker(new MarkerOptions()
                            .position(selectedlocation));

                    try {

                        mMap.addMarker(new MarkerOptions().position(gotloc).
                                icon(BitmapDescriptorFactory.fromBitmap(
                                        createCustomMarker(getActivity(), R.mipmap.profilepic, "Rizwan Sayyed")))).setTitle("Me");

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONArray array = new JSONArray(data);

                        sharepref.edit().putString("mapdata", data).apply();

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject Object = array.getJSONObject(i);

                            String location = Object.getString("location");

                            if (location.isEmpty()) {

                                return;
                            }

                            if (!location.contains("/")) {

                                return;
                            }

                            String lats = location.substring(0, location.indexOf("/"));
                            String longs = location.substring(location.indexOf("/") + 1, location.length());


                            LatLng gotlocs = new LatLng(Double.parseDouble(lats), Double.parseDouble(longs));

                            try {
                                mMap.addMarker(new MarkerOptions().position(gotlocs).
                                        icon(BitmapDescriptorFactory.fromBitmap(
                                                customdataLocation(getActivity(), R.mipmap.profilepic, "ME"))));


                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }

    public Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        AppCompatImageView markerImage = marker.findViewById(R.id.imageview);

        try {

            Picasso.get().load(sharepref.getString("profilephotouser", ""))
                    .into(new com.squareup.picasso.Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            markerImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

            Glide.with(getActivity()).asBitmap().load(sharepref.getString("profilephotouser", "")).centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.profilepic)
                    .skipMemoryCache(false)
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            getActivity().runOnUiThread(() -> {
                                markerImage.setImageBitmap(resource);
                                markerImage.setImageBitmap(resource);
                            });
                            return false;
                        }
                    }).submit();
        } catch (IllegalArgumentException e) {
            Log.e("additionalactivity", "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e("act", "onBindViewHolder: ", e);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public Bitmap customdataLocation(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customdata_marker_layout, null);

        AppCompatImageView markerImage = marker.findViewById(R.id.imageview);

        //   markerImage.setImageBitmap(bitmap);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(25, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}