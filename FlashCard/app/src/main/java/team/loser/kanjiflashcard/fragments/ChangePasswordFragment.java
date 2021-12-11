package team.loser.kanjiflashcard.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import team.loser.kanjiflashcard.R;


public class ChangePasswordFragment extends Fragment {
    public static final String CHANGE_PWD_FRAGMENT_NAME = ChangePasswordFragment.class.getName();
    private View mView;
    private EditText edNewPassword;
    private EditText edConfirmNewPassword;
    private Button btnChangePassword;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);
        initUI();
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickChangePassword();
            }
        });


        return mView;
    }

    private void initUI(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating...");
        edNewPassword = mView.findViewById(R.id.ed_new_password);
        edConfirmNewPassword = mView.findViewById(R.id.ed_confirm_new_password);
        btnChangePassword = mView.findViewById(R.id.btn_change_password);
    }

    private void OnClickChangePassword(){
        String strNewPassword = edNewPassword.getText().toString().trim();
        String strConfirmNewPassword = edConfirmNewPassword.getText().toString().trim();
        if(strNewPassword.isEmpty()){
            edNewPassword.setError("Password is required");
            return;
        }
        if(strConfirmNewPassword.isEmpty()){
            edConfirmNewPassword.setError("Password is required");
            return;
        }
        if (!strNewPassword.equals(strConfirmNewPassword)) {
            Toast.makeText(getActivity(), "Password not match", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(strNewPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "New password successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}