package helper;

import com.parse.ParseUser;

/**
 * Created by UsmanKhan on 12/13/17.
 */

public class Constants {


    private static Constants instance;

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    ParseUser pUser;

    public ParseUser getpUser() {
        return pUser;
    }

    public void setpUser(ParseUser pUser) {
        this.pUser = pUser;
    }



}
