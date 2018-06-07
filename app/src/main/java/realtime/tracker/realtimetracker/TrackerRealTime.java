package realtime.tracker.realtimetracker;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class TrackerRealTime extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MyLocation myLocation;
    private String trackedEmail;
    private LatLng currentLocation;
    private String md5User;
    private FirebaseDatabase database;
    private DatabaseReference databaseUsers;
    private Marker marker;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String foundUser = null;
            if(dataSnapshot.hasChildren()){
                for (DataSnapshot currentUser : dataSnapshot.getChildren()){
                    MyLocationDatabase myLocationDatabase = currentUser.getValue(MyLocationDatabase.class);
                    if(myLocationDatabase.getEmail().equals(trackedEmail)){
                        foundUser = "true";
                        myLocation.setLatitude(myLocationDatabase.getLat());
                        myLocation.setLongitude(myLocationDatabase.getLng());
                        updateMarker();
                        break;
                    }
                }
                if(foundUser == null) {
                    Toast.makeText(TrackerRealTime.this,"Error Email Address not found or User didn't share any location",Toast.LENGTH_LONG).show();
                    TrackerRealTime.this.onBackPressed();

                }
            } else {
                Toast.makeText(TrackerRealTime.this,"Error while retrieving data from server",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_real_time);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent getTrackedEmail = getIntent();
        trackedEmail = getTrackedEmail.getStringExtra("trackedEmail");
        myLocation = new MyLocation();
        md5User = getMd5Hash(trackedEmail.getBytes());
        database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference("users/");
        databaseUsers.addValueEventListener(valueEventListener);

    }

    @Override
    public void onBackPressed() {
        databaseUsers.removeEventListener(valueEventListener);
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation.setmMap(mMap);
    }

    public void updateMarker(){
        GoogleMap myMap = myLocation.getmMap();
        currentLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        if(marker != null){
            marker.setPosition(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        } else {
            marker = myMap.addMarker(new MarkerOptions().position(currentLocation).title("His Last Shared Location !").snippet("Email : " + trackedEmail));
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
