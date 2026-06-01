package com.example.mobileapp.model;

import java.util.List;

public class QuizHistoryPageResponse {
    private List<QuizResultResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public List<QuizResultResponse> getItems() { return items; }
    public void setItems(List<QuizResultResponse> items) { this.items = items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}
