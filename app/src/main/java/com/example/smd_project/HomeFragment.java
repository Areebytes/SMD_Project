package com.example.smd_project;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView     recyclerView;
    private PropertyAdapter  adapter;
    private List<Property>   allProperties;
    private FirebaseFirestore db;
    private ProgressBar      progressBar;
    private DatabaseHelper   dbHelper;
    private ListenerRegistration registration;
    private TextView tvGreeting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tv_greeting);
        progressBar = view.findViewById(R.id.progress_bar);
        dbHelper = new DatabaseHelper(getContext());
        db = FirebaseFirestore.getInstance();
        
        updateGreeting();

        allProperties = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_properties);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        adapter = new PropertyAdapter(getContext(), allProperties, property -> {
            if (property == null) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", property);

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

        startPropertyListener();

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

        view.findViewById(R.id.btn_profile).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            }
        });

        return view;
    }

    private void updateGreeting() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        Cursor cursor = dbHelper.getUser(currentUser.getUid());
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                String name = cursor.getString(nameIndex);
                tvGreeting.setText("Hello, " + name);
                cursor.close();
                return;
            }
            cursor.close();
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && isAdded()) {
                        String name = doc.getString("name");
                        if (name != null) {
                            tvGreeting.setText("Hello, " + name);
                            String phone = doc.getString("phone");
                            boolean isSeller = doc.getBoolean("isSeller") != null && doc.getBoolean("isSeller");
                            dbHelper.saveUser(currentUser.getUid(), name, currentUser.getEmail(), phone, isSeller);
                        }
                    }
                });

        String email = currentUser.getEmail();
        tvGreeting.setText("Hello, " + (email != null ? email.split("@")[0] : "User"));
    }

    private void startPropertyListener() {
        progressBar.setVisibility(View.VISIBLE);
        registration = db.collection("properties")
                .addSnapshotListener((value, error) -> {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        if (error != null) {
                            Log.e("HomeFragment", "Firestore Listen failed", error);
                            return;
                        }

                        if (value != null) {
                            allProperties.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                String id = doc.getString("id");
                                if (id == null) id = doc.getId();

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
                                        id,
                                        doc.getString("name"),
                                        doc.getString("type"),
                                        doc.getString("location"),
                                        price,
                                        featured,
                                        doc.getString("image"),
                                        doc.getString("description"),
                                        doc.getString("ownerName"),
                                        doc.getString("ownerPhone")
                                );
                                allProperties.add(property);
                            }
                            adapter.updateList(new ArrayList<>(allProperties));
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registration != null) registration.remove();
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
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.top = spacing;
            outRect.bottom = spacing;
        }
    }
}