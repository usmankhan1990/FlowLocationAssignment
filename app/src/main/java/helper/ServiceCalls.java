package helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import Interfaces.DialogTitleDescriptionCallBack;
import Interfaces.LocationSaveCallBack;
import Interfaces.TripsCallback;
import viewsHelper.UIView;
import activities.TrackingLocationActivity;

/**
 * Created by UsmanKhan on 12/13/17.
 * This service controller class provides methods to connect to server and to get return success/failure responses.
 * It has login, signUp, TripStart and Trip history saving methods.
 */

public class ServiceCalls {


    private static ServiceCalls serviceCallsInstance;
    ProgressDialog pDialog;

    private UIView uiView = UIView.getInstance();
    private Constants constantsInstance = Constants.getInstance();
    public ParseObject parseObjectTripHistory, parseObjectTripLocations;

    public static ServiceCalls getInstance() {
        if (serviceCallsInstance == null) {
            serviceCallsInstance = new ServiceCalls();
        }
        return serviceCallsInstance;
    }

    TripsCallback tripsCallback = null;
    LocationSaveCallBack locationSaveCallBack = null;

    /**
     * <p>
     * </p>
     *@param tripsCallback   - Making an instance of TripsCallback.
     */
    public void setTripsCallback(TripsCallback tripsCallback) {
        this.tripsCallback = tripsCallback;
    }

    /**
     * <p>
     * </p>
     *@param locationSaveCallBack   - Making an instance of TripsCallback.
     */
    public void setLocationSaveCallBack(LocationSaveCallBack locationSaveCallBack) {
        this.locationSaveCallBack = locationSaveCallBack;
    }


    /**
     * <p>This method sends a request to server to make a user login.</p>
     * In successful case it will send user object.
     * In Unsuccessful case it will send an error with Null value for user.
     *
     * @param ctx - Context from Login screen
     * @param email - Email for sending to server
     * @param password - Password for sending to server
     * @return user - It returns a user object
     */

    public void loginCall(final Context ctx, String email, String password)

    {
        pDialog = uiView.showProgressBar(ctx);

        ParseUser.logInInBackground(email, password, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException e) {

                pDialog.dismiss();
                if (user != null) {
                    Toast.makeText(ctx, R.string.login_success, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, TrackingLocationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                } else {

                    Toast.makeText(ctx, "Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * <p>This method sends a request to server to make a user signup.</p>
     * In successful case it will send user object.
     * In Unsuccessful case it will send an error with Null value for user.
     *
     * @param ctx - Context from signup screen
     * @param email - Email for sending to server
     * @param password - Password for sending to server
     * @return user - It returns a user object
     */

    public void signUpCall(final Context ctx, String email, String password)

    {

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        pDialog = uiView.showProgressBar(ctx);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                pDialog.dismiss();
                if (e == null) {
                    Toast.makeText(ctx, "Sign Up Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, TrackingLocationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);

                } else {
                    Toast.makeText(ctx, "Sign Up UnSuccessful, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    /**
     * <p>This method sends a decription and title of a trip.</p>
     * Parse object of this function query will be used to save locations in server as well.
     * @param ctx - Context
     * @param description - Description of a trip for sending to server
     * @param title - Title of a trip for sending to server
     */

    public void sendTrip(String description, String title,final Context ctx)

    {
        parseObjectTripHistory = new ParseObject("Trips");
        if(parseObjectTripHistory!=null){
            pDialog = uiView.showProgressBar(ctx);
            parseObjectTripHistory.put("description",description);
            parseObjectTripHistory.put("tripName",title);
            parseObjectTripHistory.put("user_id",constantsInstance.getpUser());
            parseObjectTripHistory.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    pDialog.dismiss();

                    if(e == null){

                        parseObjectTripHistory.getObjectId();
                        if(tripsCallback !=null){
                            tripsCallback.serverResponseForStartTrip(false,true,true, true);
                        }

                     /*   startService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */

                    }else{
                        Toast.makeText(ctx,"Please try again later...",Toast.LENGTH_LONG).show();
                    }
                }
            });



        }

    }


    /**
     * <p>This method sends a users location to the server for a track history.</p>
     * This query is also using a ParseObject of parseObjectTripHistory for saving a data with this key. Its a pointer in another table of Parse webservice data base.
     * @param ctx - Context
     * @param pGeoPoint - Users coordinates information - Latitude & Longitude
     * @param startLocation - Boolean value to start(true) or not to start(false)
     */


    public void savingLocationToServer(ParseGeoPoint pGeoPoint, boolean startLocation, final Context ctx)

    {
        if(parseObjectTripHistory!=null && startLocation == true){

            parseObjectTripLocations = new ParseObject("TripHistory");
            parseObjectTripLocations.put("trip_id",parseObjectTripHistory);
            parseObjectTripLocations.put("latlong",pGeoPoint);
            parseObjectTripLocations.put("user_id",constantsInstance.getpUser());
            parseObjectTripLocations.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e == null){
//                        startLocationService = true;

                        if(locationSaveCallBack !=null){
                            locationSaveCallBack.serverResponseForLocationSaving(true);
                        }

                    }else{
                        Toast.makeText(ctx,"Please try again later...",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}