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

    public Long getId()      { return id; }
    public String getName()  { return name; }
    public String getCreatedAt() { return createdAt; }
    public List<Vocabulary> getVocabularies() { return vocabularies; }
    public int getTotalWords() { return totalWords; }
    public int getMasteredWords() { return masteredWords; }
}