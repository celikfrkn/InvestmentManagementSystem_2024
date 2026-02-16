package com.furkan.investmentmanagementsystem.services;

import com.furkan.investmentmanagementsystem.models.PortfolioItem;
import javafx.collections.ObservableList;
import java.util.Optional;

/**
 * Service interface for managing investment portfolios.
 * Defines the contract for portfolio-related operations.
 */
public interface PortfolioService {
    /**
     * Retrieves the portfolio items for a specific user.
     * @param userEmail The email of the user
     * @return Observable list of portfolio items
     */
    ObservableList<PortfolioItem> getUserPortfolio(String userEmail);

    /**
     * Adds a new position to a user's portfolio.
     * @param userEmail The email of the user
     * @param item The portfolio item to add
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the position is invalid
     */
    boolean addPosition(String userEmail, PortfolioItem item);

    /**
     * Updates an existing position in a user's portfolio.
     * @param userEmail The email of the user
     * @param asset The asset symbol to update
     * @param newQuantity The new quantity
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the position doesn't exist or quantity is invalid
     */
    boolean updatePosition(String userEmail, String asset, int newQuantity);

    /**
     * Removes a position from a user's portfolio.
     * @param userEmail The email of the user
     * @param asset The asset symbol to remove
     * @return true if the operation was successful
     */
    boolean removePosition(String userEmail, String asset);

    /**
     * Finds a specific position in a user's portfolio.
     * @param userEmail The email of the user
     * @param asset The asset symbol to find
     * @return Optional containing the portfolio item if found
     */
    Optional<PortfolioItem> findPosition(String userEmail, String asset);

    /**
     * Calculates the total value of a user's portfolio.
     * @param userEmail The email of the user
     * @return The total portfolio value
     */
    double getTotalPortfolioValue(String userEmail);

    /**
     * Calculates the total profit/loss of a user's portfolio.
     * @param userEmail The email of the user
     * @return The total profit/loss amount
     */
    double getTotalProfitLoss(String userEmail);

    /**
     * Updates all market prices in a user's portfolio.
     * @param userEmail The email of the user
     */
    void refreshPortfolioPrices(String userEmail);
} 