package com.example.smd_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SellerPropertyAdapter
        extends RecyclerView.Adapter<SellerPropertyAdapter.ViewHolder> {

    public interface OnPropertyActionListener {
        void onEditClick(SellerProperty property);
        void onDeleteClick(SellerProperty property);
    }

    private final Context                  context;
    private final List<SellerProperty>     list;
    private final OnPropertyActionListener listener;

    public SellerPropertyAdapter(Context context,
                                 List<SellerProperty> list,
                                 OnPropertyActionListener listener) {
        this.context  = context;
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property_seller, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        SellerProperty p = list.get(position);
        if (p == null) return;

        h.tvName.setText(p.getTitle() != null ? p.getTitle() : "No Title");
        h.tvLocation.setText(p.getLocation() != null ? p.getLocation() : "No Location");
        h.tvPrice.setText("$" + String.format("%,.0f", p.getPrice()));
        h.tvType.setText(p.getType() != null ? p.getType().toUpperCase() : "");

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(h.ivProperty);
        } else {
            h.ivProperty.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        h.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(p);
        });
        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProperty;
        TextView  tvName, tvLocation, tvPrice, tvType;
        Button    btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProperty = itemView.findViewById(R.id.iv_property);
            tvName     = itemView.findViewById(R.id.tv_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice    = itemView.findViewById(R.id.tv_price);
            tvType     = itemView.findViewById(R.id.tv_type);
            btnEdit    = itemView.findViewById(R.id.btn_edit);
            btnDelete  = itemView.findViewById(R.id.btn_delete);
        }
    }
}