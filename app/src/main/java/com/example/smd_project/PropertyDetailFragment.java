package com.example.smd_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PropertyDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);

        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvPrice = view.findViewById(R.id.tv_detail_price);
        TextView tvLocation = view.findViewById(R.id.tv_detail_location);
        TextView tvType = view.findViewById(R.id.tv_detail_type);
        TextView tvFeatured = view.findViewById(R.id.tv_detail_featured);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name");
            String type = args.getString("type");
            String location = args.getString("location");
            int price = args.getInt("price");
            boolean featured = args.getBoolean("featured");

            tvName.setText(name);
            tvPrice.setText("$" + String.format("%,d", price));
            tvLocation.setText(location);




            tvType.setText(type.toUpperCase());
            tvFeatured.setVisibility(featured ? View.VISIBLE : View.GONE);
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
