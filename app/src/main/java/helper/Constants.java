package helper;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by UsmanKhan on 12/13/17.
 * This class provides variables like Current User object, trackingObjectsList, Objects for getting Tracking Detail.
 * It contains getter, setter values to use in whole project structure.
 */


public class Constants {


    private static Constants instance;
    public SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss a");
    public DecimalFormat decimalFormat = new DecimalFormat(".##");
    ParseUser pUser = ParseUser.getCurrentUser();
    ParseObject pObjectTrackingDetail;
    List<ParseObject> trackingObjectsList = new ArrayList();
    private ArrayList<LatLng> locationPoints; //added

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String description = "", title = "";

    public ArrayList<LatLng> getPointsArraylistForLine() {
        return pointsArraylistForLine;
    }

    public void setPointsArraylistForLine(ArrayList<LatLng> pointsArraylistForLine) {
        this.pointsArraylistForLine = pointsArraylistForLine;
    }

    private ArrayList<LatLng> pointsArraylistForLine = new ArrayList<LatLng>();

    public boolean isIsbuttonOn() {
        return isbuttonOn;
    }

    public void setIsbuttonOn(boolean isbuttonOn) {
        this.isbuttonOn = isbuttonOn;
    }

    boolean isbuttonOn = true;

    public boolean isIsbuttonOff() {
        return isbuttonOff;
    }

    public void setIsbuttonOff(boolean isbuttonOff) {
        this.isbuttonOff = isbuttonOff;
    }

    public boolean isIsbuttonPause() {
        return isbuttonPause;
    }

    public void setIsbuttonPause(boolean isbuttonPause) {
        this.isbuttonPause = isbuttonPause;
    }

    boolean isbuttonOff = true;
    boolean isbuttonPause = true;


    public boolean isStartLocationService() {
        return startLocationService;
    }

    public void setStartLocationService(boolean startLocationService) {
        this.startLocationService = startLocationService;
    }

    public boolean startLocationService = false;


    public ParseObject getParseObjectTripLocations() {
        return parseObjectTripLocations;
    }

    public void setParseObjectTripLocations(ParseObject parseObjectTripHistory) {
        this.parseObjectTripLocations = parseObjectTripHistory;
    }

    ParseObject parseObjectTripLocations = new ParseObject("TripHistory");
    ParseObject parseObjectTripHistory = new ParseObject("Trips");

    public ParseObject getParseObjectTripHistory() {
        return parseObjectTripHistory;
    }

    public void setParseObjectTripHistory(ParseObject parseObjectTripHistory) {
        this.parseObjectTripHistory = parseObjectTripHistory;
    }



    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    public ArrayList<LatLng> getLocationPoints() {
        return locationPoints;
    }

    public void setLocationPoints(ArrayList<LatLng> locationPoints) {
        this.locationPoints = locationPoints;
    }

    public ParseObject getpObjectTrackingDetail() {
        return pObjectTrackingDetail;
    }

    public void setpObjectTrackingDetail(ParseObject pObjectTrackingDetail) {
        this.pObjectTrackingDetail = pObjectTrackingDetail;
    }

    public List<ParseObject> getTrackingObjectsList() {
        return trackingObjectsList;
    }

    public void setTrackingObjectsList(List<ParseObject> trackingObjectsList) {
        this.trackingObjectsList = trackingObjectsList;
    }

    public ParseUser getpUser() {
        return pUser;
    }

    public void setpUser(ParseUser pUser) {
        this.pUser = pUser;
    }


    /**
     * <p>This function converts date in UTC format.</p>
     *
     * @param date - date from server to convert
     * @return the date converted in UTC format
     */

    public Date getDateTimeUTC(Date date) {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        try {
            return dateFormatLocal.parse(dateFormatGmt.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * <p>This function converts decimal degrees to radians.</p>
     *
     * @param lat1 - latitude point 1
     * @param lon1 - longitude point 1
     * @param lat2 - latitude point 2
     * @param lon2 - longitude point 2
     * @param unit - unit of measure (M, K, N)
     * @return the distance between the two points
     */

    public final double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /**
     * <p>This function converts decimal degrees to radians.</p>
     *
     * @param deg - the decimal to convert to radians
     * @return the decimal converted to radians
     */
    private final double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * <p>This function converts radians to decimal degrees.</p>
     *
     * @param rad - the radian to convert
     * @return the radian converted to decimal degrees
     */
    private final double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
