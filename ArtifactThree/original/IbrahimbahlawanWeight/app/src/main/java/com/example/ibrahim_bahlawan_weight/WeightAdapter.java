package com.example.ibrahim_bahlawan_weight;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<WeightEntry> weightEntries;
    private OnDeleteClickListener onDeleteClickListener;

    public WeightAdapter(List<WeightEntry> weightEntries, OnDeleteClickListener onDeleteClickListener) {
        this.weightEntries = weightEntries;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        WeightEntry entry = weightEntries.get(position);
        holder.weightTextView.setText(String.valueOf(entry.getWeight()));
        holder.dateTextView.setText(entry.getDate());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return weightEntries.size();
    }

    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView, dateTextView;
        Button deleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weight_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}

