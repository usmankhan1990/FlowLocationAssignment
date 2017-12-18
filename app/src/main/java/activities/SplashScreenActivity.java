package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.flow.flowlocationassignment.R;
import com.parse.ParseUser;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		launchLandingScreen();

}

ParseUser puser = ParseUser.getCurrentUser();

	private void launchLandingScreen() {
		new Thread() {
			public void run() {

				try {
					sleep(1 * 3500); // splash should not be longer than 3.5 seconds

					if(puser != null) {
						Intent intent = new Intent(SplashScreenActivity.this, TrackingLocationActivity.class);
						startActivity(intent);
						finish();

					}else{
						Intent intent = new Intent(SplashScreenActivity.this,  LoginActivity.class);
						startActivity(intent);
						finish();
					}


				}
				catch (Exception e) {
					e.getMessage();
				}
			}
		}.start();
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onNewIntent(Intent intent) {
		this.setIntent(intent);
	}
	@Override
	public void onStop() {
		super.onStop();
	}

}