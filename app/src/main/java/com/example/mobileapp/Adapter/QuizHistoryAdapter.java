package com.example.mobileapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.model.QuizResultResponse;

import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.ViewHolder> {

    private final List<QuizResultResponse> historyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(QuizResultResponse item);
    }

    public QuizHistoryAdapter(List<QuizResultResponse> historyList) {
        this.historyList = historyList;
    }

    public QuizHistoryAdapter(List<QuizResultResponse> historyList, OnItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResultResponse item = historyList.get(position);

        // Hiển thị tên Topic
        String topicName = item.getTopicName();
        holder.txtProjectName.setText(topicName != null ? topicName : "Không xác định");
        
        // Hiển thị số câu đúng
        int total = item.getTotalQuestion();
        int correct = item.getScore(); // score chính là số câu đúng
        
        holder.txtCardCount.setText(String.format(Locale.getDefault(), "%d/%d câu đúng", correct, total));
        holder.txtCreatedAt.setText(item.getCreatedAt() != null ? item.getCreatedAt() : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProjectName, txtCardCount, txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProjectName = itemView.findViewById(R.id.txtProjectName);
            txtCardCount = itemView.findViewById(R.id.txtCardCount);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
}
