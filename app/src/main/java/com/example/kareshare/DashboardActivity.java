package com.example.kareshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DashboardActivity extends AppCompatActivity {

    private View ngoSection, restaurantSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ngoSection = findViewById(R.id.ngoSection);
        restaurantSection = findViewById(R.id.restaurantSection);

        String userType = getIntent().getStringExtra("userType");

        if ("Restaurant".equalsIgnoreCase(userType)) {
            restaurantSection.setVisibility(View.VISIBLE);
            ngoSection.setVisibility(View.GONE);
        } else if ("NGO".equalsIgnoreCase(userType)) {
            ngoSection.setVisibility(View.VISIBLE);
            restaurantSection.setVisibility(View.GONE);
        }

        findViewById(R.id.btnViewAvailableMeals).setOnClickListener(v -> loadFragment(new AvailableMealsFragment()));
        findViewById(R.id.btnHistoryReceivedMeals).setOnClickListener(v -> loadFragment(new ReceivedMealsHistoryFragment()));
        findViewById(R.id.btnPostMeal).setOnClickListener(v -> loadFragment(new PostMealFragment()));
        findViewById(R.id.btnHistoryDonatedMeals).setOnClickListener(v -> loadFragment(new DonatedMealsHistoryFragment()));

        // About Us / Contact Us Button
        findViewById(R.id.btnAboutContactUs).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AboutContactActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}