package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;

import ServiceController.ServiceCalls;

/**
 * Created by UsmanKhan on 12/12/17.
 * SignUp methods here along with variable validations
 */

public class SignUpActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignIn;
    Toolbar toolbar;
    private ServiceCalls serviceCalls = ServiceCalls.getInstance();
    private String email = "", password = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

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

        btnSignIn.setOnClickListener(btnSignUpListener);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * OnClickListener for SignUp button
     * Sends sign Up call to server.
     */

    final View.OnClickListener btnSignUpListener = new View.OnClickListener() {
        public void onClick(final View v) {


            email = edtEmail.getText().toString();
            password = edtPassword.getText().toString();

            // Doing Validation for Sign Up

            if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {

                Toast.makeText(getApplicationContext(), "Kindly fill full information!", Toast.LENGTH_LONG).show();

            } else {

                serviceCalls.signUpCall(SignUpActivity.this, email, password);
            }

        }
    };

}
