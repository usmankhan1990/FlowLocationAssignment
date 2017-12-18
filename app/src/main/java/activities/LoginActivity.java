package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;
import com.parse.ParseUser;

import Controller.ServiceCalls;

/**
 * Created by UsmanKhan on 12/12/17.
 */

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    TextView txtSignUp;
    Button btnSignIn;
    Toolbar toolbar;
    private ServiceCalls serviceCalls = ServiceCalls.getInstance();
    private String email = "", password = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        init();

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, TrackingLocationActivity.class);
            finish();
            startActivity(intent);
        }

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


    void init() {

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        toolbar = findViewById(R.id.toolbar);
        txtSignUp = findViewById(R.id.txtSignUp);

        btnSignIn.setOnClickListener(btnSignInListener);
        txtSignUp.setOnClickListener(txtSignUpListener);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * OnClickListener for Login button
     * onClick success will send Login Call to server
     */

    final View.OnClickListener btnSignInListener = new View.OnClickListener() {
        public void onClick(final View v) {

            email = edtEmail.getText().toString();
            password = edtPassword.getText().toString();

            // Doing Validation for Sign Up

            if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {

                Toast.makeText(getApplicationContext(), "Kindly fill full information!", Toast.LENGTH_LONG).show();

            } else {
                serviceCalls.loginCall(LoginActivity.this, email, password);
            }

        }
    };

    /**
     * OnClickListener for Sign Up
     * Starts SignUp Activity.
     */

    final View.OnClickListener txtSignUpListener = new View.OnClickListener() {
        public void onClick(final View v) {

            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

}
