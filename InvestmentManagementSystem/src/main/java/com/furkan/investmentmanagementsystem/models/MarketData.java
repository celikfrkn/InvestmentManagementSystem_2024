package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class MarketData {
    private final StringProperty symbol;
    private final StringProperty name;
    private final StringProperty assetType;
    private final DoubleProperty currentPrice;
    private final DoubleProperty openPrice;
    private final DoubleProperty highPrice;
    private final DoubleProperty lowPrice;
    private final DoubleProperty volume;
    private final DoubleProperty marketCap;
    private final DoubleProperty changePercent;
    private final ObjectProperty<LocalDateTime> lastUpdated;
    private final StringProperty currency;
    private final StringProperty exchange;
    private final DoubleProperty previousClose;
    private final DoubleProperty fiftyTwoWeekHigh;
    private final DoubleProperty fiftyTwoWeekLow;
    private final DoubleProperty peRatio;
    private final DoubleProperty dividendYield;

    public MarketData(String symbol, String name, String assetType, double currentPrice, double openPrice, double highPrice, double lowPrice, double volume, LocalDateTime lastUpdated, String currency, String exchange, double previousClose) {
        this.symbol = new SimpleStringProperty(symbol);
        this.name = new SimpleStringProperty(name);
        this.assetType = new SimpleStringProperty(assetType);
        this.currentPrice = new SimpleDoubleProperty(currentPrice);
        this.openPrice = new SimpleDoubleProperty(openPrice);
        this.highPrice = new SimpleDoubleProperty(highPrice);
        this.lowPrice = new SimpleDoubleProperty(lowPrice);
        this.volume = new SimpleDoubleProperty(volume);
        this.marketCap = new SimpleDoubleProperty(0);
        this.changePercent = new SimpleDoubleProperty(0);
        this.lastUpdated = new SimpleObjectProperty<>(lastUpdated);
        this.currency = new SimpleStringProperty(currency);
        this.exchange = new SimpleStringProperty(exchange);
        this.previousClose = new SimpleDoubleProperty(previousClose);
        this.fiftyTwoWeekHigh = new SimpleDoubleProperty(highPrice);
        this.fiftyTwoWeekLow = new SimpleDoubleProperty(lowPrice);
        this.peRatio = new SimpleDoubleProperty(0);
        this.dividendYield = new SimpleDoubleProperty(0);
        this.currentPrice.addListener((obs, oldVal, newVal) -> updateChangePercent());
        this.previousClose.addListener((obs, oldVal, newVal) -> updateChangePercent());
    }

    private void updateChangePercent() {
        double current = currentPrice.get();
        double previous = previousClose.get();
        if (previous != 0) {
            changePercent.set((current - previous) / previous * 100);
        }
    }

    // Getters
    public String getSymbol() { return symbol.get(); }
    public String getName() { return name.get(); }
    public String getAssetType() { return assetType.get(); }
    public double getCurrentPrice() { return currentPrice.get(); }
    public double getOpenPrice() { return openPrice.get(); }
    public double getHighPrice() { return highPrice.get(); }
    public double getLowPrice() { return lowPrice.get(); }
    public double getVolume() { return volume.get(); }
    public double getMarketCap() { return marketCap.get(); }
    public double getChangePercent() { return changePercent.get(); }
    public LocalDateTime getLastUpdated() { return lastUpdated.get(); }
    public String getCurrency() { return currency.get(); }
    public String getExchange() { return exchange.get(); }
    public double getPreviousClose() { return previousClose.get(); }
    public double getFiftyTwoWeekHigh() { return fiftyTwoWeekHigh.get(); }
    public double getFiftyTwoWeekLow() { return fiftyTwoWeekLow.get(); }
    public double getPeRatio() { return peRatio.get(); }
    public double getDividendYield() { return dividendYield.get(); }

    // Property getters for JavaFX binding
    public StringProperty symbolProperty() { return symbol; }
    public StringProperty nameProperty() { return name; }
    public StringProperty assetTypeProperty() { return assetType; }
    public DoubleProperty currentPriceProperty() { return currentPrice; }
    public DoubleProperty openPriceProperty() { return openPrice; }
    public DoubleProperty highPriceProperty() { return highPrice; }
    public DoubleProperty lowPriceProperty() { return lowPrice; }
    public DoubleProperty volumeProperty() { return volume; }
    public DoubleProperty marketCapProperty() { return marketCap; }
    public DoubleProperty changePercentProperty() { return changePercent; }
    public ObjectProperty<LocalDateTime> lastUpdatedProperty() { return lastUpdated; }
    public StringProperty currencyProperty() { return currency; }
    public StringProperty exchangeProperty() { return exchange; }
    public DoubleProperty previousCloseProperty() { return previousClose; }
    public DoubleProperty fiftyTwoWeekHighProperty() { return fiftyTwoWeekHigh; }
    public DoubleProperty fiftyTwoWeekLowProperty() { return fiftyTwoWeekLow; }
    public DoubleProperty peRatioProperty() { return peRatio; }
    public DoubleProperty dividendYieldProperty() { return dividendYield; }

    // Setters
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) return;
        
        this.previousClose.set(this.currentPrice.get());
        this.currentPrice.set(newPrice);
        
        if (newPrice > this.highPrice.get()) {
            this.highPrice.set(newPrice);
        }
        if (newPrice < this.lowPrice.get()) {
            this.lowPrice.set(newPrice);
        }
        
        this.lastUpdated.set(LocalDateTime.now());
    }

    public void updateVolume(double newVolume) {
        if (newVolume >= 0) {
            this.volume.set(newVolume);
        }
    }

    public void updateMarketCap(double newMarketCap) {
        if (newMarketCap >= 0) {
            this.marketCap.set(newMarketCap);
        }
    }

    public void updateFiftyTwoWeekHigh(double newHigh) {
        if (newHigh > 0) {
            this.fiftyTwoWeekHigh.set(newHigh);
        }
    }

    public void updateFiftyTwoWeekLow(double newLow) {
        if (newLow > 0) {
            this.fiftyTwoWeekLow.set(newLow);
        }
    }

    public void updatePeRatio(double newPeRatio) {
        this.peRatio.set(newPeRatio);
    }

    public void updateDividendYield(double newDividendYield) {
        if (newDividendYield >= 0) {
            this.dividendYield.set(newDividendYield);
        }
    }

    public boolean isPriceUp() {
        return changePercent.get() > 0;
    }

    public boolean isPriceDown() {
        return changePercent.get() < 0;
    }

    public boolean isPriceUnchanged() {
        return changePercent.get() == 0;
    }

    public void setMarketCap(double marketCap) { this.marketCap.set(marketCap); }
    public void setFiftyTwoWeekHigh(double value) { this.fiftyTwoWeekHigh.set(value); }
    public void setFiftyTwoWeekLow(double value) { this.fiftyTwoWeekLow.set(value); }
    public void setPeRatio(double value) { this.peRatio.set(value); }
    public void setDividendYield(double value) { this.dividendYield.set(value); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketData that = (MarketData) o;
        return Objects.equals(symbol.get(), that.symbol.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol.get());
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f (%.2f%%)",
                symbol.get(), name.get(), currentPrice.get(), changePercent.get());
    }
} 