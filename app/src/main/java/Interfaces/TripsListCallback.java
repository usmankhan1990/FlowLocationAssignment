package Interfaces;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by UsmanKhan on 12/18/17.
 */

public interface TripsListCallback {

    void serverResponseWithTripsList(List<ParseObject> objects);

    }
