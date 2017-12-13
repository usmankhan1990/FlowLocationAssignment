package helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import viewsHelper.UIView;
import activities.MainActivity;

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


    public void loginCall(Context ctx, String email, String password)

    {

        Toast.makeText(ctx, R.string.login_success, Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);

                } else {
                    Toast.makeText(ctx, "Sign Up UnSuccessful, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });




    }

}