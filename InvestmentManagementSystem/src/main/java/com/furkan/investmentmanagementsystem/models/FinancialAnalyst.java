package com.furkan.investmentmanagementsystem.models;

import com.furkan.investmentmanagementsystem.interfaces.Analyzable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

/**
 * Represents a financial analyst user in the system.
 * Manages asset analysis reports and recommendations.
 */
public class FinancialAnalyst extends User implements Analyzable {
    private final ObservableList<AnalystReport> reports;
    private final Map<String, Map<String, Double>> assetMetrics;
    private final Map<String, String> assetRecommendations;

    public FinancialAnalyst(String id, String email, String password) {
        super(id, email, "FinancialAnalyst", password);
        this.reports = FXCollections.observableArrayList();
        this.assetMetrics = new HashMap<>();
        this.assetRecommendations = new HashMap<>();
    }

    // Report management methods
    public void addReport(AnalystReport report) {
        reports.add(report);
        updateAssetMetrics(report);
    }

    public void removeReport(AnalystReport report) {
        reports.remove(report);
    }

    public ObservableList<AnalystReport> getReports() {
        return reports;
    }

    public AnalystReport getLatestReportForAsset(String assetName) {
        return reports.stream()
            .filter(r -> r.getAssetName().equals(assetName))
            .max((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()))
            .orElse(null);
    }

    private void updateAssetMetrics(AnalystReport report) {
        Map<String, Double> metrics = new HashMap<>();
        // Calculate metrics based on report content
        metrics.put("sentiment", calculateSentimentScore(report.getOutlook()));
        metrics.put("confidence", calculateConfidenceScore(report.getReportText()));
        assetMetrics.put(report.getAssetName(), metrics);
    }

    private double calculateSentimentScore(String outlook) {
        // Simple sentiment scoring based on outlook
        return switch (outlook.toLowerCase()) {
            case "bullish" -> 0.8;
            case "neutral" -> 0.5;
            case "bearish" -> 0.2;
            default -> 0.5;
        };
    }

    private double calculateConfidenceScore(String reportText) {
        // Placeholder for confidence score calculation
        // In a real implementation, this would analyze the report text
        return 0.7;
    }

    public void addAssetRecommendation(String assetName, String recommendation) {
        assetRecommendations.put(assetName, recommendation);
    }

    public String getAssetRecommendation(String assetName) {
        return assetRecommendations.getOrDefault(assetName, "No recommendation available.");
    }

    // Analyzable interface implementation
    @Override
    public Map<String, Double> analyze() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put("reportCount", (double) reports.size());
        analysis.put("assetCount", (double) assetMetrics.size());
        analysis.put("recommendationCount", (double) assetRecommendations.size());
        return analysis;
    }

    @Override
    public String getRiskLevel() {
        // Calculate overall risk level based on asset metrics
        double avgSentiment = assetMetrics.values().stream()
            .mapToDouble(m -> m.getOrDefault("sentiment", 0.0))
            .average()
            .orElse(0.0);

        if (avgSentiment < 0.3) return "HIGH";
        if (avgSentiment < 0.6) return "MEDIUM";
        return "LOW";
    }

    @Override
    public Map<String, Double> getPerformanceMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("reportCount", (double) reports.size());
        metrics.put("assetCount", (double) assetMetrics.size());
        return metrics;
    }

    @Override
    public boolean validateCriteria(Map<String, Object> criteria) {
        // Implement validation logic based on analysis criteria
        return true; // Placeholder
    }

    @Override
    public String getDashboardType() {
        return "FinancialAnalyst";
    }

    @Override
    public boolean validateUser() {
        return getEmail() != null && !getEmail().isEmpty() &&
               getPassword() != null && !getPassword().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - Financial Analyst",
            getFirstName(), getLastName(), getEmail());
    }
} 