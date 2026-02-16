package com.furkan.investmentmanagementsystem.models;

import java.util.Date;

/**
 * Represents an economic forecast made by an economist.
 * Encapsulates forecast data for interest rates, inflation, and currency rates.
 */
public class Forecast {
    private final String economist;
    private final String interestRate;
    private final String inflation;
    private final String usdTry;
    private final String comment;
    private final Date timestamp;

    public Forecast(String economist, String interestRate, String inflation, String usdTry, String comment) {
        this.economist = economist;
        this.interestRate = interestRate;
        this.inflation = inflation;
        this.usdTry = usdTry;
        this.comment = comment;
        this.timestamp = new Date();
    }

    // Getters
    public String getEconomist() { return economist; }
    public String getInterestRate() { return interestRate; }
    public String getInflation() { return inflation; }
    public String getUsdTry() { return usdTry; }
    public String getComment() { return comment; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s | Interest: %s, Inflation: %s, USD/TRY: %s\nComment: %s",
                economist, interestRate, inflation, usdTry, comment);
    }
} 