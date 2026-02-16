package com.furkan.investmentmanagementsystem.models;

import java.util.Date;

/**
 * Represents an analyst's report for a specific asset.
 * Encapsulates the analyst's outlook and detailed analysis.
 */
public class AnalystReport {
    private final String analystEmail;
    private final String assetName;
    private final String outlook;
    private final String reportText;
    private final Date timestamp;

    public AnalystReport(String analystEmail, String assetName, String outlook, String reportText) {
        this.analystEmail = analystEmail;
        this.assetName = assetName;
        this.outlook = outlook;
        this.reportText = reportText;
        this.timestamp = new Date();
    }

    // Getters
    public String getAnalystEmail() { return analystEmail; }
    public String getAssetName() { return assetName; }
    public String getOutlook() { return outlook; }
    public String getReportText() { return reportText; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s (%s)\nAsset: %s | Outlook: %s\n%s",
                analystEmail, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp),
                assetName, outlook, reportText);
    }
} 