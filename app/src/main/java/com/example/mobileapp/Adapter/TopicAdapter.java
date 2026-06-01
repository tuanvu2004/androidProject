package com.example.mobileapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.Activity.main.flashCard.FlashcardActivity;
import com.example.mobileapp.R;
import com.example.mobileapp.model.QuizResultResponse;
import com.example.mobileapp.model.Topic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private final List<Topic> topics;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private Map<Long, QuizResultResponse> historyMap = new HashMap<>();
    private boolean isLibraryMode = false;

    public interface OnItemClickListener {
        void onItemClick(Topic topic);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Topic topic);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setLibraryMode(boolean libraryMode) {
        this.isLibraryMode = libraryMode;
    }

    public TopicAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    public TopicAdapter(List<Topic> topics, OnItemClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    public void setHistoryMap(Map<Long, QuizResultResponse> historyMap) {
        this.historyMap = historyMap;
        notifyDataSetChanged();
    }

    public void removeTopic(Long topicId) {
        for (int i = 0; i < topics.size(); i++) {
            if (topics.get(i).getId().equals(topicId)) {
                topics.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_project, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);

        holder.txtProjectName.setText(topic.getName());

        if (isLibraryMode) {
            // CHẾ ĐỘ THƯ VIỆN: Hiển thị kết quả của lần thi gần nhất từ historyMap
            int total = topic.getTotalWords();
            int score = 0;
            boolean hasHistory = false;

            if (historyMap != null && historyMap.containsKey(topic.getId())) {
                QuizResultResponse latest = historyMap.get(topic.getId());
                if (latest != null) {
                    score = latest.getScore();
                    total = latest.getTotalQuestion();
                    hasHistory = true;
                }
            }

            // Fallback nếu total chưa có (chưa load hoặc topic mới)
            if (total == 0 && topic.getVocabularies() != null) {
                total = topic.getVocabularies().size();
            }

            holder.txtCardCount.setText(String.format(Locale.getDefault(), "%d/%d câu đúng", score, total));
            
            if (hasHistory) {
                if (total > 0) {
                    int percentage = (score * 100) / total;
                    holder.txtCreatedAt.setText(String.format(Locale.getDefault(), "Lần cuối: %d%%", percentage));
                } else {
                    holder.txtCreatedAt.setText("Đã hoàn thành");
                }
            } else {
                holder.txtCreatedAt.setText("Chưa làm bài");
            }
        } else {
            // CHẾ ĐỘ TRANG CHỦ: Tên topic + Số thẻ + Thời gian tạo (dd/MM/yyyy)
            int cardCount = topic.getTotalWords();
            if (cardCount == 0 && topic.getVocabularies() != null) {
                cardCount = topic.getVocabularies().size();
            }
            holder.txtCardCount.setText(String.format(Locale.getDefault(), "%d thẻ", cardCount));
            
            holder.txtCreatedAt.setText(formatShortDate(topic.getCreatedAt()));
        }

        holder.cardStudySet.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(topic);
            } else {
                Intent intent = new Intent(v.getContext(), FlashcardActivity.class);
                intent.putExtra("TOPIC_NAME", topic.getName());
                if (topic.getVocabularies() != null) {
                    intent.putExtra("VOCAB_LIST", new ArrayList<>(topic.getVocabularies()));
                }
                v.getContext().startActivity(intent);
            }
        });

        holder.cardStudySet.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(topic);
                return true;
            }
            return false;
        });
    }

    private String formatShortDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "Mới tạo";
        try {
            // Xử lý các định dạng từ server (yyyy-MM-dd hoặc ISO 8601)
            String cleanDate = dateStr.contains("T") ? dateStr.split("T")[0] : dateStr;
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdfInput.parse(cleanDate);
            
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdfOutput.format(date);
        } catch (Exception e) {
            // Fallback nếu có lỗi định dạng
            if (dateStr.length() >= 10) {
                String ymd = dateStr.substring(0, 10);
                String[] parts = ymd.split("-");
                if (parts.length == 3) return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "Mới tạo";
        try {
            // Server trả về yyyy-MM-dd'T'HH:mm:ss... hoặc yyyy-MM-dd
            // Ta chỉ lấy phần ngày yyyy-MM-dd
            String ymd = dateStr.split("T")[0];
            String[] parts = ymd.split("-");
            if (parts.length == 3) {
                // Trả về dd/MM/yyyy
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
            return ymd;
        } catch (Exception e) {
            return dateStr;
        }
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView txtProjectName, txtCardCount, txtCreatedAt;
        androidx.cardview.widget.CardView cardStudySet;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProjectName = itemView.findViewById(R.id.txtProjectName);
            txtCardCount = itemView.findViewById(R.id.txtCardCount);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            cardStudySet = itemView.findViewById(R.id.cardStudySet);
        }
    }
}
