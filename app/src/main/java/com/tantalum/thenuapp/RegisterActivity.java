package com.tantalum.thenuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private ProgressDialog progressDialog;

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
                registerUser();
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

    private void registerUser() {
        progressDialog.show();

        TextInputLayout fullName = findViewById(R.id.fullName);
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout phone = findViewById(R.id.phone);
        TextInputLayout password = findViewById(R.id.password);
        TextInputLayout conPassword = findViewById(R.id.conPassword);

        EditText etFullName = fullName.getEditText();
        EditText etEmail = email.getEditText();
        EditText etPhone = phone.getEditText();
        EditText etPassword = password.getEditText();
        EditText etConPassword = conPassword.getEditText();

        String fullNameTxt = fullName.getEditText().getText().toString();
        String emailTxt = email.getEditText().getText().toString();
        String phoneTxt = phone.getEditText().getText().toString();
        String passwordTxt = password.getEditText().getText().toString();
        String conPasswordTxt = conPassword.getEditText().getText().toString();

        //check if user fill all the fields before sending data to firebase
        if (fullNameTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty() || passwordTxt.isEmpty()){
            Toast.makeText(this, "Please fill all the Fields.", Toast.LENGTH_LONG).show();
            return;
        }
        if (phoneTxt.length() < 9) {
            Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
        }

        /*if (emailTxt = "[a-zA-Z0-9._-]+@[a-z]+\.+[a-z]+") {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
        }*/


        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "User Created.",Toast.LENGTH_SHORT).show();
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = mStore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fullName",fullNameTxt);
                        user.put("email",emailTxt);
                        user.put("phone",phoneTxt);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "onSuccess: user Profile is created for "+ userID);
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