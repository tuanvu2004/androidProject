package com.example.mobileapp.model;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    private Long wordId;
    private String english;
    private String example;
    private List<String> options;

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
