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
import java.util.HashMap;
import java.util.Map;

public class MealPostingPageFragment extends Fragment {

    private EditText mealDescription, quantityAvailable, bestBeforeTime, pickupInstructions;
    private Button btnSubmitMeal;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_posting_page, container, false);

        // Initialize fields
        mealDescription = view.findViewById(R.id.mealDescription);
        quantityAvailable = view.findViewById(R.id.quantityAvailable);
        bestBeforeTime = view.findViewById(R.id.bestBeforeTime);
        pickupInstructions = view.findViewById(R.id.pickupInstructions);
        btnSubmitMeal = view.findViewById(R.id.btnSubmitMeal);
        db = FirebaseFirestore.getInstance();

        // Set up best before time picker
        bestBeforeTime.setOnClickListener(v -> showTimePickerDialog());

        // Set up submit button
        btnSubmitMeal.setOnClickListener(v -> postMealToDatabase());

        return view;
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String time = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
            bestBeforeTime.setText(time);
        }, 12, 0, true);
        timePicker.show();
    }

    private void postMealToDatabase() {
        String description = mealDescription.getText().toString();
        String quantity = quantityAvailable.getText().toString();
        String bestBefore = bestBeforeTime.getText().toString();
        String instructions = pickupInstructions.getText().toString();

        if (description.isEmpty() || quantity.isEmpty() || bestBefore.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mealData = new HashMap<>();
        mealData.put("description", description);
        mealData.put("quantity", quantity);
        mealData.put("bestBeforeTime", bestBefore);
        mealData.put("pickupInstructions", instructions);

        db.collection("meals").add(mealData)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Meal posted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error posting meal", Toast.LENGTH_SHORT).show());
    }
}

