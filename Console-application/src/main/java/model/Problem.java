package model;

import java.time.LocalDateTime;

public class Problem {
    private Integer id;
    private String title;
    private String description;
    private Integer memoryLimitMb;
    private Integer timeLimitMs;
    private LocalDateTime createdAt;

    public Problem(Integer id, String title, String description, Integer memoryLimitMb, Integer timeLimitMs, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.memoryLimitMb = memoryLimitMb;
        this.timeLimitMs = timeLimitMs;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMemoryLimitMb() {
        return memoryLimitMb;
    }

    public void setMemoryLimitMb(Integer memoryLimitMb) {
        this.memoryLimitMb = memoryLimitMb;
    }

    public Integer getTimeLimitMs() {
        return timeLimitMs;
    }

    public void setTimeLimitMs(Integer timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "Problem #%d\n" +
                "Title:       %s\n" +
                "Description: %s\n" +
                "Limits:      %d MB | %d ms\n" +
                "Created:     %s",
                id,
                title,
                (description != null ? description : "N/A"),
                memoryLimitMb,
                timeLimitMs,
                (createdAt != null ? createdAt : "N/A")
        );
    }
}