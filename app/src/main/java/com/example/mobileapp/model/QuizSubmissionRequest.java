package com.example.mobileapp.model;

import java.util.List;

public class QuizSubmissionRequest {
    private Long topicId;
    private List<AnswerRequest> answers;
    private int timeTakenSec;

    public QuizSubmissionRequest(Long topicId, List<AnswerRequest> answers, int timeTakenSec) {
        this.topicId = topicId;
        this.answers = answers;
        this.timeTakenSec = timeTakenSec;
    }

    public static class AnswerRequest {
        private Long wordId;
        private String userAnswer;

        public AnswerRequest(Long wordId, String userAnswer) {
            this.wordId = wordId;
            this.userAnswer = userAnswer;
        }
    }
}
