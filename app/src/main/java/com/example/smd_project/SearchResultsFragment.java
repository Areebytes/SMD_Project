package com.example.smd_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchResultsFragment extends Fragment {

    private PropertyAdapter adapter;
    private List<Property>  allProperties;
    private TextView        tvResultCount;
    private LinearLayout    layoutEmpty;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        allProperties = new ArrayList<>();
        tvResultCount = view.findViewById(R.id.tv_result_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        RecyclerView recycler = view.findViewById(R.id.recycler_results);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

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
        
        recycler.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        db.collection("properties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;
                    
                    allProperties.clear();
                    for (var doc : queryDocumentSnapshots) {
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
                                doc.getString("image")
                        );
                        allProperties.add(property);
                    }

                    adapter.updateList(new ArrayList<>(allProperties));
                    updateCount(allProperties.size());
                });

        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                filterSearch(s.toString().trim());
            }
        });

        // Filter Bottom Sheet Integration
        view.findViewById(R.id.btn_filter).setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();
            filterSheet.setListener((type, min, max) -> {
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
                updateCount(filtered.size());
            });
            filterSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void filterSearch(String query) {
        if (allProperties == null) return;
        List<Property> filtered = new ArrayList<>();
        for (Property p : allProperties) {
            boolean matchesName = p.getName() != null && p.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesType = p.getType() != null && p.getType().toLowerCase().contains(query.toLowerCase());
            if (matchesName || matchesType) {
                filtered.add(p);
            }
        }
        if (adapter != null) {
            adapter.updateList(filtered);
            updateCount(filtered.size());
        }
    }

    private void updateCount(int count) {
        if (tvResultCount != null) {
            tvResultCount.setText(count + " Ads Found");
        }
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }
    }
}
