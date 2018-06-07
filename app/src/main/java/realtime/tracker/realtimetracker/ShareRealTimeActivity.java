package realtime.tracker.realtimetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class ShareRealTimeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLocation;
    private LocationManager locationManger;
    private LocationListener locationListener;
    public MyLocation myLocation = new MyLocation();
    private Marker marker;
    private FirebaseAuth mAuth;
    private String currentUser;
    private String md5User;
    private final int LOCATION_REQUEST_CODE = 2;
    private FirebaseDatabase database;
    private DatabaseReference databaseUsers;
    MyLocationDatabase myLocationDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_real_time);
        //init location manger and listener
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getEmail();
        md5User = getMd5Hash(currentUser.getBytes());
        database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference("users");
        myLocationDatabase = new MyLocationDatabase(currentUser,myLocation.getLatitude(),myLocation.getLongitude());

        locationManger = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation.setLatitude(location.getLatitude());
                myLocation.setLongitude(location.getLongitude());

                // update database object and then write it on the database !
                myLocationDatabase.setLat(myLocation.getLatitude());
                myLocationDatabase.setLng(myLocation.getLongitude());
                databaseUsers.child(md5User).setValue(myLocationDatabase);

                // Update Marker on Map
                updateMarker();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingsIntent);
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (locationManger != null){
            locationManger.removeUpdates(locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation.setmMap(mMap);

        // TODO ask for permissions
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Ask for permissions
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},LOCATION_REQUEST_CODE);
        } else {
            locationManger.requestLocationUpdates("gps", 5000, 0, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case LOCATION_REQUEST_CODE:
                if(!(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "Please accept the GPS Permissions in order to use GPS", Toast.LENGTH_SHORT).show();

                }

        }
    }

    public void updateMarker(){
        GoogleMap myMap = myLocation.getmMap();
        currentLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        if(marker != null){
            marker.setPosition(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        } else {
            marker = myMap.addMarker(new MarkerOptions().position(currentLocation).title("You Are Here!").snippet("You are currently sharing this Location !"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        }

    }

    public String getMd5Hash(byte[] bytes) {
        Formatter fm = new Formatter();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            bytes = digest.digest();
            for (byte b : bytes) {
                fm.format("%02x", b);
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception: " + e);
        }
        return fm.out().toString();
    }
}

