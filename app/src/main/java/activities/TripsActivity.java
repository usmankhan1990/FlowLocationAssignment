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
import java.util.ArrayList;
import java.util.List;
import adapter.TrackingItemsAdapter;
import helper.Constants;
import viewsHelper.UIView;

/**
 * Created by UsmanKhan on 12/14/17.
 */

public class TripsActivity extends AppCompatActivity {


    ParseQuery pQueryTracking = new ParseQuery("Trips");
    ParseQuery pQueryTrackingDetail = new ParseQuery("TripHistory");
    RecyclerView rv_tracking_items;
    TrackingItemsAdapter tripsAdapter;
    ProgressDialog pDialog;
    Toolbar toolbar;
    LinearLayoutManager layoutManager;
    private Constants constants = Constants.getInstance();
    private UIView uiView = UIView.getInstance();
    private ArrayList<LatLng> points = new ArrayList<LatLng>(); //added

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


                ParseObject parseObject = constants.getTrackingObjectsList().get(position);

                if (parseObject.has("tripName")) {

                    // Setting ParseObject for extracting location details
                    constants.setpObjectTrackingDetail(parseObject);
                    getTrackingItemDetail();

                } else {

                    Toast.makeText(TripsActivity.this, "Please try again later", Toast.LENGTH_LONG).show();
                }


            }
        });


        getTrackingItems();
    }

    public void getTrackingItems()

    {

        pQueryTracking.whereEqualTo("user_id", constants.getpUser());

        pDialog = uiView.showProgressBar(this);
        pQueryTracking.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                pDialog.dismiss();

                if (e == null) {

                    constants.setTrackingObjectsList(objects);
                    loadData(objects);

                } else {


                }

            }

        });


    }

    public void getTrackingItemDetail()

    {

        pQueryTrackingDetail.whereEqualTo("trip_id", constants.getpObjectTrackingDetail());

        pDialog = uiView.showProgressBar(this);
        pQueryTrackingDetail.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (e == null && objects.size() > 0) {
                    for (ParseObject parseObjectLocations : objects) {
                        LatLng latLngPoints = new LatLng(parseObjectLocations.getParseGeoPoint("latlong").getLatitude(), parseObjectLocations.getParseGeoPoint("latlong").getLongitude());
                        points.add(latLngPoints);
                    }


                    constants.setLocationPoints(points);

                    pDialog.dismiss();

                    Intent intent = new Intent(TripsActivity.this, TrackingDetailScreen.class);
                    startActivity(intent);

                }else{
                    pDialog.dismiss();
                    Toast.makeText(TripsActivity.this, "Please try again later", Toast.LENGTH_LONG).show();
                }


            }

        });


    }

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

