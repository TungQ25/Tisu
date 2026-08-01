package com.example.tisu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sync_operations")
public class SyncOperation {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 80)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 40)
    private SyncEntityType entityType;

    @Column(name = "entity_id", nullable = false, length = 80)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 20)
    private SyncOperationType operation;

    @Column(name = "base_version", nullable = false)
    private long baseVersion;

    @Column(name = "client_timestamp", nullable = false)
    private long clientTimestamp;

    @Column(name = "server_timestamp", nullable = false)
    private long serverTimestamp;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public SyncEntityType getEntityType() { return entityType; }
    public void setEntityType(SyncEntityType entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public SyncOperationType getOperation() { return operation; }
    public void setOperation(SyncOperationType operation) { this.operation = operation; }
    public long getBaseVersion() { return baseVersion; }
    public void setBaseVersion(long baseVersion) { this.baseVersion = baseVersion; }
    public long getClientTimestamp() { return clientTimestamp; }
    public void setClientTimestamp(long clientTimestamp) { this.clientTimestamp = clientTimestamp; }
    public long getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
