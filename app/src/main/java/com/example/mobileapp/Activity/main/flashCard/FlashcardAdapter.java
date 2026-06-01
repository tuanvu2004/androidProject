package com.example.mobileapp.Activity.main.flashCard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.model.Vocabulary;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {

    private List<Vocabulary> list;

    public FlashcardAdapter(List<Vocabulary> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEnglish, tvVietnamese, tvExample;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEnglish = itemView.findViewById(R.id.tvEnglish);
            tvVietnamese = itemView.findViewById(R.id.tvVietnamese);
            tvExample = itemView.findViewById(R.id.tvExample);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Vocabulary v = list.get(position);

        holder.tvEnglish.setText(v.getEnglish());
        holder.tvVietnamese.setText(v.getVietnamese());
        holder.tvExample.setText(v.getExample());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}