package com.example.electionsurvey2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for Areas
 * Displays list of areas with click handling
 */
public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {

    private List<Area> areaList;
    private OnItemClickListener listener;

    /**
     * Interface for item click events
     */
    public interface OnItemClickListener {
        void onItemClick(Area area);
    }

    /**
     * Constructor
     * @param areaList List of areas to display
     * @param listener Click listener
     */
    public AreaAdapter(List<Area> areaList, OnItemClickListener listener) {
        this.areaList = areaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area, parent, false);
        return new AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        Area area = areaList.get(position);
        holder.bind(area, listener);
    }

    @Override
    public int getItemCount() {
        return areaList != null ? areaList.size() : 0;
    }

    /**
     * ViewHolder class for Area items
     */
    static class AreaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAreaName;

        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
        }

        /**
         * Bind area data to views
         * @param area Area object
         * @param listener Click listener
         */
        public void bind(Area area, OnItemClickListener listener) {
            tvAreaName.setText(area.getAreaName());

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(area);
                }
            });
        }
    }

    /**
     * Update the list of areas
     * @param newAreas New list of areas
     */
    public void updateAreas(List<Area> newAreas) {
        this.areaList = newAreas;
        notifyDataSetChanged();
    }
}





