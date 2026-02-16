package com.furkan.investmentmanagementsystem.interfaces;

import com.furkan.investmentmanagementsystem.models.Notification;
import java.util.ArrayList;

/**
 * Interface for entities that can receive and manage notifications.
 * This allows for consistent notification handling across different
 * types of notifiable entities (users, portfolios, etc.).
 */
public interface Notifiable {
    /**
     * Adds a notification to the entity
     * @param notification The notification to add
     */
    void addNotification(Notification notification);

    /**
     * Removes a notification from the entity
     * @param notificationId The ID of the notification to remove
     */
    void removeNotification(String notificationId);

    /**
     * Gets all notifications for the entity
     * @return List of notifications
     */
    ArrayList<Notification> getNotifications();

    /**
     * Marks a notification as read
     * @param notificationId The ID of the notification to mark as read
     */
    void markNotificationAsRead(String notificationId);

    /**
     * Gets the number of unread notifications
     * @return The count of unread notifications
     */
    int getUnreadNotificationCount();
} 