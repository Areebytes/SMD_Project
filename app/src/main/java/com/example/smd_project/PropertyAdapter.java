package com.example.smd_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private List<Property> propertyList;

    public PropertyAdapter(List<Property> propertyList, OnItemClickListener listener) {
        this.propertyList = propertyList;
        this.listener = listener;
    }

    // Call this to refresh list (e.g. after filter)
    public void updateList(List<Property> newList) {
        this.propertyList = newList;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property p = propertyList.get(position);
        holder.tvType.setText(p.getType().toUpperCase());
        holder.tvName.setText(p.getName());
        holder.tvLocation.setText(p.getLocation());
        holder.tvPrice.setText("$" + String.format("%,d", p.getPrice()));
        holder.tvFeatured.setVisibility(
                p.isFeatured() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(p);
        });
    }

    @Override
    public int getItemCount() { return propertyList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvName, tvLocation, tvPrice, tvFeatured;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType     = itemView.findViewById(R.id.tv_type);
            tvName     = itemView.findViewById(R.id.tv_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice    = itemView.findViewById(R.id.tv_price);
            tvFeatured = itemView.findViewById(R.id.tv_featured);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Property property);

    }
    private OnItemClickListener listener;
}