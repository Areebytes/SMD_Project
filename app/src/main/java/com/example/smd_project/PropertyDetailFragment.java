package com.example.smd_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class PropertyDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);

        ImageView ivImage = view.findViewById(R.id.iv_detail_image);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvPrice = view.findViewById(R.id.tv_detail_price);
        TextView tvLocation = view.findViewById(R.id.tv_detail_location);
        TextView tvType = view.findViewById(R.id.tv_detail_type);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "N/A");
            String type = args.getString("type", "N/A");
            String location = args.getString("location", "N/A");
            int price = args.getInt("price", 0);
            String imageUrl = args.getString("imageUrl");

            tvName.setText(name);
            tvPrice.setText("$" + String.format("%,d", price));
            tvLocation.setText(location);
            tvType.setText(type != null ? type.toUpperCase() : "N/A");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(ivImage);
            }
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Set up dummy actions for contact buttons
        view.findViewById(R.id.btn_call).setOnClickListener(v -> {
            // Logic for calling
        });

        view.findViewById(R.id.btn_chat).setOnClickListener(v -> {
            // Logic for chatting
        });

        return view;
    }
}
