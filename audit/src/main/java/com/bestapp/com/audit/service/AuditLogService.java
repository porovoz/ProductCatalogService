package com.bestapp.com.audit.service;

import com.bestapp.com.audit.model.AuditLog;

public interface AuditLogService {

    void saveAuditLog(AuditLog auditLog);

}
