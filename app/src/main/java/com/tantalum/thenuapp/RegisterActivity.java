package com.tantalum.thenuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private ProgressDialog progressDialog;

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConPassword;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null) {
            finish();
            return;
        }

        mStore = FirebaseFirestore.getInstance();

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        Button textViewSwitchToLogin = findViewById(R.id.tvSwitchToLogin);
        textViewSwitchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin();

            }
        });

        //loading dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
    }

    private void validate() {
        TextInputLayout fullName = findViewById(R.id.fullName);
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout phone = findViewById(R.id.phone);
        TextInputLayout password = findViewById(R.id.password);
        TextInputLayout conPassword = findViewById(R.id.conPassword);

        etFullName = fullName.getEditText();
        etEmail = email.getEditText();
        etPhone = phone.getEditText();
        etPassword = password.getEditText();
        etConPassword = conPassword.getEditText();

        //check if user fill all the fields before sending data to firebase
        if (isValid(fullName, email, phone, password, conPassword)) {
            if (etPhone.getText().toString().trim().length() < 9) {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                return;
            }

            String emailText = etEmail.getText().toString().trim();
            if (!emailText.contains("@") && !emailText.contains(".")) {
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                return;
            }

            if (!etPassword.getText().toString().trim().equals(etConPassword.getText().toString().trim())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            authenticate();
        }

    }

    private void authenticate() {
        progressDialog.show();

        String fullNameTxt = etFullName.getText().toString();
        String emailTxt = etEmail.getText().toString();
        String phoneTxt = etPhone.getText().toString();
        String passwordTxt = etPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = mStore.collection("users").document(userID);
                        User user = new User();
                        user.setFullName(fullNameTxt);
                        user.setEmail(emailTxt);
                        user.setPhone(phoneTxt);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                Toast.makeText(RegisterActivity.this, "User Created.",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed: "+ e.getMessage());
                            }
                        });
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Authentication Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
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
    private void switchToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void setmStore(FirebaseFirestore mStore) {
        this.mStore = mStore;
    }
}