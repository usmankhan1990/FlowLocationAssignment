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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import Interfaces.DialogConfirmCallBack;
import Interfaces.DialogTitleDescriptionCallBack;
import Interfaces.LocationSaveCallBack;
import Interfaces.TripsCallback;
import helper.Constants;
import Controller.ServiceCalls;
import viewsHelper.UIView;

/**
 * Created by Usman Khan on 13/12/2017.
 *
 * This Activity screen shows user live location.
 * It can start tracking user location and to make it turn off, pause resume state.
 * It can also provide an access for trip detail screen.
 * Google map API's are using here, along with commented code for background service(If it is also a requirement).
 */

public class TrackingLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, DialogTitleDescriptionCallBack, DialogConfirmCallBack, TripsCallback
        , LocationSaveCallBack {

    Button btnDetails, btnTurnOn, btnTurnOff, btnPauseResume, btnLogout;
    SupportMapFragment supportMapFragment;
    LocationRequest mLocationRequest;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleMap mGoogleMap;
    public static final int PERMISSIONS_REQUEST_LOCATION = 2500;
    ParseObject parseObjectTripHistory;
    ParseGeoPoint parseGeoPoints;
    private ArrayList<LatLng> pointsArraylistForLine;
    Polyline line;
    private UIView uiView = UIView.getInstance();
    private Constants constantsInstance = Constants.getInstance();
    ParseUser pUser = ParseUser.getCurrentUser();
    private ServiceCalls serviceCallsInstance = ServiceCalls.getInstance();
    String currentUser = "";
    private boolean startLocationService = false, resumePause = false, turnOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking_screen);
        mContext = getApplicationContext();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        pointsArraylistForLine = new ArrayList<LatLng>();
        constantsInstance.setpUser(pUser);
        init();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {


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

            if (startLocationService == true && latLng != null && turnOff == true) {

                redrawLine();
                parseGeoPoints = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                savingLocationToServer(parseGeoPoints, startLocationService);
                startLocationService = false;
            }


            if (constantsInstance.getpUser().getUsername() != null) {
                currentUser = constantsInstance.getpUser().getUsername();
            }

            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("User: " + currentUser + " "));
            marker.showInfoWindow();
        } catch (Exception exp) {
            Log.e("Exception at track", exp.getMessage());
        }


    }

    /**<p>
     * This methods is used to redraw a line on a map.
     * </p>
     */

    private void redrawLine() {

        //Clear the map before making any Polyline

        mGoogleMap.clear();

        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < pointsArraylistForLine.size(); i++) {
            LatLng point = pointsArraylistForLine.get(i);
            options.add(point);
        }

        line = mGoogleMap.addPolyline(options);
    }

    /**<p>
     * This methods checks runtime permission for allowing a user to use map functionality.
     * </p>
     */

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

            if (resumePause == false) {
                resumePause = true;
                btnPauseResume.setText("Resume");
                startLocationService = false;
                turnOff = false;
                // Start service when Resume button is pressed
                /* startService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */

            } else {
                resumePause = false;
                btnPauseResume.setText("Pause");
                startLocationService = true;
                turnOff = true;
                // Stop service when Pause button is pressed
                /* stopService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */

            }


        }
    };

    /**
     * OnClickListener for Logout button
     */

    final View.OnClickListener btnLogOutListener = new View.OnClickListener() {
        public void onClick(final View v) {

            uiView.logOutDialog(TrackingLocationActivity.this);

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

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(btnLogOutListener);

        btnTurnOn.setEnabled(true);
        btnTurnOff.setEnabled(false);
        btnPauseResume.setEnabled(false);

    }


    /**
     * OnClickListener for Tracking TrackOff button
     */

    final View.OnClickListener btnTurnOffListener = new View.OnClickListener() {
        public void onClick(final View v) {

            uiView.setTripStartOffDialogListener(TrackingLocationActivity.this);
            uiView.confirmDialog(TrackingLocationActivity.this, "Do you want to finish trip", "Trip Off", false);
            turnOff = false;
        }
    };

    /**
     * OnClickListener for Tracking TrackOn button
     */

    final View.OnClickListener btnTurnOnListener = new View.OnClickListener() {
        public void onClick(final View v) {
            turnOff = true;
            uiView.setTripStartOffDialogListener(TrackingLocationActivity.this);
            uiView.confirmDialog(TrackingLocationActivity.this, "Do you want to start trip", "Trip On", true);

        }
    };

    /**
     * Call back response for showing a Dialog box for description and title input
     * <p/>
     *
     * @param startTrip - This boolean shows success from server as true and will start on or as per value.
     */

    @Override
    public void sendStartStopTrip(boolean startTrip) {

        if (startTrip == true) {
            uiView.setiDialogTitleDescriptionListener(TrackingLocationActivity.this);
            uiView.showTripTitleDescDialogBox(TrackingLocationActivity.this);
        } else {
            btnTurnOn.setEnabled(true);
            btnTurnOff.setEnabled(false);
            btnPauseResume.setEnabled(false);
            btnPauseResume.setText("Pause");

            startLocationService = false;
            turnOff = false;
            pointsArraylistForLine.clear();
            parseObjectTripHistory = null;

            // Stop service when switch is turned OFF
            /* stopService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */

        }

    }

    /**
     * Callback from Dialog for getting description and title for sending them to server.
     * <p/>
     *
     * @param description - This is a description input from user.
     * @param title       - This is a title input from user.
     */


    @Override
    public void sendDescriptionTitleTrip(String description, String title) {
        serviceCallsInstance.setTripsCallback(TrackingLocationActivity.this);
        serviceCallsInstance.sendTrip(description, title, TrackingLocationActivity.this);
    }


    /**
     * Call back response for starting of trip. After this success, we will be able to start saving live location data on Server
     * <p/>
     *
     * @param isButtonOn    - This boolean shows ON(true) and OFF(false) for Track On Button
     * @param isButtonOff   - This boolean shows ON(true) and OFF(false) for Track Off Button
     * @param isButtonPause - This boolean shows ON(true) and OFF(false) for Pause Button
     * @param startLocation - This boolean use to start location service with True means start.
     */

    @Override
    public void serverResponseForStartTrip(boolean isButtonOn, boolean isButtonOff, boolean isButtonPause, boolean startLocation) {
        btnTurnOn.setEnabled(isButtonOn);
        startLocationService = startLocation;
        pointsArraylistForLine.clear();
        btnTurnOff.setEnabled(isButtonOff);
        btnPauseResume.setEnabled(isButtonPause);
    }

    /**
     * This function is sending location coordinated to server to make track history.
     * <p/>
     *
     * @param pGeoPoint     - These are user coordinates Latitude and Longitude
     * @param startLocation - This boolean shows startLocation(true) and startLocation(false) for start or stop of server
     */

    public void savingLocationToServer(ParseGeoPoint pGeoPoint, boolean startLocation)

    {
        serviceCallsInstance.setLocationSaveCallBack(TrackingLocationActivity.this);
        serviceCallsInstance.savingLocationToServer(pGeoPoint, startLocation, TrackingLocationActivity.this);

    }

    /**
     * Response from server to start tracking.
     * <p/>
     *
     * @param startLocation - boolean value to start(true) a location tracking.
     */

    @Override
    public void serverResponseForLocationSaving(boolean startLocation) {

        startLocationService = startLocation;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // On Application destroy, stop the service
          /*  stopService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */
    }
}
