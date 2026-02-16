package com.furkan.investmentmanagementsystem.services;

import com.furkan.investmentmanagementsystem.models.PortfolioItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the PortfolioService interface.
 * Manages user portfolios using in-memory storage.
 */
public class PortfolioServiceImpl implements PortfolioService {
    // Thread-safe map of user portfolios
    private final Map<String, ObservableList<PortfolioItem>> userPortfolios = new ConcurrentHashMap<>();

    @Override
    public ObservableList<PortfolioItem> getUserPortfolio(String userEmail) {
        return userPortfolios.computeIfAbsent(userEmail, k -> FXCollections.observableArrayList());
    }

    @Override
    public boolean addPosition(String userEmail, PortfolioItem item) {
        if (userEmail == null || item == null) {
            throw new IllegalArgumentException("User email and portfolio item cannot be null");
        }

        ObservableList<PortfolioItem> portfolio = getUserPortfolio(userEmail);
        
        // Check if position already exists
        Optional<PortfolioItem> existingItem = portfolio.stream()
                .filter(p -> p.getAsset().equals(item.getAsset()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing position
            PortfolioItem current = existingItem.get();
            double newQuantity = current.getQuantity() + item.getQuantity();
            
            if (newQuantity == 0) {
                return removePosition(userEmail, item.getAsset());
            } else if (newQuantity < 0) {
                throw new IllegalArgumentException("Insufficient quantity to sell");
            }
            
            // Update quantity and refresh price
            current.quantityProperty().set(newQuantity);
            current.refreshPrice(current.getLastPrice());
            return true;
        } else {
            // Add new position
            return portfolio.add(item);
        }
    }

    @Override
    public boolean updatePosition(String userEmail, String asset, int newQuantity) {
        if (userEmail == null || asset == null) {
            throw new IllegalArgumentException("User email and asset cannot be null");
        }

        Optional<PortfolioItem> existingItem = findPosition(userEmail, asset);
        if (existingItem.isEmpty()) {
            throw new IllegalArgumentException("Position not found for asset: " + asset);
        }

        if (newQuantity == 0) {
            return removePosition(userEmail, asset);
        } else if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        PortfolioItem item = existingItem.get();
        item.quantityProperty().set(newQuantity);
        item.refreshPrice(item.getLastPrice());
        return true;
    }

    @Override
    public boolean removePosition(String userEmail, String asset) {
        if (userEmail == null || asset == null) {
            throw new IllegalArgumentException("User email and asset cannot be null");
        }

        ObservableList<PortfolioItem> portfolio = getUserPortfolio(userEmail);
        return portfolio.removeIf(item -> item.getAsset().equals(asset));
    }

    @Override
    public Optional<PortfolioItem> findPosition(String userEmail, String asset) {
        if (userEmail == null || asset == null) {
            throw new IllegalArgumentException("User email and asset cannot be null");
        }

        return getUserPortfolio(userEmail).stream()
                .filter(item -> item.getAsset().equals(asset))
                .findFirst();
    }

    @Override
    public double getTotalPortfolioValue(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        return getUserPortfolio(userEmail).stream()
                .mapToDouble(PortfolioItem::getTotalValue)
                .sum();
    }

    @Override
    public double getTotalProfitLoss(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        return getUserPortfolio(userEmail).stream()
                .mapToDouble(PortfolioItem::getTotalRevenue)
                .sum();
    }

    @Override
    public void refreshPortfolioPrices(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        getUserPortfolio(userEmail).forEach(item -> item.refreshPrice(item.getLastPrice()));
    }

    /**
     * Gets the distribution of asset types in a user's portfolio.
     * @param userEmail The email of the user
     * @return Map of asset type to total value
     */
    public Map<String, Double> getAssetTypeDistribution(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        Map<String, Double> distribution = new HashMap<>();
        getUserPortfolio(userEmail).forEach(item -> 
            distribution.merge(item.getType(), item.getTotalValue(), Double::sum)
        );
        return distribution;
    }

    /**
     * Gets the risk profile of a user's portfolio.
     * @param userEmail The email of the user
     * @return Risk profile description
     */
    public String getPortfolioRiskProfile(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        Map<String, Double> distribution = getAssetTypeDistribution(userEmail);
        double total = distribution.values().stream().mapToDouble(Double::doubleValue).sum();
        
        if (total == 0) return "No Investments";
        
        double cryptoRatio = distribution.getOrDefault("Crypto", 0.0) / total;
        double stockRatio = distribution.getOrDefault("Stock", 0.0) / total;
        double forexRatio = distribution.getOrDefault("Forex", 0.0) / total;

        if (cryptoRatio > 0.6) return "Very High Risk (Crypto Heavy)";
        if (cryptoRatio > 0.3) return "High Risk (Significant Crypto)";
        if (stockRatio > 0.7) return "Medium-High Risk (Stock Biased)";
        if (stockRatio > 0.4 && forexRatio > 0.3) return "Medium Risk (Balanced)";
        return "Low to Medium Risk (Diversified or Forex/Fixed Income Heavy)";
    }
} 