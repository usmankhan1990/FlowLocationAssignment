package activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.flow.flowlocationassignment.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import Interfaces.DialogConfirmCallBack;
import Interfaces.DialogTitleDescriptionCallBack;
import helper.Constants;
import viewsHelper.UIView;


public class TrackingLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, DialogTitleDescriptionCallBack, DialogConfirmCallBack {

    Button btnDetails, btnTurnOn, btnTurnOff, btnPauseResume;
    SupportMapFragment supportMapFragment;
    LocationRequest mLocationRequest;
    LocationManager mLocationManager;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleMap mGoogleMap;
    ProgressDialog pDialog;
    boolean mGPSStatus, resumePause = false;
    public static final int PERMISSIONS_REQUEST_LOCATION = 2500;
    ParseObject parseObjectTripHistory;
    ParseObject parseObjectTripLocations;
    ParseGeoPoint parseGeoPoints;
    private ArrayList<LatLng> pointsArraylistForLine; //added
    Polyline line; //added
    private UIView uiView = UIView.getInstance();
    private Constants constantsInstance = Constants.getInstance();
    String currentUser = "";
    private boolean startLocationService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking_screen);
        mContext = getApplicationContext();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        pointsArraylistForLine = new ArrayList<LatLng>();

        init();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {

//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                checkPermission();  //Request Location Permission to user


            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void checkGPS() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mGPSStatus = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2500);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        try {
            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
            mCurrLocationMarker.showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(14)
                    .build();

            if (mGoogleMap != null) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }


            pointsArraylistForLine.add(latLng); //added
            mGoogleMap.clear();

            if(startLocationService == true && latLng!=null){

                redrawLine();
                parseGeoPoints = new ParseGeoPoint(latLng.latitude,latLng.longitude);
                startTracking(parseObjectTripHistory,parseGeoPoints);
                startLocationService = false;
            }


            if (constantsInstance.getpUser().getUsername() != null) {
                currentUser = constantsInstance.getpUser().getUsername();
            }

            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("User: " + currentUser + " "));
//        marker.showInfoWindow();
            marker.showInfoWindow();
        } catch (Exception exp) {
            Log.e("Exception at track", exp.getMessage());
        }


    }

    private void redrawLine() {

        //Clear the map before making any Polyline

        mGoogleMap.clear();

        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < pointsArraylistForLine.size(); i++) {
            LatLng point = pointsArraylistForLine.get(i);
            options.add(point);
        }
//        addMarker(); //add Marker in current position
        line = mGoogleMap.addPolyline(options); //add Polyline
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(TrackingLocationActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();


                        }
                        mGoogleMap.setMyLocationEnabled(true);


                    }

                } else {

                    Toast.makeText(this, "Permission denied, please try again later by allowing a location permission", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

        }

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        mGoogleMap.setMyLocationEnabled(true);

    }

    /**
     * OnClickListener for Tracking Detail button
     */

    final View.OnClickListener btnDetailListener = new View.OnClickListener() {
        public void onClick(final View v) {

            Intent intent = new Intent(TrackingLocationActivity.this, TripsActivity.class);
            startActivity(intent);

        }
    };

    /**
     * OnClickListener for Tracking PauseResume button
     */

    final View.OnClickListener btnPauseResumeListener = new View.OnClickListener() {
        public void onClick(final View v) {

            if(resumePause == false){
                resumePause = true;
                btnPauseResume.setText("Resume");
                startLocationService = false;
            }else{
                resumePause = false;
                btnPauseResume.setText("Pause");
                startLocationService = true;
            }


        }
    };

    /**
     * OnClickListener for Tracking TrackOff button
     */

    final View.OnClickListener btnTurnOffListener = new View.OnClickListener() {
        public void onClick(final View v) {

            uiView.setTripStartOffDialogListener(TrackingLocationActivity.this);
            uiView.confirmDialog(TrackingLocationActivity.this, "Do you want to finish trip", "Trip Off", false);

        }
    };

    /**
     * OnClickListener for Tracking TrackOn button
     */

    final View.OnClickListener btnTurnOnListener = new View.OnClickListener() {
        public void onClick(final View v) {

            uiView.setTripStartOffDialogListener(TrackingLocationActivity.this);
            uiView.confirmDialog(TrackingLocationActivity.this, "Do you want to start trip", "Trip On", true);

        }
    };

    void init() {
        btnDetails = findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(btnDetailListener);

        btnTurnOn = findViewById(R.id.btnTurnOn);
        btnTurnOn.setOnClickListener(btnTurnOnListener);

        btnTurnOff = findViewById(R.id.btnTurnOff);
        btnTurnOff.setOnClickListener(btnTurnOffListener);

        btnPauseResume = findViewById(R.id.btnPauseResume);
        btnPauseResume.setOnClickListener(btnPauseResumeListener);


        btnTurnOn.setEnabled(true);
        btnTurnOff.setEnabled(false);
        btnPauseResume.setEnabled(false);

    }

    public void sendTrip(String description, String title)

    {
            parseObjectTripHistory = new ParseObject("Trips");
        if(parseObjectTripHistory!=null){
            pDialog = uiView.showProgressBar(this);
            parseObjectTripHistory.put("description",description);
            parseObjectTripHistory.put("tripName",title);
            parseObjectTripHistory.put("user_id",constantsInstance.getpUser());
            parseObjectTripHistory.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    pDialog.dismiss();

                    if(e == null){
                        btnTurnOn.setEnabled(false);
                        startLocationService = true;
                        pointsArraylistForLine.clear();
                        btnTurnOff.setEnabled(true);
                        btnPauseResume.setEnabled(true);
                        parseObjectTripHistory.getObjectId();
                    }else{
                        Toast.makeText(TrackingLocationActivity.this,"Please try again later...",Toast.LENGTH_LONG).show();
                    }
                }
            });



        }

    }

    public void startTracking(ParseObject parseObjectLocations, ParseGeoPoint pGeoPoint)

    {
        if(parseObjectTripHistory!=null && startLocationService == true){

            parseObjectTripLocations = new ParseObject("TripHistory");
            parseObjectTripLocations.put("trip_id",parseObjectLocations);
            parseObjectTripLocations.put("latlong",pGeoPoint);
            parseObjectTripLocations.put("user_id",constantsInstance.getpUser());
            parseObjectTripLocations.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e == null){
                        startLocationService = true;
                    }else{
                        Toast.makeText(TrackingLocationActivity.this,"Please try again later...",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    @Override
    public void sendDescriptionTitleTrip(String description, String title) {
        sendTrip(description, title);
    }

    @Override
    public void sendStartStopTrip(boolean startTrip) {

        if(startTrip == true){
            uiView.setiDialogTitleDescriptionListener(TrackingLocationActivity.this);
            uiView.showTripTitleDescDialogBox(TrackingLocationActivity.this);
        }else{
            btnTurnOn.setEnabled(true);
            btnTurnOff.setEnabled(false);
            btnPauseResume.setEnabled(false);
            btnPauseResume.setText("Pause");

            startLocationService = false;
            pointsArraylistForLine.clear();
            parseObjectTripHistory = null;
        }

    }
}
