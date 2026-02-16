package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.Bindings;

/**
 * Represents a user's investment portfolio containing various assets.
 */
public class Portfolio {
    public enum RiskProfile {
        VERY_CONSERVATIVE,
        CONSERVATIVE,
        MODERATE,
        AGGRESSIVE,
        VERY_AGGRESSIVE
    }

    private final IntegerProperty id;
    private final IntegerProperty ownerId;
    private final StringProperty description;
    private final StringProperty strategy;
    private final DoubleProperty totalCost;
    private final DoubleProperty totalProfitLoss;
    private final ObjectProperty<RiskProfile> riskProfile;
    private final DoubleProperty targetReturn;
    private final DoubleProperty maxDrawdown;
    private final DoubleProperty sharpeRatio;
    private final ObjectProperty<LocalDateTime> lastModifiedDate;
    private final StringProperty name;
    private final ObjectProperty<LocalDateTime> creationDate;
    private final ObjectProperty<LocalDateTime> lastUpdateDate;
    private final ObservableList<PortfolioItem> items;
    private final DoubleProperty totalValue;
    private final DoubleProperty totalRevenue;
    private final DoubleProperty totalProfitLossPercent;
    private final Map<String, DoubleProperty> assetTypeDistribution;
    private final BooleanProperty isActive;
    private String userId;
    private ObservableList<Transaction> transactions;

    public Portfolio(String userId) {
        this.id = new SimpleIntegerProperty(0);
        this.ownerId = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.strategy = new SimpleStringProperty("");
        this.totalCost = new SimpleDoubleProperty(0.0);
        this.totalProfitLoss = new SimpleDoubleProperty(0.0);
        this.riskProfile = new SimpleObjectProperty<>(RiskProfile.MODERATE);
        this.targetReturn = new SimpleDoubleProperty(0.0);
        this.maxDrawdown = new SimpleDoubleProperty(0.0);
        this.sharpeRatio = new SimpleDoubleProperty(0.0);
        this.creationDate = new SimpleObjectProperty<>(LocalDateTime.now());
        this.lastModifiedDate = new SimpleObjectProperty<>(LocalDateTime.now());
        this.lastUpdateDate = new SimpleObjectProperty<>(LocalDateTime.now());
        this.items = FXCollections.observableArrayList();
        this.totalValue = new SimpleDoubleProperty(0.0);
        this.totalRevenue = new SimpleDoubleProperty(0.0);
        this.totalProfitLossPercent = new SimpleDoubleProperty(0.0);
        this.assetTypeDistribution = new HashMap<>();
        this.isActive = new SimpleBooleanProperty(true);
        this.userId = userId;
        this.transactions = FXCollections.observableArrayList();
        Arrays.asList("Stock", "Crypto", "Forex").forEach(type -> assetTypeDistribution.put(type, new SimpleDoubleProperty(0.0)));
        bindCalculatedProperties();
    }

    private void bindCalculatedProperties() {
        // Bind total value to sum of all items' total values
        DoubleBinding totalValueBinding = Bindings.createDoubleBinding(() ->
            items.stream()
                .mapToDouble(PortfolioItem::getTotalValue)
                .sum(),
            items);
        totalValue.bind(totalValueBinding);

        // Bind total revenue to sum of all items' total revenues
        DoubleBinding totalRevenueBinding = Bindings.createDoubleBinding(() ->
            items.stream()
                .mapToDouble(PortfolioItem::getTotalRevenue)
                .sum(),
            items);
        totalRevenue.bind(totalRevenueBinding);

        // Bind total profit/loss percentage
        totalProfitLossPercent.bind(totalRevenue.divide(totalValue).multiply(100));

        // Update asset type distribution when items change
        items.addListener((javafx.collections.ListChangeListener.Change<? extends PortfolioItem> change) -> {
            updateAssetTypeDistribution();
        });
    }

    private void updateAssetTypeDistribution() {
        double total = totalValue.get();
        if (total == 0) {
            assetTypeDistribution.values().forEach(prop -> prop.set(0.0));
            return;
        }

        Map<String, Double> typeValues = items.stream()
            .collect(Collectors.groupingBy(
                PortfolioItem::getType,
                Collectors.summingDouble(PortfolioItem::getTotalValue)
            ));

        assetTypeDistribution.forEach((type, prop) -> 
            prop.set(typeValues.getOrDefault(type, 0.0) / total * 100));
    }

    // Portfolio management methods
    public void addItem(PortfolioItem item) {
        Optional<PortfolioItem> existingItem = items.stream()
            .filter(i -> i.getAsset().equals(item.getAsset()))
            .findFirst();

        if (existingItem.isPresent()) {
            PortfolioItem current = existingItem.get();
            current.setQuantity(current.getQuantity() + item.getQuantity());
        } else {
            items.add(item);
        }
        lastUpdateDate.set(LocalDateTime.now());
    }

    public boolean removeItem(String assetName) {
        boolean removed = items.removeIf(item -> item.getAsset().equals(assetName));
        if (removed) {
            lastUpdateDate.set(LocalDateTime.now());
        }
        return removed;
    }

    public Optional<PortfolioItem> getItem(String assetName) {
        return items.stream()
            .filter(item -> item.getAsset().equals(assetName))
            .findFirst();
    }

    public void updateItemPrice(String assetName, double newPrice) {
        getItem(assetName).ifPresent(item -> {
            item.refreshPrice(newPrice);
            lastUpdateDate.set(LocalDateTime.now());
        });
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Getters for properties
    public String getName() { return name.get(); }
    public LocalDateTime getCreationDate() { return creationDate.get(); }
    public LocalDateTime getLastUpdateDate() { return lastUpdateDate.get(); }
    public ObservableList<PortfolioItem> getItems() { return items; }
    public double getTotalValue() { return totalValue.get(); }
    public double getTotalRevenue() { return totalRevenue.get(); }
    public double getTotalProfitLossPercent() { return totalProfitLossPercent.get(); }
    public double getAssetTypePercentage(String type) { 
        return assetTypeDistribution.getOrDefault(type, new SimpleDoubleProperty(0.0)).get(); 
    }
    public boolean isActive() { return isActive.get(); }

    // Getters for new fields
    public int getId() { return id.get(); }
    public int getOwnerId() { return ownerId.get(); }
    public String getDescription() { return description.get(); }
    public String getStrategy() { return strategy.get(); }
    public double getTotalCost() { return totalCost.get(); }
    public double getTotalProfitLoss() { return totalProfitLoss.get(); }
    public RiskProfile getRiskProfile() { return riskProfile.get(); }
    public double getTargetReturn() { return targetReturn.get(); }
    public double getMaxDrawdown() { return maxDrawdown.get(); }
    public double getSharpeRatio() { return sharpeRatio.get(); }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate.get(); }

    // Setters for new fields
    public void setId(int id) { this.id.set(id); }
    public void setOwnerId(int ownerId) { this.ownerId.set(ownerId); }
    public void setDescription(String description) { this.description.set(description); }
    public void setStrategy(String strategy) { this.strategy.set(strategy); }
    public void setTotalCost(double totalCost) { this.totalCost.set(totalCost); }
    public void setTotalProfitLoss(double totalProfitLoss) { this.totalProfitLoss.set(totalProfitLoss); }
    public void setRiskProfile(RiskProfile riskProfile) { this.riskProfile.set(riskProfile); }
    public void setTargetReturn(double targetReturn) { this.targetReturn.set(targetReturn); }
    public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown.set(maxDrawdown); }
    public void setSharpeRatio(double sharpeRatio) { this.sharpeRatio.set(sharpeRatio); }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate.set(lastModifiedDate); }

    // Property getters for JavaFX binding
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<LocalDateTime> creationDateProperty() { return creationDate; }
    public ObjectProperty<LocalDateTime> lastUpdateDateProperty() { return lastUpdateDate; }
    public DoubleProperty totalValueProperty() { return totalValue; }
    public DoubleProperty totalRevenueProperty() { return totalRevenue; }
    public DoubleProperty totalProfitLossPercentProperty() { return totalProfitLossPercent; }
    public DoubleProperty assetTypePercentageProperty(String type) { 
        return assetTypeDistribution.getOrDefault(type, new SimpleDoubleProperty(0.0)); 
    }
    public BooleanProperty isActiveProperty() { return isActive; }

    // Property getters for new fields
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty ownerIdProperty() { return ownerId; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty strategyProperty() { return strategy; }
    public DoubleProperty totalCostProperty() { return totalCost; }
    public DoubleProperty totalProfitLossProperty() { return totalProfitLoss; }
    public ObjectProperty<RiskProfile> riskProfileProperty() { return riskProfile; }
    public DoubleProperty targetReturnProperty() { return targetReturn; }
    public DoubleProperty maxDrawdownProperty() { return maxDrawdown; }
    public DoubleProperty sharpeRatioProperty() { return sharpeRatio; }
    public ObjectProperty<LocalDateTime> lastModifiedDateProperty() { return lastModifiedDate; }

    // Setters
    public void setActive(boolean active) { this.isActive.set(active); }

    @Override
    public String toString() {
        return String.format("Portfolio '%s' (Value: $%.2f, P/L: %.2f%%)",
            name.get(), totalValue.get(), totalProfitLossPercent.get());
    }

    public String getUserId() {
        return userId;
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public Map<String, Double> getAssetTypeDistribution() {
        Map<String, Double> distribution = new HashMap<>();
        double totalValue = getTotalValue();

        if (totalValue > 0) {
            for (PortfolioItem item : items) {
                String type = item.getType();
                double value = item.getTotalValue();
                distribution.merge(type, value / totalValue, Double::sum);
            }
        }

        return distribution;
    }

    public List<Transaction> getAssetTransactions(String assetName) {
        return transactions.stream()
                .filter(t -> t.getAssetName().equals(assetName))
                .toList();
    }

    public Map<String, Double> getPerformanceMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("totalValue", getTotalValue());
        metrics.put("assetCount", (double) items.size());
        metrics.put("transactionCount", (double) transactions.size());
        return metrics;
    }

    public boolean rebalance(Map<String, Double> targetAllocation) {
        Map<String, Double> currentAllocation = getAssetTypeDistribution();
        // Implementation of rebalancing logic would go here
        return true;
    }
} 