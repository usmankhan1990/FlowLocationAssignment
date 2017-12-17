package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.flow.flowlocationassignment.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseObject;

import helper.Constants;

/**
 * Created by UsmanKhan on 12/14/17.
 */

public class TrackingDetailScreen extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    public static final int PERMISSIONS_REQUEST_LOCATION = 2500;
    SupportMapFragment supportMapFragment;
    LocationRequest mLocationRequest;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleMap mGoogleMap;
    TextView txtStartTime, txtEndTime, txtDistance, txtDiscription;
    Polyline lineOnMap;
    String currentUser = "";
    LatLng allPointsLatLong;
    LatLng pointFirstLatLong;
    double distance = 0.0;
    CameraPosition cameraPosition;
    private Constants constantsInstance = Constants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_detail_screen);
        mContext = getApplicationContext();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
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
                mGoogleMap.setMyLocationEnabled(false);
            } else {
                checkPermission();  //Request Location Permission to user


            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(false);
        }


        try {

            redrawLine();

            if (constantsInstance.getpUser().getUsername() != null) {
                currentUser = constantsInstance.getpUser().getUsername();
            }


        } catch (Exception exp) {
            Log.e("Exception at track", exp.getMessage());
        }


    }

    /**
     * <p>This function builds Google Api Client.</p>
     */

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

    }


    /**
     * <p>This function will make a line on the map from start point to end point.
     *    It took Location coordinates and displays it on the map.
     * </p>
     */

    private void redrawLine() {

        //Clear the map before making any Polyline

        mGoogleMap.clear();

        PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);
        for (int i = 0; i < constantsInstance.getLocationPoints().size(); i++) {
            allPointsLatLong = constantsInstance.getLocationPoints().get(i);
            pointFirstLatLong = constantsInstance.getLocationPoints().get(0);
            options.add(allPointsLatLong);
        }

        distance = constantsInstance.distance(pointFirstLatLong.latitude, pointFirstLatLong.longitude, allPointsLatLong.latitude, allPointsLatLong.longitude, 'K');
        txtDistance.setText(constantsInstance.decimalFormat.format(distance) + " km");


        LatLng latLng = new LatLng(allPointsLatLong.latitude, allPointsLatLong.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(allPointsLatLong.latitude, allPointsLatLong.longitude))
                .zoom(14)
                .build();

        if (mGoogleMap != null) {
            cameraPosition = new CameraPosition.Builder().target(new LatLng(allPointsLatLong.latitude, allPointsLatLong.longitude)).zoom(12).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            lineOnMap = mGoogleMap.addPolyline(options); //add Polyline
            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(allPointsLatLong).title("Trip Ended Here"));
            marker.showInfoWindow();
        }
        getDates(constantsInstance.getpObjectTrackingDetail());


    }

    /**
     * <p>This function checks run time permission.
     * Checking Location Permission for accessing user location on map
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

                                ActivityCompat.requestPermissions(TrackingDetailScreen.this,
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
     * <p>This function get Date from server.
     * Initializing views
     * </p>
     */
    void init() {
        txtStartTime = findViewById(R.id.txtStartTime);
        txtEndTime = findViewById(R.id.txtEndTime);
        txtDistance = findViewById(R.id.txtDistance);
        txtDiscription = findViewById(R.id.txtDiscription);
    }


    /**
     * <p>This function get Date from server.
     *    It is converting Date format by using SimpleDateFormat.
     *    It is also converting Date format as per UTC time standard.
     *    It is showing Start and End time with description.
     * </p>
     *@param parseObjectDate - Parse Object from server to convert
     */

    private void getDates(ParseObject parseObjectDate) {

        if (parseObjectDate.has("startTime")) {

            txtStartTime.setText(constantsInstance.dateFormatGmt.format(constantsInstance.getDateTimeUTC(parseObjectDate.getDate("startTime"))));
        }
        if (constantsInstance.getpObjectTrackingDetail().has("endTime")) {
            txtEndTime.setText(constantsInstance.dateFormatGmt.format(constantsInstance.getDateTimeUTC(parseObjectDate.getDate("endTime"))));
        }
        if (constantsInstance.getpObjectTrackingDetail().has("description")) {
            txtDiscription.setText(parseObjectDate.getString("description"));
        }

    }


    @Override
    public void onBackPressed() {
        constantsInstance.getLocationPoints().clear();
        super.onBackPressed();
    }
}
