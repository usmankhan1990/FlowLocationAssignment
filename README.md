# Live location monitoring
- This application is a cloud based application which displays the user’s current location on a map which updates the map in real time as the user moves.
- This application is using back4app web service apis for saving live data on cloud data base with the help of back4apis. With this Admin can see the list of trips, registered users and can consume it with any admin domain like Web portal or any other application.
- With cloud base system you can easily see and monitor existing system.
-	With the help pf “tracking on/off” UI switch, it record or do not record the user’s movements. It also display's a path over the map as the user moves, that represents the user’s movements that are currently being recorded.
-	If a journey is defined as a set of recorded locations between a tracking on and a tracking off switch it retain the user’s journeys with the help of Pause/Resume button. It will stop location updates when Pause pressed and will retain location again with resume button.
-	It allows the user to see all their journeys in a list with start and end times of their journeys when they select them from the list.
-	If the app is resumed from the background during tracking, it is correctly displays a path representing the journey that is currently being recorded.
- You can also enable background service code which is currently being commented inside source code as of right now it is working on current screen monitoring base system, but code snippets are available inside code and you can enable it as well.
- All Api calls are https based with security headers as well

## Configuration and code snippets

### Back4app configuration

This code is using back4app cloud services to maintain webservices and to save data and retreive the data from cloud. Following code is used to initialize back4app(Previously it was parse SDK)
  
  ```Java
Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(appConfigInstance.getApplicationID())
                .clientKey(appConfigInstance.getClientKey())
                .server("https://parseapi.back4app.com/")
                .clientBuilder(new OkHttpClient.Builder())
                .build()
        );
```
For gradle i am using this link: 
```implementation 'com.parse:parse-android:1.16.3'```

### Run Time Permissions
For Runtime Permission for location I am using following code.
```Java
private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(TrackingLocationActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
```
### Login and Signup for Users
For Login and signup I am using Parse cloud functions to make a new User or to retrieve the existing user with login functionality, with the help of ServerCalls calls I am using a callback function to send data back to Login and SignUp activities.

**For Login:**
  ```Java
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
```
**For SignUp:**
  ```Java
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
```

### Google Maps Apis

For location tracking I am using latest Google Maps Apis by initializing ```GoogleAppClient``` with GoogleApi key configuration in Manefest file. Currently using GoogleMap Apis in Location tracking screen and Location Details screen.
One code snippet is given below, which I am using for sending or not to send location which is ```onLocationChange``` call back function.

``` Java
@Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        // mCurrLocationMarker is a location marker
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }....
```        

### Start New Trip with description & Title
- For starting a new trip, I am taking description and trip information from a user to save it ```Trips``` Table in Cloud Database and later I will use the Object of this succesful information to use as a Pointer to save location coordinates in TripHistory Cloud table.

```Java
    
    public void sendTrip(String description, String title, final Context ctx)

    {
        parseObjectTripHistory = new ParseObject("Trips");
        if (parseObjectTripHistory != null) {
            pDialog = uiView.showProgressBar(ctx);
            parseObjectTripHistory.put("description", description);
            parseObjectTripHistory.put("tripName", title);
            parseObjectTripHistory.put("user_id", constantsInstance.getpUser());
            parseObjectTripHistory.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    pDialog.dismiss();

                    if (e == null) {

                        parseObjectTripHistory.getObjectId();
                        if (tripsCallback != null) {
                            tripsCallback.serverResponseForStartTrip(false, true, true, true);
                        }

                     /*   startService(new Intent(TrackingLocationActivity.this, LocationTrackingService.class)); */

                    } else {
                        Toast.makeText(ctx, "Please try again later...", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

    }
```
### Start Track On or Track Off functionalities
For saving location corrdinates i.e Latitude and Longitude to cloud server in ```TripHistory```Table, I am using following code for it with parseObjectTripHistory as a Pointer reference in this table.
```Java
public void savingLocationToServer(ParseGeoPoint pGeoPoint, boolean startLocation, final Context ctx)

    {
        if (parseObjectTripHistory != null && startLocation == true) {

            parseObjectTripLocations = new ParseObject("TripHistory");
            parseObjectTripLocations.put("trip_id", parseObjectTripHistory);
            parseObjectTripLocations.put("latlong", pGeoPoint);
            parseObjectTripLocations.put("user_id", constantsInstance.getpUser());
            parseObjectTripLocations.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
//                        startLocationService = true;

                        if (locationSaveCallBack != null) {
                            locationSaveCallBack.serverResponseForLocationSaving(true);
                        }

                    } else {
                        Toast.makeText(ctx, "Please try again later...", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
```
### LogOut code
Following code I am using to make a user logged out from application. It will clear Current User object from memory as well.
```Java  ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                pDialog.dismiss();
                                if(e==null){
                                    ((Activity)(context)).finish();
                                }
```                                

### For making a tracking line on Google Map
I am using a following code to show a live location tracking line on the map. It first clears the ```GoogleMap``` and then redraw the line.
```Java
private void redrawLine() {

        //Clear the map before making any Polyline

        mGoogleMap.clear();

        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < pointsArraylistForLine.size(); i++) {
            LatLng point = pointsArraylistForLine.get(i);
            options.add(point);
        }

        line = mGoogleMap.addPolyline(options);
    }
```
### Background Service code
In current scope I am not using complete background services for application, as per my stategy I am not using it as it is working for showing live tracking on the screen. But if you want to use complete background services, you can use it by enable it from code and to put same TripSaving and Trip History code for saving data on cloud. Just for a reference it is also available inside application.
