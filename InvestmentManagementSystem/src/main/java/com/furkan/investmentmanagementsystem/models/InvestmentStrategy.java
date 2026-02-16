package com.furkan.investmentmanagementsystem.models;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents an investment strategy for a portfolio, including target allocations and rebalancing rules.
 */
public class InvestmentStrategy {
    private String portfolioId;
    private Map<String, Double> targetAllocation;
    private double rebalancingThreshold;
    private String strategyType;
    private String description;

    public InvestmentStrategy() {
        this.targetAllocation = new HashMap<>();
        this.rebalancingThreshold = 0.05; // Default 5% threshold
        this.strategyType = "Balanced";
        this.description = "Default balanced investment strategy";
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Map<String, Double> getTargetAllocation() {
        return targetAllocation;
    }

    public void setTargetAllocation(Map<String, Double> targetAllocation) {
        this.targetAllocation = targetAllocation;
    }

    public double getRebalancingThreshold() {
        return rebalancingThreshold;
    }

    public void setRebalancingThreshold(double rebalancingThreshold) {
        this.rebalancingThreshold = rebalancingThreshold;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean needsRebalancing(Map<String, Double> currentAllocation) {
        for (Map.Entry<String, Double> entry : targetAllocation.entrySet()) {
            String assetType = entry.getKey();
            double targetWeight = entry.getValue();
            double currentWeight = currentAllocation.getOrDefault(assetType, 0.0);

            if (Math.abs(currentWeight - targetWeight) > rebalancingThreshold) {
                return true;
            }
        }
        return false;
    }
} 