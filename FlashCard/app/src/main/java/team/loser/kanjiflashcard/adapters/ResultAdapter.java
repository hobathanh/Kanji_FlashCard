package team.loser.kanjiflashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.loser.kanjiflashcard.R;
import team.loser.kanjiflashcard.models.ResultItem;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultItemViewHolder> {
    private List<ResultItem> mListResultItems;

    public ResultAdapter(List<ResultItem> list){
        this.mListResultItems = list;
    }
    @NonNull
    @Override
    public ResultItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        return new ResultItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultItemViewHolder holder, int position) {
        ResultItem resultItem = mListResultItems.get(position);
        if (resultItem==null) return;

        holder.tvTerm.setText(resultItem.getTerm());
        holder.tvDefinition.setText(resultItem.getDefinition());
    }

    @Override
    public int getItemCount() {
        if(mListResultItems!=null){
            return mListResultItems.size();
        }
        return 0;
    }

    public class   ResultItemViewHolder extends  RecyclerView.ViewHolder{
        private TextView tvTerm, tvDefinition;
        public ResultItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTerm = itemView.findViewById(R.id.tv_term_result_item);
            tvDefinition = itemView.findViewById(R.id.tv_definition_result_item);
        }
    }
}
