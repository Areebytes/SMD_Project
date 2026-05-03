package com.example.smd_project;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private RecyclerView     recyclerView;
    private PropertyAdapter  adapter;
    private List<Property>   allProperties;
    private FirebaseFirestore db;
    private ProgressBar      progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        progressBar = view.findViewById(R.id.progress_bar);
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            tvGreeting.setText("Hello, " + (email != null ? email : "User"));
        }

        db = FirebaseFirestore.getInstance();
        allProperties = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_properties);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        adapter = new PropertyAdapter(getContext(), allProperties, property -> {
            if (property == null) return;
            Bundle bundle = new Bundle();
            bundle.putString("id", property.getId());
            bundle.putString("name", property.getName());
            bundle.putString("type", property.getType());
            bundle.putString("location", property.getLocation());
            bundle.putInt("price", property.getPrice());
            bundle.putBoolean("featured", property.isFeatured());
            bundle.putString("imageUrl", property.getImageUrl());

            PropertyDetailFragment detailFragment = new PropertyDetailFragment();
            detailFragment.setArguments(bundle);

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(detailFragment);
            }
        });
        
        recyclerView.setAdapter(adapter);
        
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(16));
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("properties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        allProperties.clear();
                        for (var doc : queryDocumentSnapshots) {
                            Object featuredObj = doc.get("featured");
                            boolean featured = false;
                            if (featuredObj instanceof Boolean) {
                                featured = (Boolean) featuredObj;
                            } else if (featuredObj instanceof String) {
                                featured = Boolean.parseBoolean((String) featuredObj);
                            }

                            Long priceLong = doc.getLong("price");
                            int price = (priceLong != null) ? priceLong.intValue() : 0;

                            Property property = new Property(
                                    doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("type"),
                                    doc.getString("location"),
                                    price,
                                    featured,
                                    doc.getString("image")
                            );
                            allProperties.add(property);
                        }
                        adapter.updateList(new ArrayList<>(allProperties));
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) progressBar.setVisibility(View.GONE);
                });

        view.findViewById(R.id.chip_all).setOnClickListener(v ->
                adapter.updateList(new ArrayList<>(allProperties)));
        
        view.findViewById(R.id.chip_villa).setOnClickListener(v ->
                adapter.updateList(filterByType("Villa")));
        
        view.findViewById(R.id.chip_apartment).setOnClickListener(v ->
                adapter.updateList(filterByType("Apartment")));
        
        view.findViewById(R.id.chip_house).setOnClickListener(v ->
                adapter.updateList(filterByType("House")));

        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new SearchResultsFragment());
            }
        });

        view.findViewById(R.id.btn_filter).setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();
            filterSheet.setListener((type, min, max) -> {
                if (type.equalsIgnoreCase("All") && min == 0 && max == Integer.MAX_VALUE) {
                    adapter.updateList(new ArrayList<>(allProperties));
                    return;
                }

                List<Property> filtered = new ArrayList<>();
                for (Property p : allProperties) {
                    boolean matchesType = type.equalsIgnoreCase("All") || 
                                          (p.getType() != null && p.getType().equalsIgnoreCase(type));
                    boolean matchesPrice = p.getPrice() >= min && p.getPrice() <= max;
                    
                    if (matchesType && matchesPrice) {
                        filtered.add(p);
                    }
                }
                adapter.updateList(filtered);
            });
            filterSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });

        // 🔧 UI Polish: Navigation to Profile
        view.findViewById(R.id.btn_profile).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        return view;
    }

    private List<Property> filterByType(String type) {
        List<Property> filtered = new ArrayList<>();
        for (Property p : allProperties) {
            if (p.getType() != null && p.getType().equalsIgnoreCase(type)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public GridSpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.top = spacing;
            outRect.bottom = spacing;
        }
    }
}
