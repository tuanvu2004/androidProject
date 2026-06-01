package com.example.mobileapp.model;

public class Filter {
    private String property;
    private String operator;
    private Object value;

    public Filter(String property, String operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    // Getters and Setters
    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}
