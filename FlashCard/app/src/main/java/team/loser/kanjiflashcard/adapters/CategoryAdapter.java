package team.loser.kanjiflashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import team.loser.kanjiflashcard.R;
import team.loser.kanjiflashcard.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> mListCategories;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private IClickListener mIClickListener;
    public interface IClickListener{
        void onClickUpdateItem(Category category);
        void onClickDeleteItem(Category category);
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
        if(category == null) return;

        viewBinderHelper.bind(holder.swipeRevealLayout, category.getId());
        holder.tvTimeStamp.setText(category.getTimeStamp());
        holder.tvCateName.setText(category.getName());
        holder.tvDescription.setText(category.getDescription());
        try {
            holder.tvDayCount.setText(countDays(category.getTimeStamp()) +"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickUpdateItem(category);
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickDeleteItem(category);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(mListCategories != null){
            return mListCategories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDayCount, tvTimeStamp,tvCateName, tvNumOfCards, tvDescription;
        private SwipeRevealLayout swipeRevealLayout;
        private TextView tvEdit, tvDelete;
        private ImageButton btnEdit, btnRemove;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayCount = itemView.findViewById(R.id.tv_day_count);
            tvTimeStamp = itemView.findViewById(R.id.tv_timestamp);
            tvCateName = itemView.findViewById(R.id.tv_category_name);
            tvNumOfCards = itemView.findViewById(R.id.tv_number_of_card);
            tvDescription = itemView.findViewById(R.id.tv_description);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnRemove = itemView.findViewById(R.id.btn_remove);
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

}
