package com.example.ibrahim_bahlawan_weight;

import com.google.firebase.Timestamp;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import android.util.Log;
import com.google.firebase.firestore.SetOptions;
import java.util.Collections;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String WEIGHTS_COLLECTION = "weights";
    private static final String GOAL_FIELD = "weightGoal";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            Log.e("FirestoreHelper", "No user is signed in");
            return null;
        }
    }

    public void setWeightGoal(double goal, OnCompleteListener<Void> listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e("FirestoreHelper", "Failed to set weight goal: User not signed in");
            listener.onComplete(Tasks.forException(new Exception("User not signed in")));
            return;
        }

        db.collection(USERS_COLLECTION).document(userId)
                .set(Collections.singletonMap(GOAL_FIELD, goal), SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirestoreHelper", "Weight goal set successfully");
                    } else {
                        Log.e("FirestoreHelper", "Failed to set weight goal", task.getException());
                    }
                    listener.onComplete(task);
                });
    }

    public void getWeightGoal(OnCompleteListener<Double> listener) {
        String userId = getCurrentUserId();
        if (userId == null) return;

        db.collection(USERS_COLLECTION).document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Double goal = task.getResult().getDouble(GOAL_FIELD);
                        listener.onComplete(Tasks.forResult(goal));
                    } else {
                        listener.onComplete(Tasks.forException(new Exception("Failed to get weight goal")));
                    }
                });
    }

    public void addWeightEntry(double weight, OnCompleteListener<DocumentReference> listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forException(new Exception("User not signed in")));
            return;
        }

        Map<String, Object> weightEntry = new HashMap<>();
        weightEntry.put("weight", weight);
        weightEntry.put("date", FieldValue.serverTimestamp());

        db.collection(USERS_COLLECTION).document(userId)
                .collection(WEIGHTS_COLLECTION)
                .add(weightEntry)
                .addOnCompleteListener(listener);
    }

    public void getAllWeights(OnCompleteListener<List<WeightEntry>> listener) {
        String userId = getCurrentUserId();
        if (userId == null) return;

        db.collection(USERS_COLLECTION).document(userId)
                .collection(WEIGHTS_COLLECTION)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<WeightEntry> weightEntries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            double weight = document.getDouble("weight");
                            Timestamp timestamp = document.getTimestamp("date");
                            weightEntries.add(new WeightEntry(id, weight, timestamp));
                        }
                        listener.onComplete(Tasks.forResult(weightEntries));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }


    public void deleteWeightEntry(String weightId, OnCompleteListener<Void> listener) {
        String userId = getCurrentUserId();
        if (userId == null) return;

        db.collection(USERS_COLLECTION).document(userId)
                .collection(WEIGHTS_COLLECTION).document(weightId)
                .delete()
                .addOnCompleteListener(listener);
    }
}