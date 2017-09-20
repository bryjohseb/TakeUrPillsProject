package com.una.takeurpills;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.animation;
import static android.R.attr.name;
import static android.R.attr.type;

public class FindPillsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;
    private int PROXIMITY_RADIUS = 2000;
    private RequestQueue queue;
    private final String placeType = "pharmacy";
    private final int zoomLevel = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pills);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDetails();
                mMap.clear();
                setUpMap();
            }
        });
    }// fin onCreate


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideDetails();
            }
        });
        // Colocar dentro de onMapReady
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                //LatLng currentLocation = new LatLng(10.0024, -84.1198);
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //add pin at user's location
                placeUserMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                preparePlaces();
            }
        }
    }

    protected void placeUserMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_user_location)));
        mMap.addMarker(markerOptions);
    }

    private void preparePlaces(){
        String url;
        //url = getUrl(10.0024, -84.1198);
        url = getUrl(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        getNearbyPlaces(url);
    }

    private String getUrl(double latitude, double longitude) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&language=" + "es");
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private void getNearbyPlaces(String url) {
        queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, url, onPlacesLoaded, onPlacesError);
        queue.add(request);
    }

    private final Response.Listener<String> onPlacesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<HashMap<String, String>> nearbyPlacesList = null;
            Log.i("PostActivity", response);
            DataParser dataParser = new DataParser();
            nearbyPlacesList =  dataParser.parse(response);
            ShowNearbyPlaces(nearbyPlacesList);
        }
    };

    private final Response.ErrorListener onPlacesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
            Mensaje("Error en la respuesta del API. No es posible cargar los datos.");
        }
    };

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        if(nearbyPlacesList.size() == 0){
            Mensaje(getResources().getString(R.string.map_zeroResult_message));
        }else{
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute","Mostrando Lugares");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                String placeId = googlePlace.get("placeId");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pills_location)));
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(placeId);
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String id = (String) marker.getTag();
                    getPlace(id);
                    //Mensaje(id);
                    return false;
                }
            });
            LinearLayout detailsMarker = (LinearLayout) findViewById(R.id.layout_map_location_details);
            detailsMarker.setTranslationY(detailsMarker.getHeight());
        }
    }

    private void getPlace(String placeId){
        if(placeId != null) {
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                Place myPlace = places.get(0);
                                String myPlaceName = (String) myPlace.getName();
                                String myPlaceAddress = (String) myPlace.getAddress();
                                String myPlacePhone = (String) myPlace.getPhoneNumber();
                                float rating = (Float) myPlace.getRating();
                                int price_level = (Integer) myPlace.getPriceLevel();
                                String sitio_Web = String.valueOf(myPlace.getWebsiteUri());
                                updatePlaceInfo(myPlaceName, myPlaceAddress, myPlacePhone,sitio_Web,rating,price_level);
                                Log.d("myPlaceInfo", myPlaceName + ", " + myPlaceAddress + ", " + myPlacePhone);
                            } else {
                                hideDetails();
                                Mensaje(getResources().getString(R.string.map_placeNotFound_message));
                            }
                            places.release();
                        }
                    });
        }
    }

    private void updatePlaceInfo(String name, String address, String phone,String sitioWeb,float rating,int price_Level){
        showDetails();
        TextView tvName = (TextView) findViewById(R.id.tv_maps_location_name);
        TextView tvAddress = (TextView) findViewById(R.id.tv_maps_location_address);
        TextView tvPhone = (TextView) findViewById(R.id.tv_maps_location_phone);
        TextView page = (TextView) findViewById(R.id.tv_maps_sitioWeb);
        TextView valuation = (TextView) findViewById(R.id.tv_maps_rating);
        TextView expensive_cheap = (TextView) findViewById(R.id.tv_maps_price_Level);
        tvName.setText(name);
        tvAddress.setText(address);
        tvPhone.setText(phone.isEmpty() ? getResources().getString(R.string.map_noPhone_text) : phone);
        page.setText(sitioWeb.equals("null")? getResources().getString(R.string.map_noWebsite_text) : sitioWeb);
        valuation.setText(getResources().getString(R.string.map_ratingTag_text) + " " + (rating == -1 ? getResources().getString(R.string.map_noRate_text) : String.valueOf(rating)));
        expensive_cheap.setText(getResources().getString(R.string.map_priceTag_text) + " " + (price_Level == -1 ? getResources().getString(R.string.map_noPrice_text) : String.valueOf(price_Level)));
    }

    public void Mensaje(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void hideDetails(){
        final LinearLayout detailsMarker = (LinearLayout) findViewById(R.id.layout_map_location_details);
        detailsMarker.animate()
                .translationY(detailsMarker.getHeight())
                .alpha(0.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                detailsMarker.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDetails();
    }


    private void showDetails(){
        final LinearLayout detailsMarker = (LinearLayout) findViewById(R.id.layout_map_location_details);
        detailsMarker.setVisibility(View.VISIBLE);
        detailsMarker.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }
}
