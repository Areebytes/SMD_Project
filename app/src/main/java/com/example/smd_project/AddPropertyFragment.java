package com.example.smd_project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddPropertyFragment extends Fragment {

    private ImageView  ivPropertyImage;
    private Uri        imageUri;
    private ProgressBar progressBar;

    private EditText etTitle, etPrice, etLocation, etDescription, etCategory;
    private Spinner  spinnerType;
    private EditText etBedrooms, etBathrooms, etLivingRooms, etKitchen,
            etGarage, etGarden, etLivingArea, etYearBuilt;

    private DatabaseReference realtimeDbRef;
    private StorageReference  storageRef;
    private FirebaseFirestore  firestore;
    private DatabaseHelper    dbHelper;

    private String existingPropertyId = null;
    private Button btnSubmit;

    private String currentUserName = "Seller";
    private String currentUserPhone = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (imageUri != null && getContext() != null) {
                        try {
                            getContext().getContentResolver()
                                    .takePersistableUriPermission(imageUri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException ignored) { }
                        Glide.with(this).load(imageUri).into(ivPropertyImage);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        realtimeDbRef = FirebaseDatabase.getInstance().getReference("properties");
        storageRef    = FirebaseStorage.getInstance().getReference("property_images");
        firestore     = FirebaseFirestore.getInstance();
        dbHelper      = new DatabaseHelper(getContext());

        fetchUserDetails();

        ivPropertyImage = view.findViewById(R.id.ivPropertyImage);
        progressBar     = view.findViewById(R.id.progress_bar_add);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        etTitle       = view.findViewById(R.id.etTitle);
        etPrice       = view.findViewById(R.id.etPrice);
        etLocation    = view.findViewById(R.id.etLocation);
        etDescription = view.findViewById(R.id.etDescription);
        etCategory    = view.findViewById(R.id.etCategory);
        spinnerType   = view.findViewById(R.id.spinnerType);
        etBedrooms    = view.findViewById(R.id.etBedrooms);
        etBathrooms   = view.findViewById(R.id.etBathrooms);
        etLivingRooms = view.findViewById(R.id.etLivingRooms);
        etKitchen     = view.findViewById(R.id.etKitchen);
        etGarage      = view.findViewById(R.id.etGarage);
        etGarden      = view.findViewById(R.id.etGarden);
        etLivingArea  = view.findViewById(R.id.etLivingArea);
        etYearBuilt   = view.findViewById(R.id.etYearBuilt);

        // Use add_property_types which doesn't have "All"
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.add_property_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnSubmit.setOnClickListener(v -> uploadData());

        if (getArguments() != null && getArguments().containsKey("property")) {
            SellerProperty property =
                    (SellerProperty) getArguments().getSerializable("property");
            if (property != null) populateFields(property);
        }

        return view;
    }

    private void fetchUserDetails() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Cursor cursor = dbHelper.getUser(uid);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex("name");
            int phoneIdx = cursor.getColumnIndex("phone");
            if (nameIdx != -1) currentUserName = cursor.getString(nameIdx);
            if (phoneIdx != -1) currentUserPhone = cursor.getString(phoneIdx);
            cursor.close();
        } else {
            // Try Firestore if not in local DB
            firestore.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    currentUserName = doc.getString("name");
                    currentUserPhone = doc.getString("phone");
                }
            });
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(intent);
    }

    private void populateFields(SellerProperty p) {
        existingPropertyId = p.getId();
        etTitle.setText(p.getTitle());
        etPrice.setText(String.valueOf(p.getPrice()));
        etLocation.setText(p.getLocation());
        etDescription.setText(p.getDescription());
        etCategory.setText(p.getCategory());
        
        if (p.getType() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerType.getAdapter();
            int pos = adapter.getPosition(p.getType());
            if (pos >= 0) spinnerType.setSelection(pos);
        }

        etBedrooms.setText(String.valueOf(p.getBedrooms()));
        etBathrooms.setText(String.valueOf(p.getBathrooms()));
        etLivingRooms.setText(String.valueOf(p.getLivingRooms()));
        etKitchen.setText(String.valueOf(p.getKitchen()));
        etGarage.setText(String.valueOf(p.getGarage()));
        etGarden.setText(String.valueOf(p.getGarden()));
        etLivingArea.setText(String.valueOf(p.getLivingArea()));
        etYearBuilt.setText(String.valueOf(p.getYearBuilt()));
        btnSubmit.setText("Update Property");

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(this).load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPropertyImage);
        }
    }

    private void uploadData() {
        String title    = etTitle.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String desc     = etDescription.getText().toString().trim();

        if (title.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please fill required fields (Title, Price, Location)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String type = spinnerType.getSelectedItem().toString();

        double price    = Double.parseDouble(priceStr);
        String category = etCategory.getText().toString().trim();
        int    bedrooms    = getInt(etBedrooms);
        int    bathrooms   = getInt(etBathrooms);
        int    livingRooms = getInt(etLivingRooms);
        int    kitchen     = getInt(etKitchen);
        int    garage      = getInt(etGarage);
        int    garden      = getInt(etGarden);
        int    livingArea  = getInt(etLivingArea);
        int    yearBuilt   = getInt(etYearBuilt);

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            String filename = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child(filename);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(snap ->
                            imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    saveProperty(title, price, location, desc, category, type,
                                            bedrooms, bathrooms, livingRooms, kitchen,
                                            garage, garden, livingArea, yearBuilt, uri.toString())))
                    .addOnFailureListener(e -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),
                                "Image upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            saveProperty(title, price, location, desc, category, type,
                    bedrooms, bathrooms, livingRooms, kitchen,
                    garage, garden, livingArea, yearBuilt, "");
        }
    }

    private void saveProperty(String title, double price, String location,
                              String description, String category, String type,
                              int bedrooms, int bathrooms, int livingRooms,
                              int kitchen, int garage, int garden,
                              int livingArea, int yearBuilt, String imageUrl) {

        String propertyId = existingPropertyId != null
                ? existingPropertyId
                : realtimeDbRef.push().getKey();
        if (propertyId == null) return;

        String uid = FirebaseAuth.getInstance().getUid();

        SellerProperty sp = new SellerProperty(propertyId, title, price, location,
                description, category, type, bedrooms, bathrooms, livingRooms,
                kitchen, garage, garden, livingArea, yearBuilt, imageUrl, uid, 
                currentUserName, currentUserPhone);

        realtimeDbRef.child(propertyId).setValue(sp);

        Map<String, Object> firestoreDoc = new HashMap<>();
        firestoreDoc.put("id",          propertyId);
        firestoreDoc.put("name",        title);
        firestoreDoc.put("type",        type);
        firestoreDoc.put("location",    location);
        firestoreDoc.put("price",       (long) price);
        firestoreDoc.put("featured",    false);
        firestoreDoc.put("image",       imageUrl);
        firestoreDoc.put("description", description);
        firestoreDoc.put("ownerId",     uid);
        firestoreDoc.put("ownerName",   currentUserName);
        firestoreDoc.put("ownerPhone",  currentUserPhone);

        firestore.collection("properties")
                .document(propertyId)
                .set(firestoreDoc)
                .addOnSuccessListener(unused -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    String msg = existingPropertyId != null
                            ? "Property updated!" : "Property added!";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    if (existingPropertyId == null) clearForm();
                    else if (getActivity() != null)
                        getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Failed to save property: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private int getInt(EditText et) {
        String s = et.getText().toString().trim();
        if (s.isEmpty()) return 0;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    private void clearForm() {
        for (EditText et : new EditText[]{
                etTitle, etPrice, etLocation, etDescription, etCategory,
                etBedrooms, etBathrooms, etLivingRooms, etKitchen,
                etGarage, etGarden, etLivingArea, etYearBuilt}) {
            et.setText("");
        }
        spinnerType.setSelection(0);
        ivPropertyImage.setImageResource(0);
        ivPropertyImage.setBackgroundColor(0xFFE0E0E0);
        imageUri = null;
    }
}