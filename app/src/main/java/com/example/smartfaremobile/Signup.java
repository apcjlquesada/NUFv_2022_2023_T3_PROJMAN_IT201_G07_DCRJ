package com.example.smartfaremobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signup_Email;
    private EditText signup_Password;
    private EditText admin_password;
    private EditText signup_PhoneNumber;
    private Button signup_btn;
    private TextView loginRedirectText;
    private Button attachFileButton;
    private boolean valid = true;
    private FirebaseFirestore fstore;
    private Uri selectedImageUri; // To store the URI of the selected image
    private static final int FILE_PICKER_REQUEST = 1;
    private StorageReference storageRef; // Initialize the StorageReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference(); // Initialize storageRef

        selectedImageUri = null;
        attachFileButton = findViewById(R.id.image_discount);
        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        signup_Email = findViewById(R.id.signup_email);
        signup_Password = findViewById(R.id.signup_password);
        signup_btn = findViewById(R.id.signup_btn);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signup_PhoneNumber = findViewById(R.id.phone_number);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signup_Email.getText().toString().trim();
                String pass = signup_Password.getText().toString().trim();
                String num = signup_PhoneNumber.getText().toString().trim();

                if (email.isEmpty()) {
                    signup_Email.setError("Email cannot be empty");
                    valid = false;
                } else if (pass.isEmpty()) {
                    signup_Password.setError("Type your password!");
                    valid = false;
                } else if (num.isEmpty()) {
                    signup_PhoneNumber.setError("Phone number cannot be empty");
                    valid = false;
                } else if (num.length() != 11) {
                    signup_PhoneNumber.setError("Phone number must be 11 digits");
                    valid = false;
                } else if (!isNumeric(num)) {
                    signup_PhoneNumber.setError("Phone number must contain only numbers");
                    valid = false;
                }

                if (valid) {
                    // Generate a 7-character random SFID
                    String sfid = generateRandomSFID(7);

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                DocumentReference df = fstore.collection("Users").document(user.getUid());
                                Map<String, Object> userinfo = new HashMap<>();
                                userinfo.put("Email", email);
                                userinfo.put("Password", pass);
                                userinfo.put("Phone Number", num);
                                userinfo.put("SFID", sfid);

                                // Add the "LatestRoute" field with an initial value of 0
                                userinfo.put("LatestRoute", 0);
                                userinfo.put("ewallet", 0);

                                df.set(userinfo);
                                startActivity(new Intent(Signup.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Signup.this, "Sign Up Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        attachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open an image picker to select an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, FILE_PICKER_REQUEST);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, MainActivity.class));
            }
        });
    }

    // Override onActivityResult to handle the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image
            selectedImageUri = data.getData();
            // Upload the image to Firebase Storage
            uploadImageToStorage(selectedImageUri);
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        if (imageUri != null) {
            // Generate a unique filename for the uploaded image (e.g., using a timestamp)
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = "images/" + timestamp + ".jpg"; // Replace "jpg" with the actual file extension.

            // Get a reference to the Firebase Storage location where the image will be uploaded
            StorageReference imageRef = storageRef.child(filename);

            // Upload the image to Firebase Storage
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload is successful
                    // You can get the download URL of the uploaded image if needed
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String imageUrl = downloadUri.toString();
                            // Now you can save this imageUrl to your database or use it as needed.
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occur during the upload
                    Toast.makeText(Signup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where no image is selected
            Toast.makeText(Signup.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    // Generate a random SFID of a specified length
    private String generateRandomSFID(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sfid = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sfid.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sfid.toString();
    }

    // Check if a string contains only numeric digits
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
}
