package com.bestapp.com.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "audit")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "methodName", nullable = false)
    private String methodName;

    @Column(name = "parameters", nullable = false)
    private String parameters;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AuditLog(String username, String methodName, String parameters, LocalDateTime timestamp) {
        this.username = username;
        this.methodName = methodName;
        this.parameters = parameters;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
