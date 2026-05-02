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

public class SignupFragment extends Fragment {

    private FirebaseAuth   mAuth;
    private DatabaseHelper dbHelper;
    private EditText       etName, etEmail, etPassword;
    private ProgressBar    progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth       = FirebaseAuth.getInstance();
        dbHelper    = new DatabaseHelper(getContext());
        etName      = view.findViewById(R.id.et_name);
        etEmail     = view.findViewById(R.id.et_email);
        etPassword  = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progress_bar);
        Button   btnSignup = view.findViewById(R.id.btn_signup);
        TextView tvLogin   = view.findViewById(R.id.tv_login);

        btnSignup.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v ->
                ((AuthActivity) requireActivity()).loadFragment(new LoginFragment())
        );

        return view;
    }

    private void registerUser() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Fill in all fields",
                    Toast.LENGTH_SHORT).show();
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
                    progressBar.setVisibility(View.GONE);

                    // Save user profile to SQLite
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        dbHelper.saveUser(user.getUid(), name, email);
                    }

                    // Go to MainActivity
                    Intent intent = new Intent(getActivity(), HomeFragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Signup failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}