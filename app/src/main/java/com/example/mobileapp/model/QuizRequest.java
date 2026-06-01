package com.example.mobileapp.model;

public class QuizRequest {
    private Long topicId;
    private int questionCount;

    public QuizRequest(Long topicId, int questionCount) {
        this.topicId = topicId;
        this.questionCount = questionCount;
    }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}
