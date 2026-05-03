package com.example.smd_project;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView     recyclerView;
    private PropertyAdapter  adapter;
    private List<Property>   allProperties;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Greeting with Firebase user email
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            tvGreeting.setText("Hello, " + auth.getCurrentUser().getEmail());
        }

        // Load dummy data
        allProperties = getDummyProperties();

        // RecyclerView — 2 column grid
        recyclerView = view.findViewById(R.id.recycler_properties);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(16));

        // Category chip clicks
        view.findViewById(R.id.chip_all).setOnClickListener(v ->
                adapter.updateList(allProperties));
        view.findViewById(R.id.chip_villa).setOnClickListener(v ->
                adapter.updateList(filterByType("Villa")));
        view.findViewById(R.id.chip_apartment).setOnClickListener(v ->
                adapter.updateList(filterByType("Apartment")));
        view.findViewById(R.id.chip_house).setOnClickListener(v ->
                adapter.updateList(filterByType("House")));

        // Search bar — tap opens SearchResultsFragment
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("query", "");
            SearchResultsFragment searchFrag = new SearchResultsFragment();
            searchFrag.setArguments(args);
            ((MainActivity) requireActivity())
                    .loadFragment(searchFrag);

        });

        return view;
    }

    private List<Property> filterByType(String type) {
        List<Property> filtered = new ArrayList<>();
        for (Property p : allProperties) {
            if (p.getType().equalsIgnoreCase(type)) filtered.add(p);
        }
        return filtered;
    }

    private List<Property> getDummyProperties() {
        List<Property> list = new ArrayList<>();
        list.add(new Property("1", "Exclusive House",  "Apartment", "134 Alabaster, AL",  120000, false));
        list.add(new Property("2", "Charming Villa",   "Villa",     "4735 Lafayette, AL", 250500, false));
        list.add(new Property("3", "Blue Star Villa",  "Villa",     "4272 Kent Dairy, AL",165000, true));
        list.add(new Property("4", "Luxury Smart Villa","Villa",    "2734 Lafayette, AL", 275800, true));
        list.add(new Property("5", "Big Central Villa", "Villa",    "4632 Kent Dairy, AL",310000, true));
        list.add(new Property("6", "Modern House",     "House",     "12 Silver St, AL",   198000, false));
        return list;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
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