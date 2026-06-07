package com.example.mobileapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Topic implements Serializable {

    private Long id;
    private String name;

    @SerializedName("createdAt")
    private String createdAt;

    private List<Vocabulary> vocabularies;
    private int totalWords;
    private int masteredWords;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    private String userEmail;

    public Long getId()      { return id; }
    public String getName()  { return name; }
    public void setName(String name) { this.name = name; }
    public String getCreatedAt() { return createdAt; }
    public List<Vocabulary> getVocabularies() { return vocabularies; }
    public void setVocabularies(List<Vocabulary> vocabularies) { this.vocabularies = vocabularies; }
    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }
    public int getMasteredWords() { return masteredWords; }
    public boolean isDeleted() { return isDeleted; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}