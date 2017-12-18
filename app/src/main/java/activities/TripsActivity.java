package activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import adapter.TrackingItemsAdapter;
import helper.Constants;
import viewsHelper.UIView;

/**
 * Created by UsmanKhan on 12/14/17.
 */

public class TripsActivity extends AppCompatActivity {


    ParseQuery pQueryTrips = new ParseQuery("Trips");
    ParseQuery pQueryTripsDetail = new ParseQuery("TripHistory");
    RecyclerView rv_tracking_items;
    TrackingItemsAdapter tripsAdapter;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    LinearLayoutManager layoutManager;
    private Constants constants = Constants.getInstance();
    private UIView uiView = UIView.getInstance();
    private ArrayList<LatLng> pointsGeoPoints = new ArrayList<LatLng>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list);

        init();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        tripsAdapter = new TrackingItemsAdapter(this);

        rv_tracking_items.setHasFixedSize(true);
        rv_tracking_items.setItemViewCacheSize(2);
        rv_tracking_items.setItemAnimator(new DefaultItemAnimator());
        rv_tracking_items.setAdapter(tripsAdapter);
        rv_tracking_items.setLayoutManager(layoutManager);

        tripsAdapter.notifyDataSetChanged();

        tripsAdapter.setOnItemClickListener(new TrackingItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                ParseObject parseObjectTrackingDetail = constants.getTrackingObjectsList().get(position);

                if (parseObjectTrackingDetail.has("tripName")) {

                    // Setting ParseObject for extracting location details in Constant Class.
                    constants.setpObjectTrackingDetail(parseObjectTrackingDetail);

                    // Calling method to extract Details of clicked trip.
                    getTrackingItemDetail(parseObjectTrackingDetail);

                } else {

                    Toast.makeText(TripsActivity.this, "Please try again later", Toast.LENGTH_LONG).show();
                }


            }
        });


        getTrackingItems(constants.getpUser());
    }


    /**
     * <p>This loads list of Tracking Trips.</p>
     * Showing a data with Descending order as per Creating time.
     * This method will also work to get the detail of particular trip by setting that particular trip object.
     *
     * @param parseUserCurrentObject - Current User object from constant class
     */

    public void getTrackingItems(ParseUser parseUserCurrentObject)

    {

        pQueryTrips.whereEqualTo("user_id", parseUserCurrentObject);
        pQueryTrips.orderByDescending("createdAt");
        progressDialog = uiView.showProgressBar(this);
        pQueryTrips.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                progressDialog.dismiss();

                if (e == null) {

                    constants.setTrackingObjectsList(objects);
                    loadData(objects);

                } else {


                }

            }

        });


    }


    /**
     * <p>This will extract a detail of Particular Trip by passing Trip Object to it.</p>
     * With successful response sets all location coordinates in Constant class for using it on Detail Map screen.
     *
     * @param parseObjectTrackingDetail - Particular Trip Object
     */
    public void getTrackingItemDetail(ParseObject parseObjectTrackingDetail)

    {

        pQueryTripsDetail.whereEqualTo("trip_id", parseObjectTrackingDetail);

        progressDialog = uiView.showProgressBar(this);
        pQueryTripsDetail.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (e == null && objects.size() > 0) {
                    for (ParseObject parseObjectLocations : objects) {
                        LatLng latLngPoints = new LatLng(parseObjectLocations.getParseGeoPoint("latlong").getLatitude(), parseObjectLocations.getParseGeoPoint("latlong").getLongitude());
                        pointsGeoPoints.add(latLngPoints);
                    }


                    constants.setLocationPoints(pointsGeoPoints);

                    progressDialog.dismiss();

                    Intent intent = new Intent(TripsActivity.this, TrackingDetailScreen.class);
                    startActivity(intent);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TripsActivity.this, "Please try again later", Toast.LENGTH_LONG).show();
                }


            }

        });


    }

    /**
     * <p>This loads list of parse object to Adapter to fill Recycler View.</p>
     * It is adding adapter with values.
     *
     * @param objects - List of Parse Objects for showing list
     */
    public void loadData(List<ParseObject> objects) {

        tripsAdapter.addAll(objects);
    }

    public void init() {
        rv_tracking_items = findViewById(R.id.rv_tracking_items);
        toolbar = findViewById(R.id.toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

