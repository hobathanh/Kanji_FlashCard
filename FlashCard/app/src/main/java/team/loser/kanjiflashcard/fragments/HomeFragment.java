package team.loser.kanjiflashcard.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.loser.kanjiflashcard.MainActivity;
import team.loser.kanjiflashcard.R;
import team.loser.kanjiflashcard.adapters.CategoryAdapter;
import team.loser.kanjiflashcard.models.Category;
import team.loser.kanjiflashcard.utils.SpacingItemDecorator;

public class HomeFragment extends Fragment {
    private MainActivity mMainActivity;
    private View mView;



    private RecyclerView rcvCategories;
    private CategoryAdapter mCategoryAdapter;
    private List<Category> mListCategories;
    private FloatingActionButton btnAddCategory;
    private ProgressDialog loader;
    private String key = "", Category, description;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mMainActivity = (MainActivity)getActivity();
        setControls();
        setEvents();
        getListCategoriesFromRealtimeDataBase();
        return mView;
    }

    private void setEvents() {
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategory();
            }
        });
    }

    private void addNewCategory() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_category_dialog);

        final EditText edCategoryName = dialog.findViewById(R.id.ed_category_name);
        final EditText edDescription = dialog.findViewById(R.id.ed_category_description);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = edCategoryName.getText().toString().trim();
                String description = edDescription.getText().toString().trim();
                String categoryId = mMainActivity.reference.push().getKey();
                String timeStamp = new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(new Date());
                if(categoryName.isEmpty()){
                    edCategoryName.setError("Category name is required");
                    return;
                }
                if(description.isEmpty()){
                    description = "No description";
                }
                loader.setMessage("Adding...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();
                Category newCategory = new Category(categoryId, categoryName, description, timeStamp);

                mMainActivity.reference.child(categoryId).setValue(newCategory).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Add successfully!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                        else {
                            String err =  task.getException().toString();
                            Toast.makeText(getActivity()  , "Add failed! " + err, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        loader.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void setControls() {
        //recycler view
        rcvCategories = mView.findViewById(R.id.rcv_list_categories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvCategories.setHasFixedSize(true);
        rcvCategories.setLayoutManager(linearLayoutManager);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        rcvCategories.addItemDecoration(itemDecorator);

        mListCategories = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(mListCategories,new CategoryAdapter.IClickListener(){

            @Override
            public void onClickUpdateItem(Category category) {
                onClickUpdateCategory(category);
            }

            @Override
            public void onClickDeleteItem(Category category) {
                onClickDeleteCategory(category);
            }
        });

        rcvCategories.setAdapter(mCategoryAdapter);

        loader = new ProgressDialog(getContext());
        btnAddCategory = mView.findViewById(R.id.btn_add_category);
    }

    private void getListCategoriesFromRealtimeDataBase(){
        mMainActivity.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListCategories.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);
                    mListCategories.add(category);
                }
                mCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onClickDeleteCategory(Category category){
        new AlertDialog.Builder(getContext())
                .setTitle("Delete")
                .setMessage("You gonna delete that shit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mMainActivity.reference.child(category.getId()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getContext(), "delete successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }
    private void onClickUpdateCategory(Category category){
        Dialog editDialog = new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.edit_category_dialog);

        EditText edCateName = editDialog.findViewById(R.id.ed_category_name_edit);
        EditText edDescription = editDialog.findViewById(R.id.ed_category_description_edit);
        Button btnCancel = editDialog.findViewById(R.id.btn_cancel_edit);
        Button btnUpdate = editDialog.findViewById(R.id.btn_update_category_edit);

        edCateName.setText(category.getName());
        edCateName.setSelection(category.getName().length());
        edDescription.setText(category.getDescription());
        edDescription.setSelection(category.getDescription().length());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog.dismiss();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cateName = edCateName.getText().toString().trim();
                String desc = edDescription.getText().toString().trim();
                String timeStamp = new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(new Date());
                Category newCategory = new Category(category.getId(), cateName,desc,timeStamp);
                loader.setMessage("Updating...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();
                mMainActivity.reference.child(category.getId()).setValue(newCategory).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "update successful!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String err = task.getException().toString();
                            Toast.makeText(getActivity(), "update failed!", Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });
                editDialog.dismiss();
            }
        });
        editDialog.show();
    }

}
