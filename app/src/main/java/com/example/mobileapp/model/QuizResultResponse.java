package com.example.mobileapp.model;

import java.io.Serializable;
import java.util.List;

public class QuizResultResponse implements Serializable {
    private Long resultId;
    private Long topicId;
    private String topicName;
    private int score;
    private int totalQuestion;
    private int timeTakenSec;
    private String createdAt;
    private List<AnswerDetail> answers;

    public Long getResultId() { return resultId; }
    public Long getTopicId() { return topicId; }
    public String getTopicName() { return topicName; }
    public int getScore() { return score; }
    public int getTotalQuestion() { return totalQuestion; }
    public int getTimeTakenSec() { return timeTakenSec; }
    public String getCreatedAt() { return createdAt; }
    public List<AnswerDetail> getAnswers() { return answers; }

    public static class AnswerDetail implements Serializable {
        private Long wordId;
        private String english;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;

        public boolean isCorrect() { return correct; }
        public String getEnglish() { return english; }
        public String getUserAnswer() { return userAnswer; }
        public String getCorrectAnswer() { return correctAnswer; }
    }
}
