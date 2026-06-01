package com.example.mobileapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicPageResponse {

    @SerializedName(value = "items", alternate = {"content"})
    private List<Topic> items;
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;

    public List<Topic> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}