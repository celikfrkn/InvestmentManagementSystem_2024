package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a notification in the system.
 * Notifications can be of different types and can be marked as read/unread.
 */
public class Notification {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty message;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final StringProperty recipientId;
    private final StringProperty senderId;
    private final StringProperty type;
    private final BooleanProperty isRead;
    private final StringProperty actionUrl;
    private final StringProperty priority;
    private final BooleanProperty isDeleted;
    private final IntegerProperty intId;
    private final IntegerProperty intRecipientId;
    private final ObjectProperty<Integer> intSenderId;
    private final ObjectProperty<NotificationType> typeEnum;
    private final ObjectProperty<NotificationPriority> priorityEnum;
    private final String entityId; // ID of the entity this notification is about

    public enum NotificationType {
        PORTFOLIO_UPDATE,
        PRICE_ALERT,
        SYSTEM_MESSAGE,
        REPORT_AVAILABLE,
        RECOMMENDATION
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public Notification(String message, NotificationType type, String entityId) {
        this.intId = new SimpleIntegerProperty(0);
        this.intRecipientId = new SimpleIntegerProperty(0);
        this.intSenderId = new SimpleObjectProperty<>(null);
        this.typeEnum = new SimpleObjectProperty<>(type);
        this.priorityEnum = new SimpleObjectProperty<>(NotificationPriority.LOW);
        this.id = new SimpleStringProperty(UUID.randomUUID().toString());
        this.title = new SimpleStringProperty("");
        this.message = new SimpleStringProperty(message);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.recipientId = new SimpleStringProperty("");
        this.senderId = new SimpleStringProperty("SYSTEM");
        this.type = new SimpleStringProperty(type.name());
        this.isRead = new SimpleBooleanProperty(false);
        this.actionUrl = new SimpleStringProperty("");
        this.priority = new SimpleStringProperty(NotificationPriority.LOW.name());
        this.isDeleted = new SimpleBooleanProperty(false);
        this.entityId = entityId;
    }

    public Notification(String title, String message, String recipientId, 
                       NotificationType type, NotificationPriority priority) {
        this.intId = new SimpleIntegerProperty(0);
        this.intRecipientId = new SimpleIntegerProperty(0);
        this.intSenderId = new SimpleObjectProperty<>(null);
        this.typeEnum = new SimpleObjectProperty<>(type);
        this.priorityEnum = new SimpleObjectProperty<>(priority);
        this.id = new SimpleStringProperty(UUID.randomUUID().toString());
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.recipientId = new SimpleStringProperty(recipientId);
        this.senderId = new SimpleStringProperty("SYSTEM");
        this.type = new SimpleStringProperty(type.name());
        this.isRead = new SimpleBooleanProperty(false);
        this.actionUrl = new SimpleStringProperty("");
        this.priority = new SimpleStringProperty(priority.name());
        this.isDeleted = new SimpleBooleanProperty(false);
        this.entityId = "";
    }

    public Notification(String title, String message, String recipientId, String senderId,
                       NotificationType type, NotificationPriority priority) {
        this.intId = new SimpleIntegerProperty(0);
        this.intRecipientId = new SimpleIntegerProperty(0);
        this.intSenderId = new SimpleObjectProperty<>(null);
        this.typeEnum = new SimpleObjectProperty<>(type);
        this.priorityEnum = new SimpleObjectProperty<>(priority);
        this.id = new SimpleStringProperty(UUID.randomUUID().toString());
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.recipientId = new SimpleStringProperty(recipientId);
        this.senderId = new SimpleStringProperty(senderId);
        this.type = new SimpleStringProperty(type.name());
        this.isRead = new SimpleBooleanProperty(false);
        this.actionUrl = new SimpleStringProperty("");
        this.priority = new SimpleStringProperty(priority.name());
        this.isDeleted = new SimpleBooleanProperty(false);
        this.entityId = "";
    }

    public Notification(int id, String title, String message, LocalDateTime timestamp, int recipientId, Integer senderId, NotificationType type, NotificationPriority priority) {
        this.intId = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.intRecipientId = new SimpleIntegerProperty(recipientId);
        this.intSenderId = new SimpleObjectProperty<>(senderId);
        this.typeEnum = new SimpleObjectProperty<>(type);
        this.priorityEnum = new SimpleObjectProperty<>(priority);
        this.id = new SimpleStringProperty(String.valueOf(id));
        this.recipientId = new SimpleStringProperty(String.valueOf(recipientId));
        this.senderId = new SimpleStringProperty(senderId == null ? "" : String.valueOf(senderId));
        this.type = new SimpleStringProperty(type.name());
        this.isRead = new SimpleBooleanProperty(false);
        this.actionUrl = new SimpleStringProperty("");
        this.priority = new SimpleStringProperty(priority.name());
        this.isDeleted = new SimpleBooleanProperty(false);
        this.entityId = "";
    }

    // Getters
    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getMessage() { return message.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public String getRecipientId() { return recipientId.get(); }
    public String getSenderId() { return senderId.get(); }
    public String getType() { return type.get(); }
    public boolean isRead() { return isRead.get(); }
    public String getActionUrl() { return actionUrl.get(); }
    public String getPriority() { return priority.get(); }
    public boolean isDeleted() { return isDeleted.get(); }
    public int getIntId() { return intId.get(); }
    public int getIntRecipientId() { return intRecipientId.get(); }
    public Integer getIntSenderId() { return intSenderId.get(); }
    public NotificationType getTypeEnum() { return typeEnum.get(); }
    public NotificationPriority getPriorityEnum() { return priorityEnum.get(); }
    public String getEntityId() { return entityId; }

    // Property getters for JavaFX binding
    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty messageProperty() { return message; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public StringProperty recipientIdProperty() { return recipientId; }
    public StringProperty senderIdProperty() { return senderId; }
    public StringProperty typeProperty() { return type; }
    public BooleanProperty isReadProperty() { return isRead; }
    public StringProperty actionUrlProperty() { return actionUrl; }
    public StringProperty priorityProperty() { return priority; }
    public BooleanProperty isDeletedProperty() { return isDeleted; }
    public IntegerProperty intIdProperty() { return intId; }
    public IntegerProperty intRecipientIdProperty() { return intRecipientId; }
    public ObjectProperty<Integer> intSenderIdProperty() { return intSenderId; }
    public ObjectProperty<NotificationType> typeEnumProperty() { return typeEnum; }
    public ObjectProperty<NotificationPriority> priorityEnumProperty() { return priorityEnum; }

    // Setters
    public void setActionUrl(String actionUrl) {
        this.actionUrl.set(actionUrl);
    }

    public void markAsRead() {
        this.isRead.set(true);
    }

    public void markAsUnread() {
        this.isRead.set(false);
    }

    public void delete() {
        this.isDeleted.set(true);
    }

    public void restore() {
        this.isDeleted.set(false);
    }

    public boolean isUrgent() {
        return priority.get().equals(NotificationPriority.URGENT.name());
    }

    public boolean isHighPriority() {
        return priority.get().equals(NotificationPriority.HIGH.name()) || isUrgent();
    }

    public void setRead(boolean read) { this.isRead.set(read); }
    public void setDeleted(boolean deleted) { this.isDeleted.set(deleted); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id.get(), that.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", 
            type.get(), message.get(), timestamp.get().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
} 