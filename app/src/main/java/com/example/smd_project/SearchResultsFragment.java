package com.example.smd_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {

    private PropertyAdapter adapter;
    private List<Property>  allProperties;
    private TextView        tvResultCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        allProperties = getDummyProperties();
        tvResultCount = view.findViewById(R.id.tv_result_count);

        RecyclerView recycler = view.findViewById(R.id.recycler_results);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new PropertyAdapter(allProperties, property -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", property.getId());
            bundle.putString("name", property.getName());
            bundle.putString("type", property.getType());
            bundle.putString("location", property.getLocation());
            bundle.putInt("price", property.getPrice());
            bundle.putBoolean("featured", property.isFeatured());

            PropertyDetailFragment detailFragment = new PropertyDetailFragment();
            detailFragment.setArguments(bundle);

            ((MainActivity) requireActivity()).loadFragment(detailFragment);
        });
        
        recycler.setAdapter(adapter);
        updateCount(allProperties.size());

        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                filterSearch(s.toString().trim());
            }
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void filterSearch(String query) {
        List<Property> filtered = new ArrayList<>();
        for (Property p : allProperties) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())
                    || p.getType().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
        updateCount(filtered.size());
    }

    private void updateCount(int count) {
        tvResultCount.setText(count + " Ads Found");
    }

    private List<Property> getDummyProperties() {
        List<Property> list = new ArrayList<>();
        list.add(new Property("1", "Exclusive House",   "Apartment", "134 Alabaster, AL",  120000, false));
        list.add(new Property("2", "Charming Villa",    "Villa",     "4735 Lafayette, AL", 250500, false));
        list.add(new Property("3", "Blue Star Villa",   "Villa",     "4272 Kent Dairy, AL", 165000, true));
        list.add(new Property("4", "Luxury Smart Villa", "Villa",    "2734 Lafayette, AL", 275800, true));
        list.add(new Property("5", "Big Central Villa",  "Villa",    "4632 Kent Dairy, AL", 310000, true));
        list.add(new Property("6", "Modern House",      "House",     "12 Silver St, AL",   198000, false));
        return list;
    }
}
