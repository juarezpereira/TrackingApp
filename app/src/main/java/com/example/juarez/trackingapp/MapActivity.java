package com.example.juarez.trackingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.Utils.Constants;
import com.example.juarez.trackingapp.Utils.PlayServiceUtil;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final int REQUEST_LOCATION = 1000;
    protected static final int REQUEST_CHECK_SETTINGS = 2000;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;

    private AlertDialog mAlertDialog;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRefUser;

    private GeoFire mGeoFire;

    private LatLng latLngOrigin;
    private LatLng latLngDestination;

    private Polyline mPolyline;
    private Circle mSearchCircle;
    private Marker mMarkerDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlayServiceUtil.checkGooglePlayService(this);

        if (mGoogleApiCliente == null) {
            this.mGoogleApiCliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        onCreateLocationListener();
        onSetupGeoFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setOnline(false);

                    mRefUser.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.signOut();
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.CLOSE_APP);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiCliente.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiCliente.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiCliente.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showAlertDialog();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onCreateLocationListener() {
        this.mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);

        LocationSettingsRequest.Builder settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> resultPendingResult = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiCliente, settingsRequest.build());

        resultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e(MainActivity.class.getSimpleName(), "SETTINGS LOCATION SATISFIED");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e(MainActivity.class.getSimpleName(), "SETTINGS LOCATION NO SATISFIED, BUT CAN BE FIXED");
                        try {
                            status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(MainActivity.class.getSimpleName(), e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(MainActivity.class.getSimpleName(), "SETTINGS LOCATION NO SATISFIED");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdate();
            }
        }
    }

    protected void startLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showAlertDialog();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location l) {
        if (l == null)
            return;

        latLngOrigin = new LatLng(l.getLatitude(), l.getLongitude());

        if (mSearchCircle != null) {
            mSearchCircle.setCenter(latLngOrigin);
        } else {
            mSearchCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(latLngOrigin)
                    .strokeColor(Color.parseColor("#4D2196F3"))
                    .radius(1000));
        }

        mGeoFire.setLocation(mUser.getUid(), new GeoLocation(l.getLatitude(), l.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.e(MapActivity.class.getSimpleName(), "Location failure saved server");
                        } else {
                            Log.e(MapActivity.class.getSimpleName(), "Location saved server");
                        }
                    }
                });

        startRoute(latLngOrigin, latLngDestination);
    }

    protected void showAlertDialog() {
        this.mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Permissões")
                .setMessage("Permissão de localização necessaria para aplicação. Ativar?")
                .setPositiveButton("PERMITIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        mAlertDialog.show();
    }

    public void onSetupGeoFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference mRefRoot = FirebaseDatabase.getInstance().getReference();
        mRefUser = mRefRoot.child(Constants.USERS).child(mUser.getUid());
        mGeoFire = new GeoFire(mRefRoot.child(Constants.GEO_FIRE));

        DatabaseReference mRefUserTracking = mRefUser.child(Constants.TRACKING);
        mRefUserTracking.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onGeoFireListener(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onGeoFireListener(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGeoFireListener(String child) {
        if (mGeoFire == null)
            return;

        mGeoFire.getLocation(child, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location == null)
                    return;

                latLngDestination = new LatLng(location.latitude, location.longitude);
                startRoute(latLngOrigin, latLngDestination);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startRoute(LatLng start, final LatLng end) {
        if (start == null || end == null) return;

        Routing mRouting = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .waypoints(start, end)
                .alternativeRoutes(false)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {

                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {
                        if (mPolyline != null) mPolyline.remove();

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.BLUE);
                        polylineOptions.width(12);
                        polylineOptions.addAll(arrayList.get(shortestRouteIndex).getPoints());

                        mPolyline = mGoogleMap.addPolyline(polylineOptions);

                        if (mMarkerDestination != null) mMarkerDestination.remove();

                        MarkerOptions options = new MarkerOptions();
                        options.position(end);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        mMarkerDestination = mGoogleMap.addMarker(options);

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(latLngOrigin)
                                .include(latLngDestination)
                                .build();

                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                }).build();
        mRouting.execute();
    }

}