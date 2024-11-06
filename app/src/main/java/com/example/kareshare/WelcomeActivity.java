package com.example.kareshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            if (mAuth.getCurrentUser() != null) {
                redirectToDashboard();
                return;
            }

            Button btnLogin = findViewById(R.id.btnLogin);
            TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

            btnLogin.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));
            tvCreateAccount.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class)));

        } catch (Exception e) {
            Log.e(TAG, "Error initializing WelcomeActivity", e);
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToDashboard() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String userType = task.getResult().getString("userType");

                        if (userType != null) {
                            Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "User type not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
