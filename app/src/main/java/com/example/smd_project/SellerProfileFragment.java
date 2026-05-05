package com.example.smd_project;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private DatabaseHelper dbHelper;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);

        tvName  = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvRole  = view.findViewById(R.id.tv_profile_role);
        Button   btnLogout = view.findViewById(R.id.btn_logout);

        dbHelper = new DatabaseHelper(getContext());
        db = FirebaseFirestore.getInstance();

        loadUserData();
        tvRole.setText("Role: Seller / Admin");

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof SellerMainActivity) {
                ((SellerMainActivity) getActivity()).logout();
            }
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        tvEmail.setText(user.getEmail());

        Cursor cursor = dbHelper.getUser(user.getUid());
        if (cursor != null && cursor.moveToFirst()) {
            int nameCol = cursor.getColumnIndex("name");
            if (nameCol >= 0) tvName.setText(cursor.getString(nameCol));
            cursor.close();
        }

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && isAdded()) {
                        String name = doc.getString("name");
                        if (name != null) tvName.setText(name);
                        
                        String phone = doc.getString("phone");
                        dbHelper.saveUser(user.getUid(), name, user.getEmail(), phone, true);
                    }
                });
    }
}