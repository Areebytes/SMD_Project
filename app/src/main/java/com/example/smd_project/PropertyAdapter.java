package com.example.smd_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private List<Property> propertyList;
    private DatabaseHelper dbHelper;
    private Context context;
    private OnItemClickListener listener;

    public PropertyAdapter(Context context, List<Property> propertyList, OnItemClickListener listener) {
        this.context = context;
        this.propertyList = propertyList != null ? propertyList : new ArrayList<>();
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Updates the list using DiffUtil for better performance and smoother animations.
     */
    public void updateList(List<Property> newList) {
        if (newList == null) return;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return propertyList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Property oldItem = propertyList.get(oldItemPosition);
                Property newItem = newList.get(newItemPosition);
                if (oldItem == null || newItem == null) return false;
                return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Property oldItem = propertyList.get(oldItemPosition);
                Property newItem = newList.get(newItemPosition);
                if (oldItem == null || newItem == null) return false;
                
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getPrice() == newItem.getPrice() &&
                        oldItem.isFeatured() == newItem.isFeatured() &&
                        oldItem.getType().equals(newItem.getType()) &&
                        oldItem.getLocation().equals(newItem.getLocation()) &&
                        String.valueOf(oldItem.getImageUrl()).equals(String.valueOf(newItem.getImageUrl()));
            }
        });

        this.propertyList = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property p = propertyList.get(position);
        if (p == null) return;

        // 🔧 FIX: Crash prevention for type.toUpperCase()
        String type = p.getType() != null ? p.getType() : "";
        holder.tvType.setText(type.toUpperCase());
        
        holder.tvName.setText(p.getName() != null ? p.getName() : "No Name");
        holder.tvLocation.setText(p.getLocation() != null ? p.getLocation() : "No Location");
        holder.tvPrice.setText("$" + String.format("%,d", p.getPrice()));
        
        holder.tvFeatured.setVisibility(p.isFeatured() ? View.VISIBLE : View.GONE);
        
        // 🔥 Glide for high-performance image loading
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.ivProperty);
        } else {
            holder.ivProperty.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(p);
        });

        String propertyId = p.getId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && propertyId != null) {
            String userId = user.getUid();
            
            boolean isFav = dbHelper.isFavourite(propertyId, userId);
            holder.btnFavorite.setImageResource(
                    isFav ? android.R.drawable.btn_star_big_on
                            : android.R.drawable.btn_star_big_off
            );

            holder.btnFavorite.setOnClickListener(v -> {
                boolean currentlyFav = dbHelper.isFavourite(propertyId, userId);
                if (currentlyFav) {
                    dbHelper.removeFavourite(propertyId, userId);
                    holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                } else {
                    // Match the 8-argument signature in DatabaseHelper v3
                    dbHelper.addFavourite(
                            userId,
                            propertyId,
                            p.getName(),
                            p.getPrice(),
                            p.getLocation(),
                            p.getType(),
                            p.getImageUrl(),
                            p.isFeatured()
                    );
                    holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return propertyList != null ? propertyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvName, tvLocation, tvPrice, tvFeatured;
        ImageView btnFavorite, ivProperty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvFeatured = itemView.findViewById(R.id.tv_featured);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            ivProperty = itemView.findViewById(R.id.iv_property);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Property property);
    }
}
