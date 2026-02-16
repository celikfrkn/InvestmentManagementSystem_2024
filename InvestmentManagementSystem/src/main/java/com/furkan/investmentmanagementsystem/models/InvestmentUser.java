package com.furkan.investmentmanagementsystem.models;

import com.furkan.investmentmanagementsystem.interfaces.Analyzable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

/**
 * Represents an investment user who can manage their portfolio and execute transactions.
 */
public class InvestmentUser extends User implements Analyzable {
    private final Portfolio portfolio;
    private final ObservableList<Transaction> transactions;
    private final DoubleProperty totalAssets;
    private final StringProperty riskTolerance;
    private final StringProperty investmentStrategy;
    private final DoubleProperty monthlyInvestmentTarget;
    private final BooleanProperty hasCompletedOnboarding;

    public enum RiskTolerance {
        VERY_CONSERVATIVE,
        CONSERVATIVE,
        MODERATE,
        AGGRESSIVE,
        VERY_AGGRESSIVE
    }

    public InvestmentUser(String id, String email, String password) {
        super(id, email, "NormalUser", password);
        this.portfolio = new Portfolio(id);
        this.transactions = FXCollections.observableArrayList();
        this.totalAssets = new SimpleDoubleProperty(0);
        this.riskTolerance = new SimpleStringProperty(RiskTolerance.MODERATE.name());
        this.investmentStrategy = new SimpleStringProperty("");
        this.monthlyInvestmentTarget = new SimpleDoubleProperty(0);
        this.hasCompletedOnboarding = new SimpleBooleanProperty(false);

        // Set up listeners for portfolio updates
        setupPortfolioListeners();
    }

    private void setupPortfolioListeners() {
        portfolio.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends PortfolioItem> change) -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                    updateTotalAssets();
                }
            }
        });
    }

    private void updateTotalAssets() {
        double total = portfolio.getItems().stream()
                .mapToDouble(PortfolioItem::getTotalValue)
                .sum();
        totalAssets.set(total);
    }

    @Override
    public String getDashboardType() {
        return "NormalUser";
    }

    @Override
    public boolean validateUser() {
        return getEmail() != null && !getEmail().isEmpty() &&
               getPassword() != null && !getPassword().isEmpty();
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        portfolio.addTransaction(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    // Property getters for JavaFX binding
    public DoubleProperty totalAssetsProperty() { return totalAssets; }
    public StringProperty riskToleranceProperty() { return riskTolerance; }
    public StringProperty investmentStrategyProperty() { return investmentStrategy; }
    public DoubleProperty monthlyInvestmentTargetProperty() { return monthlyInvestmentTarget; }
    public BooleanProperty hasCompletedOnboardingProperty() { return hasCompletedOnboarding; }

    // Setters
    public void setRiskTolerance(RiskTolerance riskTolerance) { 
        this.riskTolerance.set(riskTolerance.name()); 
    }

    public void setInvestmentStrategy(String strategy) { 
        this.investmentStrategy.set(strategy); 
    }

    public void setMonthlyInvestmentTarget(double target) { 
        this.monthlyInvestmentTarget.set(target); 
    }

    public void setOnboardingCompleted(boolean completed) { 
        this.hasCompletedOnboarding.set(completed); 
    }

    // Analyzable interface implementation
    @Override
    public Map<String, Double> analyze() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put("totalAssets", totalAssets.get());
        analysis.put("portfolioCount", (double) portfolio.getItems().size());
        analysis.put("transactionCount", (double) transactions.size());
        analysis.put("monthlyTarget", monthlyInvestmentTarget.get());
        return analysis;
    }

    @Override
    public String getRiskLevel() {
        return riskTolerance.get();
    }

    @Override
    public Map<String, Double> getPerformanceMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("totalValue", totalAssets.get());
        metrics.put("monthlyTarget", monthlyInvestmentTarget.get());
        return metrics;
    }

    @Override
    public boolean validateCriteria(Map<String, Object> criteria) {
        return true; // Placeholder implementation
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - Investment User",
            getFirstName(), getLastName(), getEmail());
    }

    public ObservableList<PortfolioItem> getPortfolioItems() {
        return portfolio.getItems();
    }
} 