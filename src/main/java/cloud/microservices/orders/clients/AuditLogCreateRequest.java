package cloud.microservices.orders.clients;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Request DTO for creating a new audit log entry.
 */
public class AuditLogCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @jakarta.validation.constraints.NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @jakarta.validation.constraints.NotBlank(message = "Action is required")
    private String action;

    @jakarta.validation.constraints.NotBlank(message = "Entity type is required")
    private String entityType;

    private String entityId;

    @jakarta.validation.constraints.NotBlank(message = "Username is required")
    private String username;

    private String details;
    private String ipAddress;
    private String userAgent;
    private Integer statusCode;

    /**
     * Default constructor.
     */
    public AuditLogCreateRequest() {
    }

    /**
     * Constructor with all fields.
     */
    public AuditLogCreateRequest(LocalDateTime timestamp, String action, String entityType, String entityId, 
                                String username, String details, String ipAddress, String userAgent, Integer statusCode) {
        this.timestamp = timestamp;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.username = username;
        this.details = details;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.statusCode = statusCode;
    }

    /**
     * Create a new audit log request with current timestamp.
     *
     * @param action     the action performed (CREATE, UPDATE, DELETE, READ)
     * @param entityType the type of entity (e.g., "Order")
     * @param entityId   the ID of the entity
     * @param username   the user who performed the action
     * @param details    additional details about the action
     * @return the audit log create request
     */
    public static AuditLogCreateRequest of(String action, String entityType, String entityId, 
                                          String username, String details) {
        AuditLogCreateRequest request = new AuditLogCreateRequest();
        request.setTimestamp(LocalDateTime.now());
        request.setAction(action);
        request.setEntityType(entityType);
        request.setEntityId(entityId);
        request.setUsername(username);
        request.setDetails(details);
        return request;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLogCreateRequest that = (AuditLogCreateRequest) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(action, that.action) &&
               Objects.equals(entityType, that.entityType) &&
               Objects.equals(entityId, that.entityId) &&
               Objects.equals(username, that.username) &&
               Objects.equals(details, that.details) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(userAgent, that.userAgent) &&
               Objects.equals(statusCode, that.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, action, entityType, entityId, username, details, ipAddress, userAgent, statusCode);
    }

    @Override
    public String toString() {
        return "AuditLogCreateRequest{" +
                "timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", username='" + username + '\'' +
                ", details='" + details + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
