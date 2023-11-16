package com.example.javaapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, pdfLocationTextView;
    PDFView pdfView;
    String pdf;
    File file;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nameTextView = findViewById(R.id.textViewName);
        emailTextView = findViewById(R.id.textViewEmail);
        pdfLocationTextView = findViewById(R.id.textViewPdfLocation);
        pdfView = findViewById(R.id.pdfView);
        databaseHelper = new DatabaseHelper(this);

        // Get email from intent
        String mail = getIntent().getStringExtra("mail");

        // Use your database helper to get user data
        User user = databaseHelper.getUserByEmail(mail);

        if (user != null) {
            // Display user data on the home screen
            nameTextView.setText("Name: " + user.getName());
            emailTextView.setText("Email: " + user.getEmail());
            pdfLocationTextView.setText("PDF Location: " + user.getPdfLocation());
            pdf = user.getPdfLocation();

        }
        if (pdf!="") {
            // Display PDF on the home screen
            Toast.makeText(HomeActivity.this, "PDF"+ pdf, Toast.LENGTH_SHORT).show();
            file = new File(pdf);
            pdfView.fromFile(file).load();
        }
    }
}
