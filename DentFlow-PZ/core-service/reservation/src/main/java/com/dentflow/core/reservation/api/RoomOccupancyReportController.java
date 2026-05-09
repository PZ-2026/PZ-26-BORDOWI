package com.dentflow.core.reservation.api;

import com.dentflow.core.reservation.application.RoomOccupancyReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/tenants/{tenantId}/reports/room-occupancy")
public class RoomOccupancyReportController {

    private final RoomOccupancyReportService reportService;

    public RoomOccupancyReportController(RoomOccupancyReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /tenants/{tenantId}/reports/room-occupancy?from=...&to=...
     * Zwraca raport obłożenia wszystkich gabinetów w podanym przedziale czasu.
     */
    @GetMapping
    public ResponseEntity<List<RoomOccupancyDTO>> getRoomOccupancyReport(
            @PathVariable Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return ResponseEntity.ok(reportService.getRoomOccupancyReport(tenantId, from, to));
    }

    /**
     * GET /tenants/{tenantId}/reports/room-occupancy/{roomId}?from=...&to=...
     * Zwraca raport obłożenia konkretnego gabinetu.
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomOccupancyDTO> getRoomOccupancy(
            @PathVariable Long tenantId,
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return ResponseEntity.ok(reportService.getRoomOccupancy(tenantId, roomId, from, to));
    }
}
