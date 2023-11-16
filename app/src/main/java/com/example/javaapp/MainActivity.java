package com.example.javaapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
 EditText email,password;
 Button btnlogin;
 DatabaseHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button SignupBtn = findViewById(R.id.SignupBtn);
        SignupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        email = (EditText) findViewById(R.id.textEmail);
        password = (EditText) findViewById(R.id.password);
        btnlogin = (Button) findViewById(R.id.loginbtn);
        DB = new DatabaseHelper(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if(mail.equals("")||pass.equals(""))
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    // check email and password for login
                    Boolean checkuserpass = DB.checkEmailPassword(mail, pass);
                    if(checkuserpass==true){
                        Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        openHomeScreen(mail);
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the entered text is a valid email
                if (isValidEmail(charSequence.toString())) {
                    // Set the email icon to a check mark or any other desired icon
                    setDrawableRight(email, R.drawable.baseline_mark_email_read_24);
                } else {
                    // Set the email icon to the default email icon or any other desired icon
                    setDrawableRight(email, R.drawable.email);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // to open Home Screen with help of user
    private void openHomeScreen(String mail) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("mail", mail);
        startActivity(intent);
        finish();
    }
    private void setDrawableRight(EditText editText, int drawableResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    private boolean isValidEmail(String email) {
        // Implement your email validation logic here
        // For a simple example, we are checking if the email contains '@'
        return email.contains("@");
    }

}