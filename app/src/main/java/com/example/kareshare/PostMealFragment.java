package com.example.kareshare;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class PostMealFragment extends Fragment {

    private EditText etMealDescription, etQuantity, etBestBeforeTime, etPickupInstructions;
    private Button btnPostMeal;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_meal, container, false);

        etMealDescription = view.findViewById(R.id.etMealDescription);
        etQuantity = view.findViewById(R.id.etQuantity);
        etBestBeforeTime = view.findViewById(R.id.etBestBeforeTime);
        etPickupInstructions = view.findViewById(R.id.etPickupInstructions);
        btnPostMeal = view.findViewById(R.id.btnPostMeal);

        db = FirebaseFirestore.getInstance();

        // Set up Time Picker Dialog
        etBestBeforeTime.setOnClickListener(v -> openTimePickerDialog());

        btnPostMeal.setOnClickListener(v -> postMealToDatabase());

        return view;
    }

    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    // Format the time and set it in the EditText
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    etBestBeforeTime.setText(formattedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void postMealToDatabase() {
        String mealDescription = etMealDescription.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String bestBeforeTime = etBestBeforeTime.getText().toString().trim();
        String pickupInstructions = etPickupInstructions.getText().toString().trim();

        if (mealDescription.isEmpty() || quantity.isEmpty() || bestBeforeTime.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a meal data object
        Meal meal = new Meal(mealDescription, quantity, bestBeforeTime, pickupInstructions);

        // Save to Firebase
        db.collection("meals").add(meal)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Meal posted successfully!", Toast.LENGTH_SHORT).show();
                    // Clear fields after saving
                    etMealDescription.setText("");
                    etQuantity.setText("");
                    etBestBeforeTime.setText("");
                    etPickupInstructions.setText("");
                    // Close fragment and return to dashboard
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to post meal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Create a simple Meal class to store meal details
    public static class Meal {
        private String description;
        private String quantity;
        private String bestBeforeTime;
        private String pickupInstructions;

        public Meal() {}

        public Meal(String description, String quantity, String bestBeforeTime, String pickupInstructions) {
            this.description = description;
            this.quantity = quantity;
            this.bestBeforeTime = bestBeforeTime;
            this.pickupInstructions = pickupInstructions;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getBestBeforeTime() {
            return bestBeforeTime;
        }

        public void setBestBeforeTime(String bestBeforeTime) {
            this.bestBeforeTime = bestBeforeTime;
        }

        public String getPickupInstructions() {
            return pickupInstructions;
        }

        public void setPickupInstructions(String pickupInstructions) {
            this.pickupInstructions = pickupInstructions;
        }
    }
}