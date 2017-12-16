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

import viewsHelper.UIView;
import activities.TrackingLocationActivity;

/**
 * Created by UsmanKhan on 12/13/17.
 */

public class ServiceCalls {


    private static ServiceCalls serviceCallsInstance;
    ProgressDialog pDialog;

    private UIView uiView = UIView.getInstance();



    public static ServiceCalls getInstance() {
        if (serviceCallsInstance == null) {
            serviceCallsInstance = new ServiceCalls();
        }
        return serviceCallsInstance;
    }


    public void loginCall(final Context ctx, String email, String password)

    {
        pDialog =  uiView.showProgressBar(ctx);

        ParseUser.logInInBackground(email, password, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException e) {

                pDialog.dismiss();
                if (user != null) {
                    Toast.makeText(ctx, R.string.login_success, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, TrackingLocationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }else{

                    Toast.makeText(ctx,"Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signUpCall(final Context ctx, String email, String password)

    {

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        pDialog =  uiView.showProgressBar(ctx);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                pDialog.dismiss();
                if (e == null) {
                    Toast.makeText(ctx, "Sign Up Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, TrackingLocationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);

                } else {
                    Toast.makeText(ctx, "Sign Up UnSuccessful, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}