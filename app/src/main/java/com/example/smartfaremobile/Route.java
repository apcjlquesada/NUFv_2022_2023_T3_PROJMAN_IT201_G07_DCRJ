package com.example.smartfaremobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Route extends Fragment {

    RadioGroup radioGroup;
    TextView textView;

    Button btn_payment;
    Button btn_proceed;

    // Store the selected fare value
    String selectedFare;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        radioGroup = view.findViewById(R.id.radioGroup);
        textView = view.findViewById(R.id.fare);

        btn_payment = view.findViewById(R.id.btn_payment);
        btn_proceed = view.findViewById(R.id.btn_proceed);

        // Firebase initialization
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Disable btn_proceed by default
        btn_proceed.setEnabled(false);

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton radioButton = view.findViewById(selectedId);
                    selectedFare = radioButton.getTag().toString();
                    textView.setText("FARE PRICE: ₱" + selectedFare);

                    // Update the "LatestRoute" with the selectedFare immediately
                    storeLatestRoute(selectedFare);

                    // Enable btn_proceed when btn_payment is clicked
                    btn_proceed.setEnabled(true);
                } else {
                    textView.setText("No fare selected.");
                }
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFare != null) {
                    // Replace or add the Payment fragment to the container
                    Payment paymentFragment = new Payment();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.payment_container, paymentFragment);
                    transaction.addToBackStack(null); // Optional, to add the fragment to the back stack
                    transaction.commit();
                } else {
                    textView.setText("Select a fare first.");
                }
            }
        });



        return view;
    }

    private void storeLatestRoute(String selectedFare) {
        // Get the user's UID
        String userId = firebaseAuth.getCurrentUser().getUid();
        Log.d("DEBUG", "User ID: " + userId); // Add this line for debugging

        // Create a reference to the user's document in Firestore
        DocumentReference userRef = db.collection("Users").document(userId);

        // Update the "LatestRoute" field with the selected fare
        userRef.update("LatestRoute", selectedFare)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEBUG", "LatestRoute updated successfully"); // Add this line for debugging
                        // Successfully updated the "LatestRoute" field
                        // You can add further handling if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEBUG", "Failed to update LatestRoute: " + e.getMessage()); // Add this line for debugging
                        // Handle the failure to update the "LatestRoute" field
                        // You can add error handling as needed
                    }
                });
    }

    }


