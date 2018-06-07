package realtime.tracker.realtimetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailTxtView;
    private EditText passwordTxtView;
    private Button loginButtonView;
    private ProgressBar progressBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        emailTxtView = (EditText) findViewById(R.id.email_field_login);
        passwordTxtView = (EditText) findViewById(R.id.password_field_login);
        loginButtonView = (Button) findViewById(R.id.login_btn);
        progressBarView = (ProgressBar) findViewById(R.id.progressBar_login);

    }


    public void toSignupActivity(View view) {
        Intent toSignupActivity = new Intent(this,SignupActivity.class);
        startActivity(toSignupActivity);
    }

    public void loginWithNewAccount(View view) {
        String email = emailTxtView.getText().toString().trim();
        String password = passwordTxtView.getText().toString().trim();

        if(email.isEmpty()){
            emailTxtView.setError("Email cannot be empty");
            emailTxtView.requestFocus();
            return;
        }

        if(password.isEmpty() || password.length() < 6){
            passwordTxtView.setError("Password cannot be empty or lower than 6 characters in length");
            passwordTxtView.requestFocus();
            return;
        }

        loginButtonView.setEnabled(false);
        progressBarView.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loginButtonView.setEnabled(true);
                progressBarView.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Successfully Logged In !", Toast.LENGTH_SHORT).show();
                    // TODO Create GoogleMapIntent and start it with the Flag (clear top)
                    Intent profileActivity = new Intent(getApplicationContext(),ProfileActivity.class);
                    profileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profileActivity);
                } else {
                    Toast.makeText(getApplicationContext(),"Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
