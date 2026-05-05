package com.example.smd_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class PropertyDetailFragment extends Fragment {

    private Property property;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);

        ImageView ivImage = view.findViewById(R.id.iv_detail_image);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvPrice = view.findViewById(R.id.tv_detail_price);
        TextView tvLocation = view.findViewById(R.id.tv_detail_location);
        TextView tvType = view.findViewById(R.id.tv_detail_type);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvAgentName = view.findViewById(R.id.tv_agent_name);
        
        Bundle args = getArguments();
        if (args != null) {
            property = (Property) args.getSerializable("property");
            
            if (property != null) {
                tvName.setText(property.getName());
                tvPrice.setText("$" + String.format("%,d", property.getPrice()));
                tvLocation.setText(property.getLocation());
                tvType.setText(property.getType() != null ? property.getType().toUpperCase() : "N/A");
                
                if (property.getDescription() != null && !property.getDescription().isEmpty()) {
                    tvDescription.setText(property.getDescription());
                }
                
                if (property.getOwnerName() != null && !property.getOwnerName().isEmpty()) {
                    tvAgentName.setText(property.getOwnerName());
                } else {
                    tvAgentName.setText("Agent");
                }

                if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(property.getImageUrl())
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivImage);
                }
            }
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_call).setOnClickListener(v -> {
            if (property != null && property.getOwnerPhone() != null && !property.getOwnerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + property.getOwnerPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn_chat).setOnClickListener(v -> {
            if (property != null && property.getOwnerPhone() != null && !property.getOwnerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + property.getOwnerPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
