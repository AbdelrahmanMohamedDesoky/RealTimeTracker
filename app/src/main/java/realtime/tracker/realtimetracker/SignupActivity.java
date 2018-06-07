package realtime.tracker.realtimetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText emailTxtView;
    private EditText passwordTxtView;
    private ProgressBar progressBarView;
    private Button signupButtonView;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        emailTxtView = (EditText) findViewById(R.id.email_field_signup);
        passwordTxtView = (EditText) findViewById(R.id.password_field_signup);
        progressBarView = (ProgressBar) findViewById(R.id.progressBar_signup);
        signupButtonView = (Button) findViewById(R.id.signup_btn);
    }

    public void toLoginActivity(View view) {
        Intent toLoginActivity = new Intent(this,MainActivity.class);
        startActivity(toLoginActivity);
    }

    public void registerNewAccount(View view){
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

        progressBarView.setVisibility(View.VISIBLE);
        signupButtonView.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarView.setVisibility(View.GONE);
                        signupButtonView.setEnabled(true);
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successsfully Signed up",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Error : " + task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
