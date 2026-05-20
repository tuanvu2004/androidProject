package com.example.mobileapp.model;

public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public T getData() {
        return data;
    }
}