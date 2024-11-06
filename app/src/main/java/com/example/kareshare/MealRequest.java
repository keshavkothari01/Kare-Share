package com.example.kareshare;

public class MealRequest {
    private String id;
    private String ngoName;
    private String mealDescription;
    private String requestStatus;

    public MealRequest() {}

    public MealRequest(String ngoName, String mealDescription, String requestStatus) {
        this.ngoName = ngoName;
        this.mealDescription = mealDescription;
        this.requestStatus = requestStatus;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNgoName() { return ngoName; }
    public void setNgoName(String ngoName) { this.ngoName = ngoName; }

    public String getMealDescription() { return mealDescription; }
    public void setMealDescription(String mealDescription) { this.mealDescription = mealDescription; }

    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }
}