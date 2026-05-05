package com.example.smd_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private OnFilterApplyListener listener;

    public interface OnFilterApplyListener {
        void onApply(String type, int minPrice, int maxPrice);
    }

    public void setListener(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        EditText etMin = view.findViewById(R.id.et_min_price);
        EditText etMax = view.findViewById(R.id.et_max_price);
        Button btnApply = view.findViewById(R.id.btn_apply);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "Villa", "Apartment", "House"}
        );
        spinnerType.setAdapter(adapter);

        btnApply.setOnClickListener(v -> {
            String type = spinnerType.getSelectedItem().toString();

            int min = etMin.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(etMin.getText().toString());

            int max = etMax.getText().toString().isEmpty() ? Integer.MAX_VALUE :
                    Integer.parseInt(etMax.getText().toString());

            if (listener != null) {
                listener.onApply(type, min, max);
            }

            dismiss();
        });

        return view;
    }
}
