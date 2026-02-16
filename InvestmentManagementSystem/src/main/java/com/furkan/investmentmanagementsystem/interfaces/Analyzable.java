package com.furkan.investmentmanagementsystem.interfaces;

import java.util.Map;

/**
 * Interface for entities that can be analyzed.
 * This allows for consistent analysis operations across different
 * types of analyzable entities (portfolios, assets, etc.).
 */
public interface Analyzable {
    /**
     * Performs analysis on the entity
     * @return Map of analysis metrics and their values
     */
    Map<String, Double> analyze();

    /**
     * Gets the risk level of the entity
     * @return Risk level as a string (e.g., "LOW", "MEDIUM", "HIGH")
     */
    String getRiskLevel();

    /**
     * Gets the performance metrics of the entity
     * @return Map of performance metrics and their values
     */
    Map<String, Double> getPerformanceMetrics();

    /**
     * Validates if the entity meets certain criteria
     * @param criteria The criteria to validate against
     * @return true if the entity meets the criteria, false otherwise
     */
    boolean validateCriteria(Map<String, Object> criteria);
} 