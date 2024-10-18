package com.example.ibrahim_bahlawan_weight;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class WeightEntry {
    private String id;
    private double weight;
    private Timestamp date;

    public WeightEntry(String id, double weight, Timestamp date) {
        this.id = id;
        this.weight = weight;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getFormattedDate() {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date.toDate()) : "";
    }
}