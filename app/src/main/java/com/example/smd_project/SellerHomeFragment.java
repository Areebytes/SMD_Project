package com.example.smd_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class SellerHomeFragment extends Fragment
        implements SellerPropertyAdapter.OnPropertyActionListener {

    private RecyclerView          recyclerView;
    private SellerPropertyAdapter adapter;
    private List<SellerProperty>  propertyList;
    private DatabaseReference     dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_seller_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        propertyList = new ArrayList<>();
        adapter      = new SellerPropertyAdapter(getContext(), propertyList, this);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("properties");
        loadProperties();

        return view;
    }

    private void loadProperties() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        dbRef.orderByChild("ownerId").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                propertyList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SellerProperty p = ds.getValue(SellerProperty.class);
                    if (p != null) propertyList.add(p);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load properties", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEditClick(SellerProperty property) {
        AddPropertyFragment fragment = new AddPropertyFragment();
        Bundle args = new Bundle();
        args.putSerializable("property", property);
        fragment.setArguments(args);
        if (getActivity() instanceof SellerMainActivity) {
            ((SellerMainActivity) getActivity()).loadFragment(fragment);
        }
    }

    @Override
    public void onDeleteClick(SellerProperty property) {
        if (property.getId() != null) {
            dbRef.child(property.getId()).removeValue()
                    .addOnSuccessListener(v ->
                            Toast.makeText(getContext(), "Property deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show());
            
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("properties").document(property.getId()).delete();
        }
    }
}