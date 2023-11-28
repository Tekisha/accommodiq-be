package com.example.accommodiq.dtos;

public class ReportResponseDto {
    private Long id;
    private String reason;
    private Long timestamp;
    private Long reportingUserId;
    private Long reportedUserId;

    public ReportResponseDto(Long id, String reason, Long timestamp, Long reportingUserId, Long reportedUserId) {
        this.id = id;
        this.reason = reason;
        this.timestamp = timestamp;
        this.reportingUserId = reportingUserId;
        this.reportedUserId = reportedUserId;
    }

    public ReportResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getReportingUserId() {
        return reportingUserId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setReportingUserId(Long reportingUserId) {
        this.reportingUserId = reportingUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }
}
