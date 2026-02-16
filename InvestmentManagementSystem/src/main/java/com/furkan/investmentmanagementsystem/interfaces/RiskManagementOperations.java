package com.furkan.investmentmanagementsystem.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for risk management and analysis.
 * This allows for consistent risk assessment across different portfolios and assets.
 */
public interface RiskManagementOperations {
    /**
     * Calculates the Value at Risk (VaR) for a portfolio
     * @param confidenceLevel The confidence level (e.g., 0.95 for 95%)
     * @param timeHorizon The time horizon in days
     * @return The VaR value
     */
    double calculateValueAtRisk(double confidenceLevel, int timeHorizon);

    /**
     * Calculates the Expected Shortfall (CVaR) for a portfolio
     * @param confidenceLevel The confidence level (e.g., 0.95 for 95%)
     * @param timeHorizon The time horizon in days
     * @return The CVaR value
     */
    double calculateExpectedShortfall(double confidenceLevel, int timeHorizon);

    /**
     * Calculates the portfolio's beta
     * @param marketIndex The market index symbol
     * @param lookbackPeriod The lookback period in days
     * @return The beta value
     */
    double calculateBeta(String marketIndex, int lookbackPeriod);

    /**
     * Calculates the portfolio's Sharpe ratio
     * @param riskFreeRate The risk-free rate
     * @param lookbackPeriod The lookback period in days
     * @return The Sharpe ratio
     */
    double calculateSharpeRatio(double riskFreeRate, int lookbackPeriod);

    /**
     * Calculates the portfolio's Sortino ratio
     * @param riskFreeRate The risk-free rate
     * @param lookbackPeriod The lookback period in days
     * @return The Sortino ratio
     */
    double calculateSortinoRatio(double riskFreeRate, int lookbackPeriod);

    /**
     * Calculates the portfolio's maximum drawdown
     * @param lookbackPeriod The lookback period in days
     * @return The maximum drawdown as a percentage
     */
    double calculateMaximumDrawdown(int lookbackPeriod);

    /**
     * Calculates the portfolio's correlation with a market index
     * @param marketIndex The market index symbol
     * @param lookbackPeriod The lookback period in days
     * @return The correlation coefficient
     */
    double calculateCorrelation(String marketIndex, int lookbackPeriod);

    /**
     * Performs stress testing on the portfolio
     * @param scenarios List of stress scenarios to test
     * @return Map of scenario names to their impact on portfolio value
     */
    Map<String, Double> performStressTesting(List<Map<String, Object>> scenarios);

    /**
     * Calculates the portfolio's risk metrics
     * @return Map of risk metric names to their values
     */
    Map<String, Double> calculateRiskMetrics();

    /**
     * Gets the portfolio's risk limits
     * @return Map of risk limit types to their values
     */
    Map<String, Double> getRiskLimits();

    /**
     * Sets risk limits for the portfolio
     * @param limits Map of risk limit types to their values
     */
    void setRiskLimits(Map<String, Double> limits);

    /**
     * Checks if the portfolio is within risk limits
     * @return true if all risk limits are satisfied
     */
    boolean isWithinRiskLimits();

    /**
     * Gets risk limit violations
     * @return List of risk limit violations
     */
    List<Map<String, Object>> getRiskLimitViolations();

    /**
     * Calculates the portfolio's risk contribution
     * @return Map of asset symbols to their risk contribution
     */
    Map<String, Double> calculateRiskContribution();

    /**
     * Calculates the portfolio's risk-adjusted return
     * @param riskMeasure The risk measure to use (e.g., "VaR", "CVaR")
     * @return The risk-adjusted return
     */
    double calculateRiskAdjustedReturn(String riskMeasure);

    /**
     * Generates a risk report
     * @param startDate Start of the reporting period
     * @param endDate End of the reporting period
     * @return Map containing risk report data
     */
    Map<String, Object> generateRiskReport(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculates the portfolio's tracking error
     * @param benchmark The benchmark symbol
     * @param lookbackPeriod The lookback period in days
     * @return The tracking error
     */
    double calculateTrackingError(String benchmark, int lookbackPeriod);

    /**
     * Calculates the portfolio's information ratio
     * @param benchmark The benchmark symbol
     * @param lookbackPeriod The lookback period in days
     * @return The information ratio
     */
    double calculateInformationRatio(String benchmark, int lookbackPeriod);

    /**
     * Calculates the portfolio's downside deviation
     * @param minimumAcceptableReturn The minimum acceptable return
     * @param lookbackPeriod The lookback period in days
     * @return The downside deviation
     */
    double calculateDownsideDeviation(double minimumAcceptableReturn, int lookbackPeriod);
} 