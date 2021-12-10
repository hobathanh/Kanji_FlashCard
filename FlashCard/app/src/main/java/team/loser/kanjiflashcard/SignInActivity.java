package team.loser.kanjiflashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements TextWatcher,
        CompoundButton.OnCheckedChangeListener {
    private EditText edEmail, edPassword;
    private Button btnSignIn, btnSignInWithGoogle ;
    private LinearLayout layoutSignUp;
    private ProgressDialog loader;
    private CheckBox cbRemember;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
        setControls();
        setEvents();
    }

    private void setEvents() {
        layoutSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    edEmail.setError("email is required");
                    return;
                }
                if (password.isEmpty()) {
                    edPassword.setError("password is required");
                    return;
                }

                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();
                loader.show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loader.dismiss();
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            }
                        })
                        .addOnFailureListener(SignInActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(SignInActivity.this,"Email or password is incorrect", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(SignInActivity.this,"The email address is badly formatted.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignInActivity.this,e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,GoogleSignInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setControls() {
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        layoutSignUp = findViewById(R.id.layout_sign_up);
        loader = new ProgressDialog(SignInActivity.this);
        loader.setMessage("Please wait...");
        edEmail = findViewById(R.id.ed_email_sign_in);
        edPassword = findViewById(R.id.ed_password_sign_in);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignInWithGoogle = findViewById((R.id.btn_sign_in_with_google));
        cbRemember = findViewById(R.id.cb_remember);

        if(sharedPreferences.getBoolean(KEY_REMEMBER,false)){
            cbRemember.setChecked(true);
        }else  cbRemember.setChecked(false);

        edEmail.setText(sharedPreferences.getString(KEY_EMAIL,""));
        edPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
        edEmail.addTextChangedListener(this);
        edPassword.addTextChangedListener(this);
        cbRemember.setOnCheckedChangeListener(this);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }
    private void managePrefs(){
        if(cbRemember.isChecked()){
            editor.putString(KEY_EMAIL, edEmail.getText().toString().trim());
            editor.putString(KEY_PASSWORD, edPassword.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER,true);
        }else {
            editor.putBoolean(KEY_REMEMBER,false);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
        } editor.apply();
    }
}