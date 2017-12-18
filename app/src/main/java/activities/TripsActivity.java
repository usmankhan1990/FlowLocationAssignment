package activities;

import android.app.ProgressDialog;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import Interfaces.TripsListCallback;
import adapter.TrackingItemsAdapter;
import helper.Constants;
import Controller.ServiceCalls;
import viewsHelper.UIView;

/**
 * Created by UsmanKhan on 12/14/17.
 */

public class TripsActivity extends AppCompatActivity implements TripsListCallback{

    ParseQuery pQueryTripsDetail = new ParseQuery("TripHistory");
    RecyclerView rv_tracking_items;
    TrackingItemsAdapter tripsAdapter;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    LinearLayoutManager layoutManager;

    private Constants constants = Constants.getInstance();
    private ServiceCalls serviceCalls = ServiceCalls.getInstance();
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
                    serviceCalls.getTrackingItemDetail(parseObjectTrackingDetail, TripsActivity.this);


                } else {

                    Toast.makeText(TripsActivity.this, "Please try again later", Toast.LENGTH_LONG).show();
                }


            }
        });

        serviceCalls.setLocationSaveCallBack(TripsActivity.this);
        serviceCalls.getTrackingItems(constants.getpUser(), TripsActivity.this);

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


    @Override
    public void serverResponseWithTripsList(List<ParseObject> objects) {

        if(objects!=null && objects.size()>0){
            tripsAdapter.addAll(objects);
        }


    }
}

