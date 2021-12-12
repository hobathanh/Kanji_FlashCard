package team.loser.kanjiflashcard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import team.loser.kanjiflashcard.fragments.CardsFragment;
import team.loser.kanjiflashcard.fragments.ChangePasswordFragment;
import team.loser.kanjiflashcard.fragments.HomeFragment;
import team.loser.kanjiflashcard.fragments.ProfileFragment;
import team.loser.kanjiflashcard.utils.IOnBackPressed;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static final int GALLERY_REQUEST_CODE = 69;
    private static final int FRAGMENT_HOME = 0;
    private static final int DIALOG_FEEDBACK = 1;
    private static final int FRAGMENT_ABOUT = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_PWD = 4;
    private static final int FRAGMENT_CARDS = 5;
    private static final int FRAGMENT_SHARE = 6;

    private FragmentTransaction fragmentTransaction;
    final private ProfileFragment mProfileFragment = new ProfileFragment();
    private NavigationView mNavigationView;
    private ImageView imgAvt;
    private TextView tvUserEmail, tvUserName;
    private int mCurrentFragment = FRAGMENT_HOME;

    public static DatabaseReference reference;
    public DatabaseReference referenceFeedback;
    public static String onlineUserID;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    if(intent == null) return;

                    Uri uri = intent.getData();
                    mProfileFragment.setUriData(uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mProfileFragment.setBitmapImageView(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setControls();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,
                toolbar,
                R.string.nav_drawer_open,
                R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);



        showUserInfoInMenuLeft();
    }

    private void setControls() {
        mNavigationView = findViewById(R.id.nav_view);
        imgAvt = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.imv_user_avt);
        tvUserEmail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_user_email);
        tvUserName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_user_name);

        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("cards").child(onlineUserID);
        referenceFeedback = FirebaseDatabase.getInstance().getReference().child("feedbacks").child(onlineUserID);
        //transaction fragments
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, new HomeFragment());
        fragmentTransaction.commit();
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        mCurrentFragment = FRAGMENT_HOME;
    }
    // go to Cards Fragment
    public void goToCardsFragment(DatabaseReference categoryRef){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        CardsFragment cardsFragment = new CardsFragment(categoryRef);
        fragmentTransaction.replace(R.id.content_frame, cardsFragment);
        fragmentTransaction.addToBackStack(cardsFragment.CARDS_FRAGMENT_NAME);
        fragmentTransaction.commit();

    }
    public void showUserInfoInMenuLeft(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        if(name == null){
            tvUserName.setVisibility(View.GONE);
        }
        else {
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(name);
        }
        tvUserEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.avt_default).into(imgAvt);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            if(mCurrentFragment != FRAGMENT_HOME){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.content_frame, new HomeFragment());
                fragmentTransaction.commit();
                mCurrentFragment = FRAGMENT_HOME;
            }
        }
        else if(id == R.id.nav_feedback){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_feedback);
            Window window = dialog.getWindow();
            if(window == null){
                return false;
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.gravity = Gravity.CENTER;
            window.setAttributes(windowAttributes);
            dialog.setCancelable(true);

            EditText editText = dialog.findViewById(R.id.ed_user_feedback);
            Button btnSend = dialog.findViewById(R.id.btn_send_feedback);
            btnSend.setOnClickListener(view -> {
                String timeStamp = new SimpleDateFormat("dd-MM-yyy-HH:mm:ss").format(new Date());
                String feedback = editText.getText().toString().trim();
                referenceFeedback.child(timeStamp).setValue(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialog.show();
        }
        else if(id == R.id.nav_about){
            if(mCurrentFragment != FRAGMENT_ABOUT){
                mCurrentFragment = FRAGMENT_ABOUT;
            }
        }
        else if(id == R.id.nav_sharing){
            if(mCurrentFragment!= FRAGMENT_SHARE){
                mCurrentFragment = FRAGMENT_SHARE;
            }
        }
        else if(id == R.id.nav_profile){
            if(mCurrentFragment != FRAGMENT_PROFILE){
                ProfileFragment profileFragment = new ProfileFragment();
                replaceFragment(profileFragment, profileFragment.PROFILE_FRAGMENT_NAME);
                mCurrentFragment = FRAGMENT_PROFILE;
            }

        }
        else if(id == R.id.nav_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

        }
        else if(id == R.id.nav_change_pwd){
            if(mCurrentFragment != FRAGMENT_PWD){
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                replaceFragment(changePasswordFragment, changePasswordFragment.CHANGE_PWD_FRAGMENT_NAME);
                mCurrentFragment = FRAGMENT_PWD;
            }

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void replaceFragment(Fragment fragment, String fragmentName){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }

    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "select picture"));
    }

    @Override
    public void onBackPressed() {
        mCurrentFragment = FRAGMENT_CARDS;
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}