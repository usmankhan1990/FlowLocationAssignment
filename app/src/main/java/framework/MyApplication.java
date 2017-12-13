package framework;

import android.app.Application;

import com.parse.Parse;

import helper.AppConfig;
import okhttp3.OkHttpClient;

/**
 * Created by Usman Khan on 13/12/2017.
 * Application class for initializing Parse for every API call.
 * Variables include ApplicationID and Client Key.
 * Connecting through okhttp3 protocols.
 */


public class MyApplication extends Application {

    private static MyApplication mInstance;
    private AppConfig appConfigInstance = AppConfig.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(appConfigInstance.getApplicationID())
                .clientKey(appConfigInstance.getClientKey())
                .server("https://parseapi.back4app.com/")
                .clientBuilder(new OkHttpClient.Builder())
                .build()
        );

    }

}
