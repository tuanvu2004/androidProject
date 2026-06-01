package com.example.mobileapp.model;

import java.util.ArrayList;
import java.util.List;

public class TopicSearchRequest {

    private int page;
    private int size;
    private String sortBy;
    private String sortDir;
    private List<Filter> filters;

    public TopicSearchRequest() {
        this.page = 0;
        this.size = 100;
        this.sortBy = "createdAt";
        this.sortDir = "DESC";
        this.filters = new ArrayList<>();
    }

    public void addFilter(String property, String operator, Object value) {
        this.filters.add(new Filter(property, operator, value));
    }

    public void clearFilters() {
        this.filters.clear();
    }

    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
    public List<Filter> getFilters() { return filters; }
    public void setFilters(List<Filter> filters) { this.filters = filters; }
}
