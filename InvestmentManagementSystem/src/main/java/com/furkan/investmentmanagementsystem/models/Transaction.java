package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a transaction in a portfolio.
 */
public class Transaction {
    private final StringProperty id;
    private final StringProperty assetName;
    private final StringProperty type;
    private final DoubleProperty quantity;
    private final DoubleProperty price;
    private final DoubleProperty totalAmount;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final StringProperty status;
    private final StringProperty notes;
    private final StringProperty color;

    public enum TransactionType {
        BUY("Buy", "green"),
        SELL("Sell", "red"),
        DIVIDEND("Dividend", "blue"),
        INTEREST("Interest", "purple"),
        TRANSFER("Transfer", "orange");

        private final String displayName;
        private final String color;

        TransactionType(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public Transaction(String assetName, TransactionType type, double quantity, double price) {
        this.id = new SimpleStringProperty(java.util.UUID.randomUUID().toString());
        this.assetName = new SimpleStringProperty(assetName);
        this.type = new SimpleStringProperty(type.getDisplayName());
        this.quantity = new SimpleDoubleProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.totalAmount = new SimpleDoubleProperty(0);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.status = new SimpleStringProperty(TransactionStatus.COMPLETED.name());
        this.notes = new SimpleStringProperty("");
        this.color = new SimpleStringProperty(type.getColor());

        // Bind calculated properties
        totalAmount.bind(quantityProperty().multiply(priceProperty()));
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getAssetName() {
        return assetName.get();
    }

    public StringProperty assetNameProperty() {
        return assetName;
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

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp.get();
    }

    public ObjectProperty<LocalDateTime> timestampProperty() {
        return timestamp;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status.set(status.name());
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public String getColor() {
        return color.get();
    }

    public StringProperty colorProperty() {
        return color;
    }

    public TransactionType getTransactionType() {
        return TransactionType.valueOf(type.get().toUpperCase());
    }

    public String getDisplayType() {
        return type.get();
    }

    @Override
    public String toString() {
        return String.format("%s %s - %s: %.2f @ %.2f = %.2f (%s)",
            getTimestamp(), getType(), getAssetName(), getQuantity(), getPrice(), getTotalAmount(), getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id.get(), that.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }
} 