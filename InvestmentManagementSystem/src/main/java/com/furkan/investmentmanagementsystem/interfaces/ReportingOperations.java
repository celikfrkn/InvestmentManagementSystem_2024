package com.furkan.investmentmanagementsystem.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for generating and managing reports.
 * This allows for consistent report generation across different types of reports.
 */
public interface ReportingOperations {
    /**
     * Generates a portfolio performance report
     * @param startDate Start of the reporting period
     * @param endDate End of the reporting period
     * @param includeTransactions Whether to include transaction details
     * @return Map containing performance report data
     */
    Map<String, Object> generatePerformanceReport(LocalDateTime startDate, 
                                                LocalDateTime endDate, 
                                                boolean includeTransactions);

    /**
     * Generates an asset allocation report
     * @param groupBy The grouping criteria (e.g., "assetType", "sector", "country")
     * @return Map containing allocation report data
     */
    Map<String, Object> generateAllocationReport(String groupBy);

    /**
     * Generates a transaction report
     * @param startDate Start of the reporting period
     * @param endDate End of the reporting period
     * @param filters Map of filter criteria
     * @return Map containing transaction report data
     */
    Map<String, Object> generateTransactionReport(LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                Map<String, Object> filters);

    /**
     * Generates a tax report
     * @param year The tax year
     * @param taxJurisdiction The tax jurisdiction
     * @return Map containing tax report data
     */
    Map<String, Object> generateTaxReport(int year, String taxJurisdiction);

    /**
     * Generates a risk report
     * @param startDate Start of the reporting period
     * @param endDate End of the reporting period
     * @param riskMetrics List of risk metrics to include
     * @return Map containing risk report data
     */
    Map<String, Object> generateRiskReport(LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         List<String> riskMetrics);

    /**
     * Generates a compliance report
     * @param startDate Start of the reporting period
     * @param endDate End of the reporting period
     * @param regulations List of regulations to check
     * @return Map containing compliance report data
     */
    Map<String, Object> generateComplianceReport(LocalDateTime startDate,
                                               LocalDateTime endDate,
                                               List<String> regulations);

    /**
     * Generates a custom report
     * @param reportType The type of report to generate
     * @param parameters Map of report parameters
     * @return Map containing custom report data
     */
    Map<String, Object> generateCustomReport(String reportType, Map<String, Object> parameters);

    /**
     * Exports a report to a file
     * @param reportData The report data to export
     * @param format The export format (e.g., "PDF", "CSV", "EXCEL")
     * @param filePath The path to save the file
     * @return true if export was successful
     */
    boolean exportReport(Map<String, Object> reportData, String format, String filePath);

    /**
     * Schedules a report generation
     * @param reportType The type of report to schedule
     * @param schedule The schedule parameters
     * @param recipients List of recipient email addresses
     * @return The schedule ID
     */
    String scheduleReport(String reportType, Map<String, Object> schedule, List<String> recipients);

    /**
     * Cancels a scheduled report
     * @param scheduleId The schedule ID to cancel
     * @return true if cancellation was successful
     */
    boolean cancelScheduledReport(String scheduleId);

    /**
     * Gets the list of scheduled reports
     * @return List of scheduled report details
     */
    List<Map<String, Object>> getScheduledReports();

    /**
     * Gets the report templates
     * @return Map of report type to template details
     */
    Map<String, Object> getReportTemplates();

    /**
     * Saves a report template
     * @param reportType The type of report
     * @param template The template details
     * @return true if save was successful
     */
    boolean saveReportTemplate(String reportType, Map<String, Object> template);

    /**
     * Deletes a report template
     * @param reportType The type of report
     * @return true if deletion was successful
     */
    boolean deleteReportTemplate(String reportType);

    /**
     * Gets the report history
     * @param startDate Start of the history period
     * @param endDate End of the history period
     * @return List of generated report details
     */
    List<Map<String, Object>> getReportHistory(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Archives a report
     * @param reportId The report ID to archive
     * @return true if archiving was successful
     */
    boolean archiveReport(String reportId);

    /**
     * Restores an archived report
     * @param reportId The report ID to restore
     * @return true if restoration was successful
     */
    boolean restoreReport(String reportId);
} 