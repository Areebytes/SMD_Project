package com.example.smd_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private FirebaseAuth   mAuth;
    private FirebaseFirestore db;
    private DatabaseHelper dbHelper;
    private EditText       etName, etEmail, etPassword, etPhone;
    private ProgressBar    progressBar;
    private boolean        isSeller = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        if (getArguments() != null) {
            isSeller = getArguments().getBoolean("is_seller", false);
        }

        mAuth       = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();
        dbHelper    = new DatabaseHelper(getContext());
        etName      = view.findViewById(R.id.et_name);
        etEmail     = view.findViewById(R.id.et_email);
        etPhone     = view.findViewById(R.id.et_phone);
        etPassword  = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progress_bar);
        Button   btnSignup = view.findViewById(R.id.btn_signup);
        TextView tvLogin   = view.findViewById(R.id.tv_login);

        TextView tvSubtitle = view.findViewById(R.id.tv_signup_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(isSeller ? "Register as Seller / Admin" : "Start your journey with us");
        }

        btnSignup.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            LoginFragment loginFragment = new LoginFragment();
            Bundle args = new Bundle();
            args.putBoolean("is_seller", isSeller);
            loginFragment.setArguments(args);
            ((AuthActivity) requireActivity()).loadFragment(loginFragment);
        });

        return view;
    }

    private void registerUser() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("uid", uid);
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("phone", phone);
                        userMap.put("isSeller", isSeller);

                        db.collection("users").document(uid).set(userMap)
                                .addOnCompleteListener(task -> {
                                    dbHelper.saveUser(uid, name, email, phone, isSeller);
                                    progressBar.setVisibility(View.GONE);
                                    launchMainActivity();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Signup failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void launchMainActivity() {
        Intent intent;
        if (isSeller) {
            intent = new Intent(getActivity(), SellerMainActivity.class);
        } else {
            intent = new Intent(getActivity(), MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}