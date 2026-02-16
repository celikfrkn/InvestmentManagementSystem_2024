package com.furkan.investmentmanagementsystem.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations that can be performed on assets.
 * This allows for consistent asset management across different types of assets.
 */
public interface AssetOperations {
    /**
     * Gets the current price of the asset
     * @return The current price in USD
     */
    double getCurrentPrice();

    /**
     * Gets the historical prices for a time period
     * @param startDate Start of the period
     * @param endDate End of the period
     * @return Map of timestamps to prices
     */
    Map<LocalDateTime, Double> getHistoricalPrices(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets the trading volume for a time period
     * @param startDate Start of the period
     * @param endDate End of the period
     * @return The total trading volume
     */
    double getTradingVolume(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets the volatility of the asset
     * @param period The number of days to calculate volatility for
     * @return The volatility as a percentage
     */
    double getVolatility(int period);

    /**
     * Gets the market cap of the asset
     * @return The market cap in USD
     */
    double getMarketCap();

    /**
     * Gets the dividend yield of the asset
     * @return The dividend yield as a percentage
     */
    double getDividendYield();

    /**
     * Gets the price-to-earnings ratio
     * @return The P/E ratio
     */
    double getPriceToEarningsRatio();

    /**
     * Gets the price-to-book ratio
     * @return The P/B ratio
     */
    double getPriceToBookRatio();

    /**
     * Gets the asset's sector
     * @return The sector name
     */
    String getSector();

    /**
     * Gets the asset's industry
     * @return The industry name
     */
    String getIndustry();

    /**
     * Gets the asset's country of origin
     * @return The country name
     */
    String getCountry();

    /**
     * Gets the asset's currency
     * @return The currency code
     */
    String getCurrency();

    /**
     * Gets the asset's exchange
     * @return The exchange name
     */
    String getExchange();

    /**
     * Gets the asset's trading hours
     * @return List of trading sessions
     */
    List<String> getTradingHours();

    /**
     * Checks if the asset is currently tradable
     * @return true if the asset can be traded
     */
    boolean isTradable();

    /**
     * Gets the minimum trade size
     * @return The minimum quantity that can be traded
     */
    double getMinimumTradeSize();

    /**
     * Gets the maximum trade size
     * @return The maximum quantity that can be traded
     */
    double getMaximumTradeSize();

    /**
     * Gets the trading fees
     * @return Map of fee types to their values
     */
    Map<String, Double> getTradingFees();
} 