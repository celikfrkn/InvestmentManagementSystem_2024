package com.furkan.investmentmanagementsystem.interfaces;

import com.furkan.investmentmanagementsystem.models.PortfolioItem;
import com.furkan.investmentmanagementsystem.models.Transaction;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations that can be performed on a portfolio.
 * This allows for consistent portfolio management across different types of portfolios.
 */
public interface PortfolioOperations {
    /**
     * Adds a new item to the portfolio
     * @param item The portfolio item to add
     * @return true if the item was added successfully
     */
    boolean addItem(PortfolioItem item);

    /**
     * Removes an item from the portfolio
     * @param assetName The name of the asset to remove
     * @return true if the item was removed successfully
     */
    boolean removeItem(String assetName);

    /**
     * Updates the quantity of an existing item
     * @param assetName The name of the asset to update
     * @param newQuantity The new quantity
     * @return true if the update was successful
     */
    boolean updateItemQuantity(String assetName, int newQuantity);

    /**
     * Updates the price of an existing item
     * @param assetName The name of the asset to update
     * @param newPrice The new price in USD
     * @return true if the update was successful
     */
    boolean updateItemPrice(String assetName, double newPrice);

    /**
     * Executes a transaction on the portfolio
     * @param transaction The transaction to execute
     * @return true if the transaction was executed successfully
     */
    boolean executeTransaction(Transaction transaction);

    /**
     * Gets the current value of the portfolio
     * @return The total value in USD
     */
    double getTotalValue();

    /**
     * Gets the distribution of assets by type
     * @return Map of asset types to their total value
     */
    Map<String, Double> getAssetTypeDistribution();

    /**
     * Gets all transactions for a specific asset
     * @param assetName The name of the asset
     * @return List of transactions
     */
    List<Transaction> getAssetTransactions(String assetName);

    /**
     * Gets the performance metrics for the portfolio
     * @return Map of metric names to their values
     */
    Map<String, Double> getPerformanceMetrics();

    /**
     * Rebalances the portfolio according to target allocations
     * @param targetAllocations Map of asset names to target percentages
     * @return true if rebalancing was successful
     */
    boolean rebalance(Map<String, Double> targetAllocations);
} 