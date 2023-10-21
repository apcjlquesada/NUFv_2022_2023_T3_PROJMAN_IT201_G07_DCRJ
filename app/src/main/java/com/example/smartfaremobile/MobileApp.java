package com.example.smartfaremobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MobileApp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_app); // Replace with the correct layout file name

        Button cat1Button = findViewById(R.id.cat1);

        cat1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open Cat1Activity
                Intent intent = new Intent(MobileApp.this, Ewallet.class);
                startActivity(intent);
            }
        });
    }
}