package team.loser.kanjiflashcard.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecorator extends RecyclerView.ItemDecoration {
    private  final  int space;
    private boolean horizontal, vertical;
    public SpacingItemDecorator(int space, boolean vertical, boolean horizontal) {
        this.space = space;
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        if(vertical){
            outRect.bottom = space;
        }
        else if(horizontal){
            outRect.left= space;
            outRect.right=space;
        }
        else{
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }

    }
}
