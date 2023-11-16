package com.example.javaapp;

import static java.io.File.createTempFile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SecondActivity extends AppCompatActivity {
    EditText textName, password, repassword, textEmail;
    String pdfPath;
    Button signup;
    DatabaseHelper DB;

    private static final int PICK_PDF_REQUEST = 1;
    private TextView selectedFileNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        textName = (EditText) findViewById(R.id.textName);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        textEmail = (EditText) findViewById(R.id.textEmail);
        signup = (Button) findViewById(R.id.SignuppageBtn);

        DB = new DatabaseHelper(this); signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = textName.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                String email = textEmail.getText().toString();
                String path = pdfPath;

                if(name.equals("")||pass.equals("")||repass.equals(""))
                    Toast.makeText(SecondActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    if(pass.equals(repass)){
                        Boolean checkuser = DB.checkEmail(email);
                        if(checkuser==false){
                            // Signup Activity
                            Boolean insert = DB.insertData(email,pass,name,path);
                            if(insert==true){
                                Toast.makeText(SecondActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SecondActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(SecondActivity.this, "User already exists! please sign in", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SecondActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                } }
        });

        // to Display PDF file name
      selectedFileNameTextView = (TextView)findViewById(R.id.selectedFileNameTextView);

        Button UpResumeBtn = findViewById(R.id.UpResumeBtn);

        UpResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SecondActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                    }
                }
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                    if(!Environment.isExternalStorageManager()){
                        try{
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                            startActivityIfNeeded(intent,101);
                        }catch (Exception exception){
                            Intent intent=new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            startActivityIfNeeded(intent,101);
                        }
                    }
                }
                pickPdfFile();

            }
        });
        textEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the entered text is a valid email
                if (isValidEmail(charSequence.toString())) {
                    // Set the email icon to a check mark or any other desired icon
                    setDrawableRight(textEmail, R.drawable.baseline_mark_email_read_24);
                } else {
                    // Set the email icon to the default email icon or any other desired icon
                    setDrawableRight(textEmail, R.drawable.email);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void pickPdfFile() {
        // method to pick PDF File from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");

        startActivityForResult(
                Intent.createChooser(intent, "Select a PDF file"),
                PICK_PDF_REQUEST
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (data != null) {
                Uri pdfUri = data.getData();
                // The selected PDF file URI is in the 'pdfUri' variable
                // You can use this URI to perform further operations
                pdfPath=getFilePathFromUri(pdfUri);
                Toast.makeText(this, "PDF file selected: " +getFilePathFromUri(pdfUri), Toast.LENGTH_SHORT).show();
                String fileName = pdfUri.getLastPathSegment();
                selectedFileNameTextView.setText(getFileName(pdfUri,getApplicationContext()));

            }
        }
    }

    private String getFileName(Uri uri, Context context) {
        // method to get PDF file name
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
    // Helper method to get the file path from URI
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
            // For devices running KitKat and above
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String[] split = documentId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // Add more conditions if needed for other types of external storage
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId));
                filePath = getDataColumn(this, contentUri, null, null);
            }
            else if (DocumentsContract.isDocumentUri(this, uri)) {
                // If the URI is a document URI, use DocumentFile
                // This is for Android O and above
                try {
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        InputStream inputStream = new FileInputStream(fd);

                        // Create a temporary file
                        File tempFile = createTempFile("temp_pdf", ".pdf", getCacheDir());

                        // Copy the content of the input stream to the temporary file
                        copyStreamToFile(inputStream, tempFile);

                        // Get the absolute path of the temporary file
                        filePath = tempFile.getAbsolutePath();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // For devices running versions lower than KitKat
            filePath = getDataColumn(this, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // For file-based URIs
            filePath = uri.getPath();
        }
        }


        return filePath;
    }
    // Helper method to get the data column for a given URI
    private String getDataColumn(Activity context, Uri uri, String selection, String[] selectionArgs) {
        String column = "_data";
        String[] projection = {column};

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        }

        return null;
    }
    private void setDrawableRight(EditText editText, int drawableResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    private boolean isValidEmail(String email) {
        // Implement your email validation logic here
        // For a simple example, we are checking if the email contains '@'
        return email.contains("@");
    }
    private void copyStreamToFile(InputStream inputStream, File outputFile) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }
    }
}
