package helper;

/**
 * Created by UsmanKhan on 12/12/17.
 * This class is providing application and client key for parse to initialize server configurations.
 */

public class AppConfig {


    private static AppConfig appConfigInstance;

    public static AppConfig getInstance() {
        if (appConfigInstance == null) {
            appConfigInstance = new AppConfig();
        }
        return appConfigInstance;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getClientKey() {
        return clientKey;
    }

    private  String applicationID = "ESJQncoov78hVEBozxwulUB1AtGjzeABSlKPzXXO";
    private  String clientKey = "vXnOgfC0loMMUPwakrWlQcQAx4DPE6dx6ZWNumtw";

}
