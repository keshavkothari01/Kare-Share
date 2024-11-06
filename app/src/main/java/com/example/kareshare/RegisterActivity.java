package com.example.kareshare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etGSTNumber, etEmail, etPassword, etConfirmPassword, etPhoneNumber, etAlternatePhoneNumber, etLocation;
    private RadioGroup radioGroupUserType;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etGSTNumber = findViewById(R.id.etGSTNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAlternatePhoneNumber = findViewById(R.id.etAlternatePhoneNumber);
        etLocation = findViewById(R.id.etLocation);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String gstNumber = etGSTNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String alternatePhoneNumber = etAlternatePhoneNumber.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        int selectedId = radioGroupUserType.getCheckedRadioButtonId();
        final String userType = selectedId == R.id.radioNgo ? "NGO" : "Restaurant";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(location) || TextUtils.isEmpty(userType) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(gstNumber) && userType.equals("Restaurant")) {
            Toast.makeText(this, "GST Number is required for restaurants", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, name, gstNumber, email, phoneNumber, alternatePhoneNumber, location, userType);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String gstNumber, String email, String phoneNumber, String alternatePhoneNumber, String location, String userType) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("gstNumber", gstNumber);
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);
        user.put("alternatePhoneNumber", alternatePhoneNumber);
        user.put("location", location);
        user.put("userType", userType);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
