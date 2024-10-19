package com.example.ibrahim_bahlawan_weight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeightEntryActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private EditText weightInput;
    private Button addWeightButton, toWeightGoalButton;
    private RecyclerView weightRecyclerView;
    private WeightAdapter weightAdapter;
    private DatabaseHelper dbHelper;
    private List<WeightEntry> weightEntries;
    private double weightGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_entry);

        dbHelper = new DatabaseHelper(this);
        weightEntries = dbHelper.getAllWeights();
        weightGoal = getIntent().getDoubleExtra("weightGoal", -1);

        weightInput = findViewById(R.id.weight_input);
        addWeightButton = findViewById(R.id.add_weight_button);
        toWeightGoalButton = findViewById(R.id.to_weight_goal_button);
        weightRecyclerView = findViewById(R.id.weight_recycler_view);

        weightAdapter = new WeightAdapter(weightEntries, new WeightAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                WeightEntry entry = weightEntries.get(position);
                dbHelper.deleteWeightEntry(entry.getId());
                weightEntries.remove(position);
                weightAdapter.notifyItemRemoved(position);
            }
        });

        weightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weightRecyclerView.setAdapter(weightAdapter);

        addWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = weightInput.getText().toString();
                if (!weightStr.isEmpty()) {
                    double weight = Double.parseDouble(weightStr);
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    long id = dbHelper.addWeightEntry(weight, date);
                    WeightEntry newEntry = new WeightEntry(id, weight, date);
                    weightEntries.add(newEntry);
                    weightAdapter.notifyItemInserted(weightEntries.size() - 1);
                    weightInput.setText("");

                    if (weightGoal != -1 && weight == weightGoal) {
                        checkAndReceiveSms();
                    }

                } else {
                    Toast.makeText(WeightEntryActivity.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toWeightGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightEntryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // i am really not sure why i am not receiving an sms.
    private void checkAndReceiveSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You reached your goal weight.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You reached your goal weight.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



