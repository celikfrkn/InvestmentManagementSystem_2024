package com.furkan.investmentmanagementsystem.models;

import com.furkan.investmentmanagementsystem.interfaces.Analyzable;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a portfolio manager user in the system.
 * Manages user portfolios and provides recommendations.
 */
public class PortfolioManager extends User implements Analyzable {
    private final Map<String, String> userReports;
    private final Map<String, List<String>> portfolioRecommendations;
    private final Map<String, Map<String, Double>> portfolioMetrics;
    private ObservableList<Portfolio> managedPortfolios;
    private Map<String, InvestmentStrategy> strategies;
    private Map<String, RiskProfile> riskProfiles;

    public PortfolioManager(String id, String email, String password) {
        super(id, email, "PortfolioManager", password);
        this.userReports = new HashMap<>();
        this.portfolioRecommendations = new HashMap<>();
        this.portfolioMetrics = new HashMap<>();
        this.managedPortfolios = FXCollections.observableArrayList();
        this.strategies = new HashMap<>();
        this.riskProfiles = new HashMap<>();
    }

    // Report management methods
    public void addUserReport(String userEmail, String report) {
        userReports.put(userEmail, report);
        updatePortfolioMetrics(userEmail, report);
    }

    public String getUserReport(String userEmail) {
        return userReports.getOrDefault(userEmail, "No report available.");
    }

    // Recommendation management methods
    public void addPortfolioRecommendation(String userEmail, String recommendation) {
        portfolioRecommendations.computeIfAbsent(userEmail, k -> new ArrayList<>())
            .add(recommendation);
    }

    public List<String> getPortfolioRecommendations(String userEmail) {
        return portfolioRecommendations.getOrDefault(userEmail, new ArrayList<>());
    }

    public void clearUserReports(String userEmail) {
        userReports.remove(userEmail);
        portfolioRecommendations.remove(userEmail);
        portfolioMetrics.remove(userEmail);
    }

    private void updatePortfolioMetrics(String userEmail, String report) {
        // Extract metrics from report and update portfolioMetrics
        // This is a placeholder - implement actual metric extraction logic
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("riskScore", calculateRiskScore(report));
        metrics.put("performanceScore", calculatePerformanceScore(report));
        portfolioMetrics.put(userEmail, metrics);
    }

    private double calculateRiskScore(String report) {
        // Placeholder for risk score calculation
        // Implement actual risk analysis logic
        return 0.5;
    }

    private double calculatePerformanceScore(String report) {
        // Placeholder for performance score calculation
        // Implement actual performance analysis logic
        return 0.5;
    }

    // Analyzable interface implementation
    @Override
    public Map<String, Double> analyze() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put("userCount", (double) userReports.size());
        analysis.put("recommendationCount", (double) portfolioRecommendations.size());
        // Calculate average metrics across all portfolios
        double avgRiskScore = portfolioMetrics.values().stream()
            .mapToDouble(m -> m.getOrDefault("riskScore", 0.0))
            .average()
            .orElse(0.0);
        double avgPerformanceScore = portfolioMetrics.values().stream()
            .mapToDouble(m -> m.getOrDefault("performanceScore", 0.0))
            .average()
            .orElse(0.0);
        analysis.put("avgRiskScore", avgRiskScore);
        analysis.put("avgPerformanceScore", avgPerformanceScore);
        return analysis;
    }

    @Override
    public String getRiskLevel() {
        // Calculate overall risk level based on portfolio metrics
        double avgRiskScore = portfolioMetrics.values().stream()
            .mapToDouble(m -> m.getOrDefault("riskScore", 0.0))
            .average()
            .orElse(0.0);
        if (avgRiskScore > 0.7) return "HIGH";
        if (avgRiskScore > 0.4) return "MEDIUM";
        return "LOW";
    }

    @Override
    public Map<String, Double> getPerformanceMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("userCount", (double) userReports.size());
        metrics.put("recommendationCount", (double) portfolioRecommendations.size());
        return metrics;
    }

    @Override
    public boolean validateCriteria(Map<String, Object> criteria) {
        // Implement validation logic based on portfolio criteria
        return true; // Placeholder
    }

    @Override
    public String getDashboardType() {
        return "PortfolioManager";
    }

    @Override
    public boolean validateUser() {
        return getEmail() != null && !getEmail().isEmpty() &&
               getPassword() != null && !getPassword().isEmpty();
    }

    public ObservableList<Portfolio> getManagedPortfolios() {
        return managedPortfolios;
    }

    public void addManagedPortfolio(Portfolio portfolio) {
        managedPortfolios.add(portfolio);
        updatePortfolioStrategy(portfolio);
    }

    public void removeManagedPortfolio(Portfolio portfolio) {
        managedPortfolios.remove(portfolio);
    }

    public InvestmentStrategy getStrategy(String portfolioId) {
        return strategies.getOrDefault(portfolioId, new InvestmentStrategy());
    }

    public void updatePortfolioStrategy(Portfolio portfolio) {
        InvestmentStrategy strategy = new InvestmentStrategy();
        strategy.setPortfolioId(String.valueOf(portfolio.getId()));
        strategy.setTargetAllocation(calculateTargetAllocation(portfolio));
        strategy.setRebalancingThreshold(0.05); // 5% threshold
        strategies.put(String.valueOf(portfolio.getId()), strategy);
    }

    public RiskProfile getRiskProfile(String portfolioId) {
        return riskProfiles.getOrDefault(portfolioId, new RiskProfile());
    }

    public void updateRiskProfile(String portfolioId, RiskProfile profile) {
        riskProfiles.put(portfolioId, profile);
    }

    private Map<String, Double> calculateTargetAllocation(Portfolio portfolio) {
        Map<String, Double> allocation = new HashMap<>();
        // Placeholder for allocation calculation
        // In a real implementation, this would consider various factors
        allocation.put("Stocks", 0.6);
        allocation.put("Bonds", 0.3);
        allocation.put("Cash", 0.1);
        return allocation;
    }

    public void rebalancePortfolio(Portfolio portfolio) {
        InvestmentStrategy strategy = getStrategy(String.valueOf(portfolio.getId()));
        Map<String, Double> currentAllocation = portfolio.getAssetTypeDistribution();
        Map<String, Double> targetAllocation = strategy.getTargetAllocation();
        for (Map.Entry<String, Double> entry : targetAllocation.entrySet()) {
            String assetType = entry.getKey();
            double targetWeight = entry.getValue();
            double currentWeight = currentAllocation.getOrDefault(assetType, 0.0);
            if (Math.abs(currentWeight - targetWeight) > strategy.getRebalancingThreshold()) {
                // Calculate and execute rebalancing trades
                double adjustment = (targetWeight - currentWeight) * portfolio.getTotalValue();
                // Execute rebalancing trades (implementation would depend on trading system)
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - Portfolio Manager",
            getFirstName(), getLastName(), getEmail());
    }
} 