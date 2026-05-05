package com.example.smd_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * First screen shown to users. Lets them choose to continue as a Buyer or a Seller (admin).
 * The selected role is passed to LoginFragment / SignupFragment so they know which
 * MainActivity variant to launch after authentication.
 */
public class RoleSelectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_role_selection, container, false);

        Button btnBuyer  = view.findViewById(R.id.btn_buyer);
        Button btnSeller = view.findViewById(R.id.btn_seller);

        btnBuyer.setOnClickListener(v -> openAuth(false));
        btnSeller.setOnClickListener(v -> openAuth(true));

        return view;
    }

    private void openAuth(boolean isSeller) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_seller", isSeller);
        loginFragment.setArguments(args);

        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).loadFragment(loginFragment);
        }
    }
}