package com.furkan.investmentmanagementsystem.models;

import com.furkan.investmentmanagementsystem.interfaces.Analyzable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

/**
 * Represents an economist user in the system.
 * Manages economic forecasts and analysis.
 */
public class Economist extends User implements Analyzable {
    private final ObservableList<Forecast> forecasts;
    private final Map<String, Double> economicIndicators;

    public Economist(String id, String email, String password) {
        super(id, email, "Economist", password);
        this.forecasts = FXCollections.observableArrayList();
        this.economicIndicators = new HashMap<>();
    }

    // Forecast management methods
    public void addForecast(Forecast forecast) {
        forecasts.add(forecast);
        updateEconomicIndicators(forecast);
    }

    public void removeForecast(Forecast forecast) {
        forecasts.remove(forecast);
    }

    public ObservableList<Forecast> getForecasts() {
        return forecasts;
    }

    private void updateEconomicIndicators(Forecast forecast) {
        try {
            economicIndicators.put("interestRate", Double.parseDouble(forecast.getInterestRate()));
            economicIndicators.put("inflation", Double.parseDouble(forecast.getInflation()));
            economicIndicators.put("usdTry", Double.parseDouble(forecast.getUsdTry()));
        } catch (NumberFormatException e) {
            // Handle invalid number format in forecast
            System.err.println("Invalid number format in forecast: " + e.getMessage());
        }
    }

    // Analyzable interface implementation
    @Override
    public Map<String, Double> analyze() {
        Map<String, Double> analysis = new HashMap<>(economicIndicators);
        analysis.put("forecastCount", (double) forecasts.size());
        return analysis;
    }

    @Override
    public String getRiskLevel() {
        // Calculate risk level based on economic indicators
        double avgInflation = economicIndicators.getOrDefault("inflation", 0.0);
        if (avgInflation > 20.0) return "HIGH";
        if (avgInflation > 10.0) return "MEDIUM";
        return "LOW";
    }

    @Override
    public Map<String, Double> getPerformanceMetrics() {
        return new HashMap<>(economicIndicators);
    }

    @Override
    public boolean validateCriteria(Map<String, Object> criteria) {
        // Implement validation logic based on economic criteria
        return true; // Placeholder
    }

    @Override
    public String getDashboardType() {
        return "Economist";
    }

    @Override
    public boolean validateUser() {
        return getEmail() != null && !getEmail().isEmpty() && 
               getPassword() != null && !getPassword().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - Economist",
            getFirstName(), getLastName(), getEmail());
    }

    public Forecast getLatestForecast() {
        return forecasts.isEmpty() ? null : 
               forecasts.stream()
                       .max((f1, f2) -> f1.getTimestamp().compareTo(f2.getTimestamp()))
                       .orElse(null);
    }
} 