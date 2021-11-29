package team.loser.kanjiflashcard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import team.loser.kanjiflashcard.fragments.AboutFragment;
import team.loser.kanjiflashcard.fragments.CardsFragment;
import team.loser.kanjiflashcard.fragments.HomeFragment;
import team.loser.kanjiflashcard.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static final int GALLERY_REQUEST_CODE = 69;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_QUIZ = 1;
    private static final int FRAGMENT_ABOUT = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_PWD = 4;
    private static final int FRAGMENT_CARDS = 5;

    final private ProfileFragment mProfileFragment = new ProfileFragment();
    private NavigationView mNavigationView;
    private ImageView imgAvt;
    private TextView tvUserEmail, tvUserName;
    private int mCurrentFragment = FRAGMENT_HOME;

    public static DatabaseReference reference;
    public static String onlineUserID;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        imgAvt = mNavigationView.getHeaderView(0).findViewById(R.id.imv_user_avt);
        tvUserEmail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_user_email);
        tvUserName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_user_name);

        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("cards").child(onlineUserID);

        //transaction fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
        Glide.with(this).load(photoUrl).error(R.drawable.avatar_default_icon).into(imgAvt);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            if(mCurrentFragment != FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        }
        else if(id == R.id.nav_quiz){

        }
        else if(id == R.id.nav_about){
            replaceFragment(new AboutFragment());
            mCurrentFragment = FRAGMENT_ABOUT;
        }
        else if(id == R.id.nav_profile){
            if(mCurrentFragment != FRAGMENT_PROFILE){
                replaceFragment(mProfileFragment);
                mCurrentFragment = FRAGMENT_PROFILE;
            }

        }
        else if(id == R.id.nav_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

        }
        else if(id == R.id.nav_profile){

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
    public void showCardsFragment(DatabaseReference reference) {
        if (mCurrentFragment != FRAGMENT_CARDS) {
            replaceFragment(new CardsFragment(reference));
            mCurrentFragment = FRAGMENT_CARDS;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
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
}