package realtime.tracker.realtimetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView welcomeTxtView;
    private EditText trackedEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        welcomeTxtView = (TextView) findViewById(R.id.textView_welcome_field);
        trackedEditTxt = (EditText) findViewById(R.id.track_email_field);
        welcomeTxtView.setText(welcomeTxtView.getText()+ " " + mAuth.getCurrentUser().getEmail());
    }

    public void startSharing(View view) {
        Toast.makeText(this, "Started Sharing Locaiton, {Retrieving Data From GPS} please wait !", Toast.LENGTH_SHORT).show();
        Intent sharingIntent = new Intent(getApplicationContext(),ShareRealTimeActivity.class);
        startActivity(sharingIntent);
    }

    public void startTracking(View view) {
        Intent trackerIntent = new Intent(getApplicationContext(),TrackerRealTime.class);
        String trackedEmail = trackedEditTxt.getText().toString().trim();

        if(trackedEmail.isEmpty()){
            trackedEditTxt.setError("You Must Type An Email Address to Track it's user");
            trackedEditTxt.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(trackedEmail).matches()){
            trackedEditTxt.setError("You must type a valid Email address !");
            trackedEditTxt.requestFocus();
            return;
        }

        trackerIntent.putExtra("trackedEmail",trackedEmail);
        startActivity(trackerIntent);
    }

    public void logoutFromApp(View view) {
        Toast.makeText(this, "You have been sucessfully logged out !", Toast.LENGTH_SHORT).show();
        Intent logoutToLogin = new Intent(this,MainActivity.class);
        logoutToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutToLogin);
    }
}
