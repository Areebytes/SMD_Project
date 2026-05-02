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

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText     etEmail, etPassword;
    private ProgressBar  progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth       = FirebaseAuth.getInstance();
        etEmail     = view.findViewById(R.id.et_email);
        etPassword  = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progress_bar);
        Button   btnLogin  = view.findViewById(R.id.btn_login);
        TextView tvSignup  = view.findViewById(R.id.tv_signup);

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v ->
                ((AuthActivity) requireActivity()).loadFragment(new SignupFragment())
        );

        return view;
    }

    private void loginUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    progressBar.setVisibility(View.GONE);
                    // Go to MainActivity, clear back stack
                    Intent intent = new Intent(getActivity(), HomeFragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}