package com.example.mobileapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.model.Vocabulary;

import java.util.List;

public class VocabEditAdapter extends RecyclerView.Adapter<VocabEditAdapter.ViewHolder> {

    private List<Vocabulary> vocabularies;
    private OnVocabActionListener actionListener;

    public interface OnVocabActionListener {
        void onEdit(Vocabulary vocabulary, int position);
        void onDelete(Vocabulary vocabulary, int position);
    }

    public VocabEditAdapter(List<Vocabulary> vocabularies, OnVocabActionListener actionListener) {
        this.vocabularies = vocabularies;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocab_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vocabulary v = vocabularies.get(position);
        holder.tvEnglish.setText(v.getEnglish());
        holder.tvVietnamese.setText(v.getVietnamese());
        
        if (v.getExample() != null && !v.getExample().isEmpty()) {
            holder.tvExample.setVisibility(View.VISIBLE);
            holder.tvExample.setText(v.getExample());
        } else {
            holder.tvExample.setVisibility(View.GONE);
        }
        
        holder.btnEdit.setOnClickListener(view -> actionListener.onEdit(v, position));
        holder.btnDelete.setOnClickListener(view -> actionListener.onDelete(v, position));
    }

    @Override
    public int getItemCount() {
        return vocabularies != null ? vocabularies.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEnglish, tvVietnamese, tvExample;
        ImageButton btnEdit, btnDelete;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglish = itemView.findViewById(R.id.tvEnglish);
            tvVietnamese = itemView.findViewById(R.id.tvVietnamese);
            tvExample = itemView.findViewById(R.id.tvExample);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}