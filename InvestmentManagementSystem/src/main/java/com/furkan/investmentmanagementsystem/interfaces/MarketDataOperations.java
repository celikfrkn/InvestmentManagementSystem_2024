package com.furkan.investmentmanagementsystem.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for market data retrieval and analysis.
 * This allows for consistent market data handling across different data sources.
 */
public interface MarketDataOperations {
    /**
     * Gets real-time market data for a list of assets
     * @param assetSymbols List of asset symbols to get data for
     * @return Map of asset symbols to their current market data
     */
    Map<String, Map<String, Object>> getRealTimeData(List<String> assetSymbols);

    /**
     * Gets historical market data for an asset
     * @param assetSymbol The asset symbol
     * @param startDate Start of the period
     * @param endDate End of the period
     * @param interval The data interval (e.g., "1d", "1h", "15m")
     * @return List of market data points
     */
    List<Map<String, Object>> getHistoricalData(String assetSymbol, 
                                              LocalDateTime startDate, 
                                              LocalDateTime endDate, 
                                              String interval);

    /**
     * Gets market indices data
     * @param indexSymbols List of index symbols
     * @return Map of index symbols to their current values
     */
    Map<String, Double> getMarketIndices(List<String> indexSymbols);

    /**
     * Gets currency exchange rates
     * @param baseCurrency The base currency code
     * @param targetCurrencies List of target currency codes
     * @return Map of currency pairs to their exchange rates
     */
    Map<String, Double> getExchangeRates(String baseCurrency, List<String> targetCurrencies);

    /**
     * Gets market news for specified assets
     * @param assetSymbols List of asset symbols
     * @param maxResults Maximum number of news items to return
     * @return List of news items
     */
    List<Map<String, Object>> getMarketNews(List<String> assetSymbols, int maxResults);

    /**
     * Gets market sentiment for specified assets
     * @param assetSymbols List of asset symbols
     * @return Map of asset symbols to their sentiment scores
     */
    Map<String, Double> getMarketSentiment(List<String> assetSymbols);

    /**
     * Gets economic indicators
     * @param indicators List of indicator codes
     * @return Map of indicator codes to their values
     */
    Map<String, Object> getEconomicIndicators(List<String> indicators);

    /**
     * Gets market sector performance
     * @return Map of sector names to their performance metrics
     */
    Map<String, Map<String, Double>> getSectorPerformance();

    /**
     * Gets market trading hours
     * @param exchange The exchange code
     * @return Map of trading session types to their hours
     */
    Map<String, List<String>> getTradingHours(String exchange);

    /**
     * Gets market holidays
     * @param exchange The exchange code
     * @param year The year to get holidays for
     * @return List of holiday dates
     */
    List<LocalDateTime> getMarketHolidays(String exchange, int year);

    /**
     * Gets market depth for an asset
     * @param assetSymbol The asset symbol
     * @param depth The number of price levels to return
     * @return Map containing bid and ask orders
     */
    Map<String, List<Map<String, Object>>> getMarketDepth(String assetSymbol, int depth);

    /**
     * Gets market statistics
     * @param assetSymbol The asset symbol
     * @return Map of statistic names to their values
     */
    Map<String, Object> getMarketStatistics(String assetSymbol);

    /**
     * Gets market alerts
     * @param assetSymbols List of asset symbols
     * @return List of market alerts
     */
    List<Map<String, Object>> getMarketAlerts(List<String> assetSymbols);

    /**
     * Subscribes to real-time market data updates
     * @param assetSymbols List of asset symbols to subscribe to
     * @param callback The callback to handle updates
     */
    void subscribeToMarketData(List<String> assetSymbols, MarketDataCallback callback);

    /**
     * Unsubscribes from real-time market data updates
     * @param assetSymbols List of asset symbols to unsubscribe from
     */
    void unsubscribeFromMarketData(List<String> assetSymbols);

    /**
     * Interface for handling real-time market data updates
     */
    interface MarketDataCallback {
        void onMarketDataUpdate(String assetSymbol, Map<String, Object> data);
        void onError(String assetSymbol, String errorMessage);
    }
} 