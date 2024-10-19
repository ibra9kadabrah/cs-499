package com.example.ibrahim_bahlawan_weight;

public class WeightEntry {
    private long id;
    private double weight;
    private String date;

    public WeightEntry(long id, double weight, String date) {
        this.id = id;
        this.weight = weight;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }
}

