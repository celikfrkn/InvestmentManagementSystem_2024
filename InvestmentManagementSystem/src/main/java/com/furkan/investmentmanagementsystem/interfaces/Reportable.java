package com.furkan.investmentmanagementsystem.interfaces;

import java.util.List;

/**
 * Interface for entities that can generate and manage reports.
 * This allows for polymorphic handling of different types of reports
 * across the system.
 */
public interface Reportable {
    /**
     * Generates a report for the specified entity
     * @param entityId The ID of the entity to generate a report for
     * @return A formatted report string
     */
    String generateReport(String entityId);

    /**
     * Gets all reports associated with this entity
     * @return List of report strings
     */
    List<String> getAllReports();

    /**
     * Adds a new report
     * @param report The report content to add
     */
    void addReport(String report);

    /**
     * Removes a report
     * @param reportId The ID of the report to remove
     */
    void removeReport(String reportId);
} 