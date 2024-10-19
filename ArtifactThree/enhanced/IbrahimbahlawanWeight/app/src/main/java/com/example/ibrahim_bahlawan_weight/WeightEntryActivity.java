package com.example.ibrahim_bahlawan_weight;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class WeightEntryActivity extends AppCompatActivity {

    private EditText weightInput;
    private Button addWeightButton;
    private Button toWeightGoalButton;
    private RecyclerView weightRecyclerView;
    private WeightAdapter weightAdapter;
    private FirestoreHelper firestoreHelper;
    private List<WeightEntry> weightEntries;
    private double weightGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_entry);

        firestoreHelper = new FirestoreHelper();
        weightEntries = new ArrayList<>();
        weightGoal = getIntent().getDoubleExtra("weightGoal", -1);

        weightInput = findViewById(R.id.weight_input);
        addWeightButton = findViewById(R.id.add_weight_button);
        toWeightGoalButton = findViewById(R.id.to_weight_goal_button);
        weightRecyclerView = findViewById(R.id.weight_recycler_view);

        weightAdapter = new WeightAdapter(weightEntries, new WeightAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                WeightEntry entry = weightEntries.get(position);
                deleteWeightEntry(entry.getId(), position);
            }
        });

        weightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weightRecyclerView.setAdapter(weightAdapter);

        loadWeightEntries();

        addWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = weightInput.getText().toString();
                if (!weightStr.isEmpty()) {
                    double weight = Double.parseDouble(weightStr);
                    addWeightEntry(weight);
                } else {
                    Toast.makeText(WeightEntryActivity.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toWeightGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightEntryActivity.this, MainActivity.class);
                startActivity(intent);
                // Optional: Finish current activity if you don't want to keep it in the back stack
                // finish();
            }
        });
    }

    private void loadWeightEntries() {
        firestoreHelper.getAllWeights(new OnCompleteListener<List<WeightEntry>>() {
            @Override
            public void onComplete(@NonNull Task<List<WeightEntry>> task) {
                if (task.isSuccessful()) {
                    weightEntries.clear();
                    weightEntries.addAll(task.getResult());
                    weightAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WeightEntryActivity.this, "Failed to load weight entries", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addWeightEntry(double weight) {
        firestoreHelper.addWeightEntry(weight, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    weightInput.setText("");
                    loadWeightEntries();
                    if (weightGoal != -1 && weight == weightGoal) {
                        Toast.makeText(WeightEntryActivity.this, "You reached your goal weight!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WeightEntryActivity.this, "Failed to add weight entry", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteWeightEntry(String weightId, final int position) {
        firestoreHelper.deleteWeightEntry(weightId, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    weightEntries.remove(position);
                    weightAdapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(WeightEntryActivity.this, "Failed to delete weight entry", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}