package com.dentflow.core.reservation.api;

import com.dentflow.core.reservation.application.PatientVisitHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants/{tenantId}/patients/{patientId}/visits")
public class PatientVisitHistoryController {

    private final PatientVisitHistoryService historyService;

    public PatientVisitHistoryController(PatientVisitHistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * GET /tenants/{tenantId}/patients/{patientId}/visits
     * Zwraca pełną historię wizyt pacjenta, od najnowszej.
     *
     * Opcjonalny parametr ?status=COMPLETED filtruje po statusie.
     */
    @GetMapping
    public ResponseEntity<List<PatientVisitHistoryDTO>> getPatientVisitHistory(
            @PathVariable Long tenantId,
            @PathVariable Long patientId,
            @RequestParam(required = false) String status) {

        List<PatientVisitHistoryDTO> result = (status != null && !status.isBlank())
                ? historyService.getPatientHistoryByStatus(tenantId, patientId, status)
                : historyService.getPatientHistory(tenantId, patientId);

        return ResponseEntity.ok(result);
    }
}
