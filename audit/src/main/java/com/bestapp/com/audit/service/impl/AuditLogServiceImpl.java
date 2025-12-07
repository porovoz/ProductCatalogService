package com.bestapp.com.audit.service.impl;

import com.bestapp.com.audit.model.AuditLog;
import com.bestapp.com.audit.repository.AuditLogRepository;
import com.bestapp.com.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}
