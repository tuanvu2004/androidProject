package com.example.mobileapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobileapp.database.Converters;

import java.util.List;

@Entity(tableName = "local_quiz_questions")
@TypeConverters(Converters.class)
public class LocalQuizQuestion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private Long topicId;
    private String userEmail;
    private Long wordId;
    private String english;
    private String example;
    private List<String> options;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
