package com.example.smartfaremobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class Ewallet extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView sfidResultTextView;
    private TextView ewalletTextView;

    private Button sfidOptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewallet);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sfidResultTextView = findViewById(R.id.sfid_result);
        ewalletTextView = findViewById(R.id.balance);
        sfidOptionButton = findViewById(R.id.sfid_option); // Replace with your button ID

        // Automatically fetch and display ewallet balance
        fetchEwalletFromFirebase();

        // Set a click listener for the "sfid_option" button
        sfidOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch and display the SFID when the button is pressed
                fetchSFIDFromFirebase();
            }
        });
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
                            Double ewallet = documentSnapshot.getDouble("ewallet");
                            if (ewallet != null) {
                                ewalletTextView.setText("Balance: " + ewallet);
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
