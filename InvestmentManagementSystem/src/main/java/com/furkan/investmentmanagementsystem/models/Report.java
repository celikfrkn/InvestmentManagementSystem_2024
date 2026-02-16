package com.furkan.investmentmanagementsystem.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty content;
    private final ObjectProperty<LocalDateTime> creationDate;
    private final ObjectProperty<LocalDateTime> lastModifiedDate;
    private final StringProperty authorId;
    private final StringProperty reportType;
    private final StringProperty status;
    private final StringProperty targetAudience;
    private final BooleanProperty isPublic;
    private final StringProperty tags;
    private final IntegerProperty intId;
    private final IntegerProperty intAuthorId;
    private final ObjectProperty<ReportType> reportTypeEnum;
    private final List<String> tagsList = new ArrayList<>();

    public enum ReportType {
        MARKET_ANALYSIS,
        PORTFOLIO_PERFORMANCE,
        RISK_ASSESSMENT,
        ECONOMIC_FORECAST,
        ASSET_RECOMMENDATION
    }

    public enum ReportStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }

    public Report(int id, String title, String content, LocalDateTime creationDate, int authorId, ReportType type) {
        this.intId = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.creationDate = new SimpleObjectProperty<>(creationDate);
        this.lastModifiedDate = new SimpleObjectProperty<>(creationDate);
        this.intAuthorId = new SimpleIntegerProperty(authorId);
        this.reportTypeEnum = new SimpleObjectProperty<>(type);
        this.id = new SimpleStringProperty(String.valueOf(id));
        this.authorId = new SimpleStringProperty(String.valueOf(authorId));
        this.reportType = new SimpleStringProperty(type.name());
        this.status = new SimpleStringProperty(ReportStatus.DRAFT.name());
        this.targetAudience = new SimpleStringProperty("");
        this.isPublic = new SimpleBooleanProperty(false);
        this.tags = new SimpleStringProperty("");

        // Update lastModifiedDate when content changes
        this.content.addListener((obs, oldVal, newVal) -> 
            lastModifiedDate.set(LocalDateTime.now()));
    }

    // Getters
    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getContent() { return content.get(); }
    public LocalDateTime getCreationDate() { return creationDate.get(); }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate.get(); }
    public String getAuthorId() { return authorId.get(); }
    public String getReportType() { return reportType.get(); }
    public String getStatus() { return status.get(); }
    public String getTargetAudience() { return targetAudience.get(); }
    public boolean isPublic() { return isPublic.get(); }
    public String getTags() { return tags.get(); }
    public ReportType getReportTypeEnum() { return reportTypeEnum.get(); }
    public int getIntId() { return intId.get(); }
    public int getIntAuthorId() { return intAuthorId.get(); }

    // Property getters for JavaFX binding
    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty contentProperty() { return content; }
    public ObjectProperty<LocalDateTime> creationDateProperty() { return creationDate; }
    public ObjectProperty<LocalDateTime> lastModifiedDateProperty() { return lastModifiedDate; }
    public StringProperty authorIdProperty() { return authorId; }
    public StringProperty reportTypeProperty() { return reportType; }
    public StringProperty statusProperty() { return status; }
    public StringProperty targetAudienceProperty() { return targetAudience; }
    public BooleanProperty isPublicProperty() { return isPublic; }
    public StringProperty tagsProperty() { return tags; }
    public IntegerProperty intIdProperty() { return intId; }
    public IntegerProperty intAuthorIdProperty() { return intAuthorId; }
    public List<String> getTagsList() { return tagsList; }

    // Setters
    public void setTitle(String title) { 
        this.title.set(title);
        updateLastModifiedDate();
    }
    
    public void setContent(String content) { 
        this.content.set(content);
        updateLastModifiedDate();
    }
    
    public void setStatus(ReportStatus status) { 
        this.status.set(status.name());
        updateLastModifiedDate();
    }
    
    public void setTargetAudience(String targetAudience) { 
        this.targetAudience.set(targetAudience);
        updateLastModifiedDate();
    }
    
    public void setPublic(boolean isPublic) { 
        this.isPublic.set(isPublic);
        updateLastModifiedDate();
    }
    
    public void setTags(String tags) { 
        this.tags.set(tags);
        updateLastModifiedDate();
    }

    public void setReportTypeEnum(ReportType type) { this.reportTypeEnum.set(type); }

    private void updateLastModifiedDate() {
        lastModifiedDate.set(LocalDateTime.now());
    }

    public void publish() {
        setStatus(ReportStatus.PUBLISHED);
    }

    public void archive() {
        setStatus(ReportStatus.ARCHIVED);
    }

    public void revertToDraft() {
        setStatus(ReportStatus.DRAFT);
    }

    public boolean isPublished() {
        return status.get().equals(ReportStatus.PUBLISHED.name());
    }

    public boolean isDraft() {
        return status.get().equals(ReportStatus.DRAFT.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id.get(), report.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Created: %s, Status: %s",
                title.get(), reportType.get(), 
                creationDate.get().toString(), status.get());
    }

    public void addAllTags(List<String> tags) { this.tagsList.addAll(tags); }
} 