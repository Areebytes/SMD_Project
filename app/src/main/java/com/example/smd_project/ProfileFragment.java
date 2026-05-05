package com.example.smd_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseHelper dbHelper;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DatabaseHelper(getContext());
        db = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);

        loadUserData();

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_my_favorites).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new FavoritesFragment());
            }
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        tvEmail.setText(user.getEmail());

        // 1. Try local SQLite
        Cursor cursor = dbHelper.getUser(user.getUid());
        if (cursor != null && cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex("name");
            if (nameIdx != -1) {
                tvName.setText(cursor.getString(nameIdx));
                cursor.close();
                return;
            }
            cursor.close();
        }

        // 2. Try Firestore
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && isAdded()) {
                        String name = doc.getString("name");
                        tvName.setText(name != null ? name : "User");
                        
                        // Sync to local
                        String phone = doc.getString("phone");
                        boolean isSeller = doc.getBoolean("isSeller") != null && doc.getBoolean("isSeller");
                        dbHelper.saveUser(user.getUid(), name, user.getEmail(), phone, isSeller);
                    }
                });
    }
}
