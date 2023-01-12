package com.tantalum.thenuapp;

import static com.tantalum.thenuapp.FirebasePaths.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private EditText etLoginEmail;
    private EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUser();
            }
        });

        Button registerNowBtn = findViewById(R.id.registerNowBtn);
        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });

        //loading dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
    }

    private void validateUser() {
        TextInputLayout loginEmail = findViewById(R.id.loginEmail);
        TextInputLayout loginPassword = findViewById(R.id.loginPassword);

        etLoginEmail = loginEmail.getEditText();
        etLoginPassword = loginPassword.getEditText();

        //check if user fill all the fields before sending data to firebase
        if (isValid(loginEmail, loginPassword)){
        }
            authenticateUser();
    }

    private void authenticateUser() {
        progressDialog.show();

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMainActivity();

                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private boolean isValid(TextInputLayout... textInputLayouts) {
        for (TextInputLayout til : textInputLayouts) {
            String text = til.getEditText().getText().toString().trim();
            if (text.isEmpty()) {
                til.setError("Required");
                return false;
            }
        }
        return true;
    }
    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void switchToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}