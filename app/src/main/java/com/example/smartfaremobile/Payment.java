package com.example.smartfaremobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Payment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView sfidResultTextView;
    private TextView ewalletTextView;
    private Button sfidOptionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sfidResultTextView = view.findViewById(R.id.sfid_result);
        ewalletTextView = view.findViewById(R.id.balance);
        sfidOptionButton = view.findViewById(R.id.sfid_option);

        // Set a click listener for the "sfid_option" button
        sfidOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch and display the SFID when the button is pressed
                fetchSFIDFromFirebase();
            }
        });
        // Find and set a click listener for the "nfc_option" button
        Button nfcOptionButton = view.findViewById(R.id.nfc_option);
        nfcOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the NfcPayment activity when the button is clicked
                Intent intent = new Intent(getActivity(), NfcPayment.class);
                startActivity(intent);
            }
        });


        // Automatically fetch and display ewallet balance
        fetchEwalletFromFirebase();

        // Set a click listener for the "back_btn" ImageView to simulate a back button press
        ImageView backBtn = view.findViewById(R.id.back_btn); // Replace with your button ID
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // Simulate a back button press
            }
        });

        return view;
    }

    private void fetchSFIDFromFirebase() {
        // Get the user's UID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Create a reference to the user's document in Firestore
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the SFID field from the document
                            String sfid = documentSnapshot.getString("SFID");
                            sfidResultTextView.setText("SFID: " + sfid);
                        } else {
                            sfidResultTextView.setText("SFID not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sfidResultTextView.setText("Error retrieving SFID");
                    }
                });
    }

    private void fetchEwalletFromFirebase() {
        // Get the user's UID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Create a reference to the user's document in Firestore
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the ewallet field from the document as a numeric value
                            Long ewallet = documentSnapshot.getLong("ewallet");

                            if (ewallet != null) {
                                // Convert Long to String and set it in the TextView
                                ewalletTextView.setText("Balance: " + ewallet.toString());
                            } else {
                                ewalletTextView.setText("Ewallet not found");
                            }
                        } else {
                            ewalletTextView.setText("Ewallet not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ewalletTextView.setText("Error retrieving ewallet balance");
                    }
                });
    }
}
