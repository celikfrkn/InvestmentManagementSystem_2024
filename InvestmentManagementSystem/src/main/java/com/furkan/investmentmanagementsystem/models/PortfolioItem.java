package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Represents an individual asset item in a portfolio.
 */
public class PortfolioItem {
    private final StringProperty asset;
    private final StringProperty type;
    private final DoubleProperty quantity;
    private final DoubleProperty openPrice;
    private final DoubleProperty lastPrice;
    private final DoubleProperty totalValue;
    private final DoubleProperty totalRevenue;
    private final ObjectProperty<LocalDateTime> lastUpdateTime;
    private final BooleanProperty isActive;

    public PortfolioItem(String asset, String type, double quantity, double openPrice) {
        // Validate parameters
        if (asset == null || asset.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name cannot be null or empty");
        }
        if (type == null || !isValidAssetType(type)) {
            throw new IllegalArgumentException("Invalid asset type. Must be one of: Stock, Crypto, Forex");
        }
        if (openPrice <= 0) {
            throw new IllegalArgumentException("Open price must be greater than 0");
        }

        this.asset = new SimpleStringProperty(asset);
        this.type = new SimpleStringProperty(type);
        this.quantity = new SimpleDoubleProperty(quantity);
        this.openPrice = new SimpleDoubleProperty(openPrice);
        this.lastPrice = new SimpleDoubleProperty(generateSimulatedPrice(openPrice));
        this.totalValue = new SimpleDoubleProperty(0);
        this.totalRevenue = new SimpleDoubleProperty(0);
        this.lastUpdateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        this.isActive = new SimpleBooleanProperty(true);

        // Bind calculated properties
        totalValue.bind(quantityProperty().multiply(lastPriceProperty()));
        totalRevenue.bind(quantityProperty().multiply(lastPriceProperty().subtract(openPriceProperty())));
    }

    private boolean isValidAssetType(String type) {
        return type != null && (
            type.equals("Stock") ||
            type.equals("Crypto") ||
            type.equals("Forex")
        );
    }

    private double generateSimulatedPrice(double basePrice) {
        // Generate a random price within ±10% of the base price
        return basePrice * (1 + (new Random().nextDouble() * 0.2 - 0.1));
    }

    public String getAsset() {
        return asset.get();
    }

    public StringProperty assetProperty() {
        return asset;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public double getQuantity() {
        return quantity.get();
    }

    public DoubleProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public double getOpenPrice() {
        return openPrice.get();
    }

    public DoubleProperty openPriceProperty() {
        return openPrice;
    }

    public double getLastPrice() {
        return lastPrice.get();
    }

    public DoubleProperty lastPriceProperty() {
        return lastPrice;
    }

    public void setLastPrice(double price) {
        this.lastPrice.set(price);
        this.lastUpdateTime.set(LocalDateTime.now());
    }

    public double getTotalValue() {
        return totalValue.get();
    }

    public DoubleProperty totalValueProperty() {
        return totalValue;
    }

    public double getTotalRevenue() {
        return totalRevenue.get();
    }

    public DoubleProperty totalRevenueProperty() {
        return totalRevenue;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime.get();
    }

    public ObjectProperty<LocalDateTime> lastUpdateTimeProperty() {
        return lastUpdateTime;
    }

    public boolean isActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive.set(active);
    }

    public String getPosition() {
        return quantity.get() >= 0 ? "Long" : "Short";
    }

    public double getProfitLossPercent() {
        if (quantity.get() == 0 || openPrice.get() == 0) return 0;
        // Always use the standard formula for long positions, no leverage
        return (lastPrice.get() - openPrice.get()) / openPrice.get() * 100;
    }

    public void refreshPrice(double newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        this.lastPrice.set(newPrice);
        this.lastUpdateTime.set(LocalDateTime.now());
    }

    public void simulatePriceChange() {
        refreshPrice(generateSimulatedPrice(openPrice.get()));
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Quantity: %.2f, Price: %.2f, Total Value: %.2f",
            getAsset(), getType(), getQuantity(), getLastPrice(), getTotalValue());
    }
} 