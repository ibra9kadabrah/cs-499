package com.example.ibrahim_bahlawan_weight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText goalInput;
    private Button addGoalButton, deleteGoalButton, toWeightEntryButton;
    private TextView currentGoalValue;
    private FirestoreHelper firestoreHelper;
    private double weightGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        firestoreHelper = new FirestoreHelper();

        goalInput = findViewById(R.id.goal_input);
        addGoalButton = findViewById(R.id.add_goal_button);
        deleteGoalButton = findViewById(R.id.delete_goal_button);
        currentGoalValue = findViewById(R.id.current_goal_value);
        toWeightEntryButton = findViewById(R.id.to_weight_entry_button);

        loadCurrentGoal();

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalStr = goalInput.getText().toString();
                if (!goalStr.isEmpty()) {
                    double goal = Double.parseDouble(goalStr);
                    setWeightGoal(goal);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWeightGoal();
            }
        });

        toWeightEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightEntryActivity.class);
                intent.putExtra("weightGoal", weightGoal);
                startActivity(intent);
            }
        });
    }

    private void loadCurrentGoal() {
        firestoreHelper.getWeightGoal(new OnCompleteListener<Double>() {
            @Override
            public void onComplete(@NonNull Task<Double> task) {
                if (task.isSuccessful()) {
                    Double goal = task.getResult();
                    if (goal != null && goal > 0) {
                        weightGoal = goal;
                        currentGoalValue.setText(String.valueOf(weightGoal) + " kg");
                    } else {
                        currentGoalValue.setText("No goal set");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load goal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setWeightGoal(double goal) {
        firestoreHelper.setWeightGoal(goal, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    weightGoal = goal;
                    loadCurrentGoal();
                    Toast.makeText(MainActivity.this, "Goal set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to set goal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteWeightGoal() {
        firestoreHelper.setWeightGoal(-1, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadCurrentGoal();
                    Toast.makeText(MainActivity.this, "Goal deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete goal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}