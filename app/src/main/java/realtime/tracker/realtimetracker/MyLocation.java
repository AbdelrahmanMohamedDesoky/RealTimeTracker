package realtime.tracker.realtimetracker;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Admin on 2017-11-07.
 */

class MyLocation {
    private double Longitude;
    private double Latitude;
    private GoogleMap mMap;
    private static String trackedEmail;

    public static String getTrackedEmail() {
        return trackedEmail;
    }

    public static void setTrackedEmail(String trackedEmail) {
        MyLocation.trackedEmail = trackedEmail;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
