package com.example.ibrahim_bahlawan_weight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private EditText goalInput;
    private Button addGoalButton, deleteGoalButton, toWeightEntryButton;
    private TextView currentGoalValue;
    private DatabaseHelper dbHelper;
    private double weightGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        dbHelper = new DatabaseHelper(this);

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
                    weightGoal = goal;
                    dbHelper.setWeightGoal(goal);
                    loadCurrentGoal();
                    Toast.makeText(MainActivity.this, "Goal set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteWeightGoal();
                loadCurrentGoal();
                Toast.makeText(MainActivity.this, "Goal deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        toWeightEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightEntryActivity.class);
                intent.putExtra("weightGoal", weightGoal); // Pass the weight goal to WeightEntryActivity
                startActivity(intent);
            }
        });
    }

    private void loadCurrentGoal() {
        weightGoal = dbHelper.getWeightGoal();
        if (weightGoal != -1) {
            currentGoalValue.setText(String.valueOf(weightGoal) + " kg");
        } else {
            currentGoalValue.setText("No goal set");
        }
    }
}



