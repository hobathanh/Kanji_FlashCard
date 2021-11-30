package team.loser.kanjiflashcard.adapters;

import static team.loser.kanjiflashcard.MainActivity.onlineUserID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.FractionRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import team.loser.kanjiflashcard.R;
import team.loser.kanjiflashcard.models.Card;
import team.loser.kanjiflashcard.models.Category;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Card> mListCards;
    private IClickListener mIClickListener;
    public interface IClickListener{
        void onClickEditCard(Card card);
        void onClickRemoveCard(Card card);

    }
    public CardAdapter(List<Card> listCards, IClickListener iClickListener){
        this.mListCards = listCards;
        this.mIClickListener = iClickListener;
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = mListCards.get(position);
        if(card == null) return;
//        setHeightCard(holder.frontCard, holder.backCard);

        holder.tvTerm.setText(card.getTerm());
        holder.tvDefinition.setText(card.getDefinition());
        holder.tvHowToRead.setText(card.getHowtoread());
        holder.tvExamples.setText(card.getExamples());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickEditCard(card);
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickRemoveCard(card);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mListCards != null){
            return mListCards.size();
        }
        return 0;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTerm, tvDefinition,tvHowToRead, tvExamples;
        private FrameLayout frontCard, backCard;
        private ImageButton btnEdit, btnRemove;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTerm = itemView.findViewById(R.id.tv_term_card_item);
            tvDefinition = itemView.findViewById(R.id.tv_definition_card_item);
            tvHowToRead = itemView.findViewById(R.id.tv_how_to_read_card_item);
            tvExamples = itemView.findViewById(R.id.tv_examples_card_item);
            frontCard = itemView.findViewById(R.id.front_card);
            backCard = itemView.findViewById(R.id.back_card);
            btnEdit = itemView.findViewById(R.id.btn_edit_card_item);
            btnRemove = itemView.findViewById(R.id.btn_remove_card_item);


        }
    }
    private void setHeightCard(View front, View back){
        int frontWidth = front.getLayoutParams().height;
        int backWidth = back.getLayoutParams().height;
        int max = frontWidth >= backWidth? frontWidth: backWidth;

        ViewGroup.LayoutParams frontLayoutParams = front.getLayoutParams();
        frontLayoutParams.height = max;
        front.setLayoutParams(frontLayoutParams);

        ViewGroup.LayoutParams backLayoutParams = front.getLayoutParams();
        backLayoutParams.height = max;
        front.setLayoutParams(backLayoutParams);
    }

}
