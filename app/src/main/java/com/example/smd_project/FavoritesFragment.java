package com.example.smd_project;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private PropertyAdapter adapter;
    private List<Property> favoriteList;
    private DatabaseHelper dbHelper;
    private LinearLayout layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        dbHelper = new DatabaseHelper(getContext());
        favoriteList = new ArrayList<>();
        layoutEmpty = view.findViewById(R.id.layout_empty_fav);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PropertyAdapter(getContext(), favoriteList, property -> {
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

        loadFavorites();

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        favoriteList.clear();
        Cursor cursor = dbHelper.getFavourites(user.getUid());

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("property_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("property_name"));
                String priceStr = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                
                // Note: The Favorites table doesn't store 'type', 'featured', or 'image' 
                // in the current schema. To keep the UI consistent, we might need dummy data
                // or fetch from Firestore. For now, we use defaults.
                Property p = new Property(
                        id,
                        name,
                        "Apartment", // Default
                        location,
                        Integer.parseInt(priceStr),
                        false, // Default
                        null   // Default
                );
                favoriteList.add(p);
            }
            cursor.close();
        }

        adapter.updateList(new ArrayList<>(favoriteList));
        layoutEmpty.setVisibility(favoriteList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
