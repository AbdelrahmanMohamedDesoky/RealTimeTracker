package realtime.tracker.realtimetracker;

/**
 * Created by Admin on 2017-11-07.
 */

public class MyLocationDatabase {

    private String email;
    private double Lat;
    private double Lng;

    public MyLocationDatabase(){

    }

    public MyLocationDatabase(String email, double lat, double lng) {
        this.email = email;
        Lat = lat;
        Lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }
}
