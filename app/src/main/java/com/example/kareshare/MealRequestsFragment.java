package com.example.kareshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MealRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private FirebaseFirestore db;
    private List<MealRequest> requestsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_requests, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        requestsList = new ArrayList<>();

        fetchRequests();

        return view;
    }

    private void fetchRequests() {
        db.collection("requests")
                .whereEqualTo("requestStatus", "Pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MealRequest request = document.toObject(MealRequest.class);
                            request.setId(document.getId());
                            requestsList.add(request);
                        }
                        adapter = new RequestsAdapter(requestsList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Error fetching requests.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

        private List<MealRequest> requests;

        public RequestsAdapter(List<MealRequest> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            MealRequest request = requests.get(position);
            holder.tvNgoName.setText("NGO: " + request.getNgoName());
            holder.tvMealDescription.setText("Meal: " + request.getMealDescription());

            holder.btnAccept.setOnClickListener(v -> acceptRequest(request));
            holder.btnDecline.setOnClickListener(v -> declineRequest(request));
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {
            TextView tvNgoName, tvMealDescription;
            Button btnAccept, btnDecline;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNgoName = itemView.findViewById(R.id.tvNgoName);
                tvMealDescription = itemView.findViewById(R.id.tvMealDescription);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnDecline = itemView.findViewById(R.id.btnDecline);
            }
        }
    }

    private void acceptRequest(MealRequest request) {
        db.collection("requests").document(request.getId())
                .update("requestStatus", "Accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Request accepted.", Toast.LENGTH_SHORT).show();
                    fetchRequests(); // Refresh the list
                });
    }

    private void declineRequest(MealRequest request) {
        db.collection("requests").document(request.getId())
                .update("requestStatus", "Declined")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Request declined.", Toast.LENGTH_SHORT).show();
                    fetchRequests(); // Refresh the list
                });
    }
}