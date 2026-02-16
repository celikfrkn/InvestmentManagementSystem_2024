package com.furkan.investmentmanagementsystem.models;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents a risk profile for a portfolio, including risk tolerance and limits.
 */
public class RiskProfile {
    private String portfolioId;
    private String riskLevel; // Conservative, Moderate, Aggressive
    private double maxDrawdownLimit;
    private double volatilityLimit;
    private Map<String, Double> assetClassLimits;
    private Map<String, Double> sectorLimits;

    public RiskProfile() {
        this.riskLevel = "Moderate";
        this.maxDrawdownLimit = 0.15; // Default 15% max drawdown
        this.volatilityLimit = 0.20; // Default 20% volatility
        this.assetClassLimits = new HashMap<>();
        this.sectorLimits = new HashMap<>();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getMaxDrawdownLimit() {
        return maxDrawdownLimit;
    }

    public void setMaxDrawdownLimit(double maxDrawdownLimit) {
        this.maxDrawdownLimit = maxDrawdownLimit;
    }

    public double getVolatilityLimit() {
        return volatilityLimit;
    }

    public void setVolatilityLimit(double volatilityLimit) {
        this.volatilityLimit = volatilityLimit;
    }

    public Map<String, Double> getAssetClassLimits() {
        return assetClassLimits;
    }

    public void setAssetClassLimits(Map<String, Double> assetClassLimits) {
        this.assetClassLimits = assetClassLimits;
    }

    public Map<String, Double> getSectorLimits() {
        return sectorLimits;
    }

    public void setSectorLimits(Map<String, Double> sectorLimits) {
        this.sectorLimits = sectorLimits;
    }

    public boolean isWithinLimits(Map<String, Double> currentAllocation) {
        // Check asset class limits
        for (Map.Entry<String, Double> limit : assetClassLimits.entrySet()) {
            String assetClass = limit.getKey();
            double maxAllocation = limit.getValue();
            double currentAssetClassAllocation = currentAllocation.getOrDefault(assetClass, 0.0);

            if (currentAssetClassAllocation > maxAllocation) {
                return false;
            }
        }

        // Check sector limits
        for (Map.Entry<String, Double> limit : sectorLimits.entrySet()) {
            String sector = limit.getKey();
            double maxAllocation = limit.getValue();
            double currentSectorAllocation = currentAllocation.getOrDefault(sector, 0.0);

            if (currentSectorAllocation > maxAllocation) {
                return false;
            }
        }

        return true;
    }

    public boolean isDrawdownWithinLimit(double currentDrawdown) {
        return currentDrawdown <= maxDrawdownLimit;
    }

    public boolean isVolatilityWithinLimit(double currentVolatility) {
        return currentVolatility <= volatilityLimit;
    }
} 