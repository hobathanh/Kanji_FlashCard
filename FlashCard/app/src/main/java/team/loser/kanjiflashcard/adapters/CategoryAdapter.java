package team.loser.kanjiflashcard.adapters;

import static team.loser.kanjiflashcard.MainActivity.onlineUserID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import team.loser.kanjiflashcard.MainActivity;
import team.loser.kanjiflashcard.R;
import team.loser.kanjiflashcard.fragments.CardsFragment;
import team.loser.kanjiflashcard.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> mListCategories;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private IClickListener mIClickListener;
    private DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("cards").child(onlineUserID);
    private int numOfCards = 0;

    public interface IClickListener {
        void onClickUpdateItem(Category category);

        void onClickDeleteItem(Category category);

        void onClickItemCategory(DatabaseReference categoryRef);

        void onClickStartReview(Category category);
        void onClickStartPractice(Category category);
    }

    public CategoryAdapter(List<Category> mListCategories, IClickListener iClickListener) {
        this.mListCategories = mListCategories;
        this.mIClickListener = iClickListener;

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mListCategories.get(position);
        if (category == null) return;

        viewBinderHelper.bind(holder.swipeRevealLayout, category.getId());
        holder.tvTimeStamp.setText(category.getTimeStamp());
        holder.tvCateName.setText(category.getName());
        holder.tvDescription.setText(category.getDescription());
        setNumOfCards_Category(category, holder.tvNumOfCards, holder.btnReview_Add, holder.btnPractice); // must call before set practice/review button
        try {
            long days = countDays(category.getTimeStamp());
            holder.tvDayCount.setText(days < 1 ? "recently" : days + " days ago");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // actions listener
        holder.btnEdit.setOnClickListener(view -> mIClickListener.onClickUpdateItem(category));
        holder.btnRemove.setOnClickListener(view -> mIClickListener.onClickDeleteItem(category));
        holder.btnAddCard.setOnClickListener(view -> mIClickListener.onClickItemCategory(mUserReference.child(category.getId())));
        holder.btnReview_Add.setOnClickListener(view -> {
            if (holder.btnReview_Add.getText() == "ADD CARDS") {
                mIClickListener.onClickItemCategory(mUserReference.child(category.getId()));
            } else {
                mIClickListener.onClickStartReview(category);
            }
        });
        holder.btnPractice.setOnClickListener(view -> mIClickListener.onClickStartPractice(category));
        holder.tvCateName.setOnClickListener(view -> mIClickListener.onClickItemCategory(mUserReference.child(category.getId())));
        //layout click
        holder.tvNumOfCards.setOnClickListener(view -> mIClickListener.onClickItemCategory(mUserReference.child(category.getId())));
        holder.loButtons.setOnClickListener(view -> mIClickListener.onClickItemCategory(mUserReference.child(category.getId())));
        holder.tvDescription.setOnClickListener(view -> mIClickListener.onClickItemCategory(mUserReference.child(category.getId())));
    }


    @Override
    public int getItemCount() {
        if (mListCategories != null) {
            return mListCategories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayCount, tvTimeStamp, tvCateName, tvNumOfCards, tvDescription;
        private SwipeRevealLayout swipeRevealLayout;
        private ImageButton btnEdit, btnRemove, btnAddCard;
        private Button btnReview_Add, btnPractice;
        private LinearLayout loBottom, loDescription, loButtons;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayCount = itemView.findViewById(R.id.tv_day_count);
            tvTimeStamp = itemView.findViewById(R.id.tv_timestamp);
            tvCateName = itemView.findViewById(R.id.tv_category_name);
            tvNumOfCards = itemView.findViewById(R.id.tv_cards_or_card);
            tvDescription = itemView.findViewById(R.id.tv_description);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            btnEdit = itemView.findViewById(R.id.btn_edit_category_item);
            btnRemove = itemView.findViewById(R.id.btn_remove_category_item);
            btnAddCard = itemView.findViewById(R.id.btn_add_card_category_item);
            btnReview_Add = itemView.findViewById(R.id.btn_review_or_add_category_item);
            btnPractice = itemView.findViewById(R.id.btn_practice_category_item);
            //layout
            loButtons = itemView.findViewById((R.id.layout_buttons));
        }
    }

    public long countDays(String fromDate)
            throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH);
        Date firstDate = sdf.parse(fromDate);
        Date secondDate = sdf.parse(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(new Date()));

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return diff;
    }

    private void setNumOfCards_Category(Category category, TextView textView, Button btnReview, Button btnPractice) {
        DatabaseReference cardRef = mUserReference.child(category.getId()).child("flashCards");
        cardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot i : snapshot.getChildren()) {
                    count++;
                }
                if (count < 2) {
                    textView.setText(count + " card");
                } else {
                    textView.setText(count + " cards");
                }
                if (count < 5) {
                    btnPractice.setVisibility(View.INVISIBLE);
                    btnReview.setText("ADD CARDS");
                } else {
                    btnPractice.setVisibility(View.VISIBLE);
                    btnReview.setText("REVIEW");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textView.setText("error");
            }
        });
    }
}
