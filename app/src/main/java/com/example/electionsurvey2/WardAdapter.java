package com.example.electionsurvey2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for Wards
 * Displays list of wards with click handling
 */
public class WardAdapter extends RecyclerView.Adapter<WardAdapter.WardViewHolder> {

    private List<Ward> wardList;
    private OnItemClickListener listener;

    /**
     * Interface for item click events
     */
    public interface OnItemClickListener {
        void onItemClick(Ward ward);
    }

    /**
     * Constructor
     * @param wardList List of wards to display
     * @param listener Click listener
     */
    public WardAdapter(List<Ward> wardList, OnItemClickListener listener) {
        this.wardList = wardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ward, parent, false);
        return new WardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WardViewHolder holder, int position) {
        Ward ward = wardList.get(position);
        holder.bind(ward, listener);
    }

    @Override
    public int getItemCount() {
        return wardList != null ? wardList.size() : 0;
    }

    /**
     * ViewHolder class for Ward items
     */
    static class WardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWardName;

        public WardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWardName = itemView.findViewById(R.id.tvWardName);
        }

        /**
         * Bind ward data to views
         * @param ward Ward object
         * @param listener Click listener
         */
        public void bind(Ward ward, OnItemClickListener listener) {
            tvWardName.setText(ward.getWardName());

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(ward);
                }
            });
        }
    }

    /**
     * Update the list of wards
     * @param newWards New list of wards
     */
    public void updateWards(List<Ward> newWards) {
        this.wardList = newWards;
        notifyDataSetChanged();
    }
}





