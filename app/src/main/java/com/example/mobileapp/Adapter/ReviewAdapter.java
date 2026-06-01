package com.example.mobileapp.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.model.QuizResultResponse;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<QuizResultResponse.AnswerDetail> answers;

    public ReviewAdapter(List<QuizResultResponse.AnswerDetail> answers) {
        this.answers = answers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResultResponse.AnswerDetail detail = answers.get(position);

        holder.txtReviewEnglish.setText(detail.getEnglish());
        holder.txtUserAnswer.setText(detail.getUserAnswer());

        if (detail.isCorrect()) {
            holder.txtUserAnswer.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.layoutCorrectAnswer.setVisibility(View.GONE);
        } else {
            holder.txtUserAnswer.setTextColor(Color.RED);
            holder.layoutCorrectAnswer.setVisibility(View.VISIBLE);
            holder.txtCorrectAnswer.setText(detail.getCorrectAnswer());
        }
    }

    @Override
    public int getItemCount() {
        return answers != null ? answers.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReviewEnglish, txtUserAnswer, txtCorrectAnswer;
        LinearLayout layoutCorrectAnswer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReviewEnglish = itemView.findViewById(R.id.txtReviewEnglish);
            txtUserAnswer = itemView.findViewById(R.id.txtUserAnswer);
            txtCorrectAnswer = itemView.findViewById(R.id.txtCorrectAnswer);
            layoutCorrectAnswer = itemView.findViewById(R.id.layoutCorrectAnswer);
        }
    }
}