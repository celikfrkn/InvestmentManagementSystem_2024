package com.furkan.investmentmanagementsystem.models;

import com.furkan.investmentmanagementsystem.interfaces.Notifiable;
import com.furkan.investmentmanagementsystem.interfaces.Reportable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Abstract base class for all user types in the system.
 * Implements common interfaces and provides shared functionality.
 */
public abstract class AbstractUser implements Notifiable, Reportable {
    private final int id;
    private final String email;
    private final String role;
    private String password;
    private String firstName;
    private String lastName;
    private final ObservableList<Notification> notifications;
    private final List<String> reports;
    private LocalDateTime lastLogin;
    private boolean isActive;

    protected AbstractUser(int id, String email, String role, String password) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.password = password;
        this.notifications = FXCollections.observableArrayList();
        this.reports = new ArrayList<>();
        this.lastLogin = LocalDateTime.now();
        this.isActive = true;
    }

    // Common getters and setters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    protected void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public boolean isActive() { return isActive; }

    // User management methods
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Notifiable interface implementation
    @Override
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    @Override
    public void removeNotification(String notificationId) {
        notifications.removeIf(n -> n.getId().equals(notificationId));
    }

    @Override
    public ArrayList<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    @Override
    public void markNotificationAsRead(String notificationId) {
        notifications.stream()
            .filter(n -> n.getId().equals(notificationId))
            .findFirst()
            .ifPresent(Notification::markAsRead);
    }

    @Override
    public int getUnreadNotificationCount() {
        return (int) notifications.stream()
            .filter(n -> !n.isRead())
            .count();
    }

    // Reportable interface implementation
    @Override
    public String generateReport(String entityId) {
        return String.format("User Report for %s\n" +
                           "Name: %s\n" +
                           "Role: %s\n" +
                           "Last Login: %s\n" +
                           "Status: %s",
                           entityId, getFullName(), role, lastLogin, 
                           isActive ? "Active" : "Inactive");
    }

    @Override
    public List<String> getAllReports() {
        return new ArrayList<>(reports);
    }

    @Override
    public void addReport(String report) {
        reports.add(report);
    }

    @Override
    public void removeReport(String reportId) {
        reports.removeIf(r -> r.contains("Report ID: " + reportId));
    }

    // Abstract methods that must be implemented by concrete classes
    public abstract String getDashboardType();
    public abstract boolean validateUser();

    @Override
    public String toString() {
        return String.format("%s (ID: %d, Email: %s, Role: %s)",
            getFullName(), id, email, role);
    }
} 