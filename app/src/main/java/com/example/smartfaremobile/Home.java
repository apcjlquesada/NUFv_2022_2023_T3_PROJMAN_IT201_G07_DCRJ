package com.example.smartfaremobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth; // Import Firebase Authentication
import android.util.Log;
import android.widget.ImageView;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Home extends Fragment {

    private FirebaseFirestore db;
    private TextView remainingBalanceTextView;
    private ImageView topupButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the Firestore instance
        db = FirebaseFirestore.getInstance();

        remainingBalanceTextView = view.findViewById(R.id.remaining_balance);
        topupButton = view.findViewById(R.id.topup_btn); // Reference to topup_btn

        // Set an OnClickListener for the topupButton
        topupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the top-up dialog
                showTopUpDialog();
            }
        });

        // Get the current user's ID after they are authenticated
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Use the user's ID to fetch their Firestore document
        db.collection("Users")
                .document(userID) // Replace with the actual user ID
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the "ewallet" value from the document as an Integer
                                Integer ewalletValue = document.getLong("ewallet").intValue();
                                remainingBalanceTextView.setText(String.valueOf(ewalletValue));
                            } else {
                                // Handle the case where the document does not exist
                                Log.d("Firestore", "Document does not exist.");
                            }
                        } else {
                            // Handle exceptions or errors
                            Log.e("Firestore", "Error fetching document: " + task.getException());
                        }
                    }
                });

        return view;
    }

    private void showTopUpDialog() {
        // Create a custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.diaglog_topup);
        dialog.setTitle("Top-Up");

        EditText topupAmountEditText = dialog.findViewById(R.id.topup_amount);
        Button confirmButton = dialog.findViewById(R.id.confirm_topup);
        Button cancelButton = dialog.findViewById(R.id.cancel_topup);

        // Set an OnClickListener for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the top-up amount entered by the user
                String topupAmountText = topupAmountEditText.getText().toString();

                if (!topupAmountText.isEmpty()) {
                    // Parse the top-up amount to an integer
                    int topupAmount = Integer.parseInt(topupAmountText);

                    // Fetch the user's current eWallet balance from Firestore
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    db.collection("Users")
                            .document(userID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            // Get the current eWallet balance
                                            Integer currentBalance = document.getLong("ewallet").intValue();

                                            // Calculate the new balance after top-up
                                            int newBalance = currentBalance + topupAmount;

                                            // Update the eWallet value in Firestore
                                            db.collection("Users")
                                                    .document(userID)
                                                    .update("ewallet", newBalance)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Update the balance TextView with the new balance
                                                                remainingBalanceTextView.setText(String.valueOf(newBalance));
                                                                // Close the dialog
                                                                dialog.dismiss();
                                                                Toast.makeText(getActivity(), "Top-up successful!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Log.e("Firestore", "Error updating eWallet: " + task.getException());
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Log.d("Firestore", "Document does not exist.");
                                        }
                                    } else {
                                        Log.e("Firestore", "Error fetching document: " + task.getException());
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid top-up amount.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set an OnClickListener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog without performing any action
            }
        });

        // Show the dialog
        dialog.show();
    }
}
