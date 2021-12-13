package team.loser.kanjiflashcard.fragments;

import static team.loser.kanjiflashcard.MainActivity.GALLERY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import team.loser.kanjiflashcard.MainActivity;
import team.loser.kanjiflashcard.R;

public class ProfileFragment extends Fragment {
    public static final String PROFILE_FRAGMENT_NAME = ProfileFragment.class.getName();
    private  View mView;
    public ImageView imgAvtProfile;
    private EditText edFullName, edEmail;
    private Button btnUpdate;
    private Uri mUri;
    private MainActivity mMainActivity;
    private ProgressDialog loader;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mMainActivity = (MainActivity)getActivity();
        setControls();
        setUI();
        setEvents();
        return mView;

    }

    private void setEvents() {
        imgAvtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateProfile();
            }
        });
    }

    private void onClickRequestPermission() {
        if(mMainActivity == null) return;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mMainActivity.openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mMainActivity.openGallery();
        }
        else {
            String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions,GALLERY_REQUEST_CODE);
        }
    }

    private void setUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)   return;

        edFullName.setText(user.getDisplayName());
        edEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.avt_default).into(imgAvtProfile);
    }

    private void setControls() {
        loader = new ProgressDialog(getActivity());
        loader.setMessage("Updating...");
        imgAvtProfile = (ImageView) mView.findViewById(R.id.img_avt);
        edFullName = mView.findViewById(R.id.ed_full_name);
        edEmail = mView.findViewById(R.id.ed_email);

        btnUpdate = mView.findViewById(R.id.btn_update);

    }
    public void setBitmapImageView(Bitmap bitmapImage){
        imgAvtProfile.setImageBitmap(bitmapImage);

    }

    public void setUriData(Uri uri){
        this.mUri = uri;
    }
    private void onUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;
        String fullName = edFullName.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .setPhotoUri(mUri)
                .build();
        loader.show();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loader.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "update profile successful", Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserInfoInMenuLeft();
                        }
                    }
                });
    }
}
