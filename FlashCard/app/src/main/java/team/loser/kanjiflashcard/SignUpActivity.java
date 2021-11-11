package team.loser.kanjiflashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText edEmail, edPassword, edConfirmPassword;
    private Button btnSignUp;
    private LinearLayout layoutSignIn;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        setControls();
        setEvents();
    }

    private void setEvents() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String confirmPass = edConfirmPassword.getText().toString().trim();
                if(email.isEmpty()){
                    edEmail.setError("email is required");
                    return;
                }
                if(password.isEmpty()){
                    edPassword.setError("password is required");
                    return;
                }
                if(confirmPass.isEmpty()){
                    edConfirmPassword.setError("please confirm password");
                    return;
                }
                if(!password.equals(confirmPass)){
                    Toast.makeText(SignUpActivity.this, "password not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth = FirebaseAuth.getInstance();
                loader.show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loader.dismiss();
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        layoutSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setControls() {
        loader = new ProgressDialog(SignUpActivity.this);
        loader.setMessage("Please wait...");
        edEmail = findViewById(R.id.ed_email_sign_up);
        edPassword = findViewById(R.id.ed_password_sign_up);
        edConfirmPassword = findViewById(R.id.ed_confirm_password_sign_up);
        btnSignUp = findViewById(R.id.btn_sign_up);
        layoutSignIn = findViewById(R.id.layout_sign_in);
    }
}