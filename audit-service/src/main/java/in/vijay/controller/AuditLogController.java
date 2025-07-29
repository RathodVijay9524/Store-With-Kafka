package in.vijay.controller;

import in.vijay.beans.AuditLog;
import in.vijay.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(auditLogRepository.findByUserIdOrderByTimestampDesc((userId)));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogRepository.findByActionOrderByTimestampDesc((action)));
    }

    @GetMapping("/performed-by/{email}")
    public ResponseEntity<List<AuditLog>> getLogsByPerformedBy(@PathVariable String email) {
        return ResponseEntity.ok(auditLogRepository.findByPerformedByOrderByTimestampDesc((email)));
    }
}
