package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Comment {
    private final StringProperty id;
    private final StringProperty content;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final StringProperty authorId;
    private final StringProperty parentId;
    private final StringProperty targetType;
    private final StringProperty targetId;
    private final BooleanProperty isEdited;
    private final ObjectProperty<LocalDateTime> lastEditTime;
    private final IntegerProperty likes;
    private final BooleanProperty isDeleted;
    private final IntegerProperty intId;
    private final IntegerProperty intAuthorId;
    private final ObjectProperty<Integer> intParentId;
    private final ObjectProperty<TargetType> targetTypeEnum;

    public enum TargetType {
        REPORT,
        PORTFOLIO_ITEM,
        TRANSACTION,
        USER
    }

    public Comment(String content, String authorId, TargetType targetType, String targetId) {
        this.intId = new SimpleIntegerProperty(0);
        this.intAuthorId = new SimpleIntegerProperty(0);
        this.intParentId = new SimpleObjectProperty<>(null);
        this.targetTypeEnum = new SimpleObjectProperty<>(targetType);
        this.id = new SimpleStringProperty(UUID.randomUUID().toString());
        this.content = new SimpleStringProperty(content);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.authorId = new SimpleStringProperty(authorId);
        this.parentId = new SimpleStringProperty("");
        this.targetType = new SimpleStringProperty(targetType.name());
        this.targetId = new SimpleStringProperty(targetId);
        this.isEdited = new SimpleBooleanProperty(false);
        this.lastEditTime = new SimpleObjectProperty<>(null);
        this.likes = new SimpleIntegerProperty(0);
        this.isDeleted = new SimpleBooleanProperty(false);
    }

    public Comment(String content, String authorId, TargetType targetType, String targetId, String parentId) {
        this.intId = new SimpleIntegerProperty(0);
        this.intAuthorId = new SimpleIntegerProperty(0);
        this.intParentId = new SimpleObjectProperty<>(null);
        this.targetTypeEnum = new SimpleObjectProperty<>(targetType);
        this.id = new SimpleStringProperty(UUID.randomUUID().toString());
        this.content = new SimpleStringProperty(content);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.authorId = new SimpleStringProperty(authorId);
        this.parentId = new SimpleStringProperty(parentId);
        this.targetType = new SimpleStringProperty(targetType.name());
        this.targetId = new SimpleStringProperty(targetId);
        this.isEdited = new SimpleBooleanProperty(false);
        this.lastEditTime = new SimpleObjectProperty<>(null);
        this.likes = new SimpleIntegerProperty(0);
        this.isDeleted = new SimpleBooleanProperty(false);
    }

    public Comment(int id, String content, LocalDateTime timestamp, int authorId, Integer parentId, TargetType targetType, String targetId) {
        this.intId = new SimpleIntegerProperty(id);
        this.content = new SimpleStringProperty(content);
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.intAuthorId = new SimpleIntegerProperty(authorId);
        this.intParentId = new SimpleObjectProperty<>(parentId);
        this.targetTypeEnum = new SimpleObjectProperty<>(targetType);
        this.targetId = new SimpleStringProperty(targetId);
        this.id = new SimpleStringProperty(String.valueOf(id));
        this.authorId = new SimpleStringProperty(String.valueOf(authorId));
        this.parentId = new SimpleStringProperty(parentId == null ? "" : String.valueOf(parentId));
        this.targetType = new SimpleStringProperty(targetType.name());
        this.isEdited = new SimpleBooleanProperty(false);
        this.lastEditTime = new SimpleObjectProperty<>(null);
        this.likes = new SimpleIntegerProperty(0);
        this.isDeleted = new SimpleBooleanProperty(false);
    }

    // Getters
    public String getId() { return id.get(); }
    public String getContent() { return content.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public String getAuthorId() { return authorId.get(); }
    public String getParentId() { return parentId.get(); }
    public String getTargetType() { return targetType.get(); }
    public String getTargetId() { return targetId.get(); }
    public boolean isEdited() { return isEdited.get(); }
    public LocalDateTime getLastEditTime() { return lastEditTime.get(); }
    public int getLikes() { return likes.get(); }
    public boolean isDeleted() { return isDeleted.get(); }
    public int getIntId() { return intId.get(); }
    public int getIntAuthorId() { return intAuthorId.get(); }
    public Integer getIntParentId() { return intParentId.get(); }
    public TargetType getTargetTypeEnum() { return targetTypeEnum.get(); }

    // Property getters for JavaFX binding
    public StringProperty idProperty() { return id; }
    public StringProperty contentProperty() { return content; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public StringProperty authorIdProperty() { return authorId; }
    public StringProperty parentIdProperty() { return parentId; }
    public StringProperty targetTypeProperty() { return targetType; }
    public StringProperty targetIdProperty() { return targetId; }
    public BooleanProperty isEditedProperty() { return isEdited; }
    public ObjectProperty<LocalDateTime> lastEditTimeProperty() { return lastEditTime; }
    public IntegerProperty likesProperty() { return likes; }
    public BooleanProperty isDeletedProperty() { return isDeleted; }
    public IntegerProperty intIdProperty() { return intId; }
    public IntegerProperty intAuthorIdProperty() { return intAuthorId; }
    public ObjectProperty<Integer> intParentIdProperty() { return intParentId; }
    public ObjectProperty<TargetType> targetTypeEnumProperty() { return targetTypeEnum; }

    // Setters
    public void setContent(String content) {
        if (!this.content.get().equals(content)) {
            this.content.set(content);
            this.isEdited.set(true);
            this.lastEditTime.set(LocalDateTime.now());
        }
    }

    public void setParentId(String parentId) {
        this.parentId.set(parentId);
    }

    public void like() {
        this.likes.set(this.likes.get() + 1);
    }

    public void unlike() {
        int currentLikes = this.likes.get();
        if (currentLikes > 0) {
            this.likes.set(currentLikes - 1);
        }
    }

    public void delete() {
        this.isDeleted.set(true);
        this.content.set("[Deleted]");
    }

    public void restore() {
        this.isDeleted.set(false);
    }

    public boolean isReply() {
        return !parentId.get().isEmpty();
    }

    public void setEdited(boolean edited) { this.isEdited.set(edited); }
    public void setLastEditTime(LocalDateTime time) { this.lastEditTime.set(time); }
    public void setLikes(int likes) { this.likes.set(likes); }
    public void setDeleted(boolean deleted) { this.isDeleted.set(deleted); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id.get(), comment.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        return String.format("Comment by %s on %s: %s",
                authorId.get(), targetType.get(), 
                isDeleted.get() ? "[Deleted]" : content.get());
    }
} 