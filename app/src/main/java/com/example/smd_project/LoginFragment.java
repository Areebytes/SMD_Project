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

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseHelper dbHelper;
    private EditText     etEmail, etPassword;
    private ProgressBar  progressBar;
    private boolean      isSeller = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        if (getArguments() != null) {
            isSeller = getArguments().getBoolean("is_seller", false);
        }

        mAuth       = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();
        dbHelper    = new DatabaseHelper(getContext());
        etEmail     = view.findViewById(R.id.et_email);
        etPassword  = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progress_bar);
        Button   btnLogin = view.findViewById(R.id.btn_login);
        TextView tvSignup = view.findViewById(R.id.tv_signup);
        TextView tvBack   = view.findViewById(R.id.tv_back_role);

        TextView tvSubtitle = view.findViewById(R.id.tv_login_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(isSeller ? "Login as Seller / Admin" : "Login as Buyer");
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v -> {
            SignupFragment signupFragment = new SignupFragment();
            Bundle args = new Bundle();
            args.putBoolean("is_seller", isSeller);
            signupFragment.setArguments(args);
            ((AuthActivity) requireActivity()).loadFragment(signupFragment);
        });

        if (tvBack != null) {
            tvBack.setOnClickListener(v ->
                    ((AuthActivity) requireActivity()).loadFragment(new RoleSelectionFragment()));
        }

        return view;
    }

    private void loginUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        db.collection("users").document(user.getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String name = documentSnapshot.getString("name");
                                        boolean seller = documentSnapshot.getBoolean("isSeller") != null && 
                                                        documentSnapshot.getBoolean("isSeller");
                                        dbHelper.saveUser(user.getUid(), name, email, seller);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    launchMainActivity();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    launchMainActivity();
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        launchMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Login failed: " + e.getMessage(),
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