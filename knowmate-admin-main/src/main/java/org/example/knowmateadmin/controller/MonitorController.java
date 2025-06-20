package org.example.knowmateadmin.controller;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.knowmateadmin.common.ApiResponse;

@RestController
@RequestMapping("/api/admin/monitor")
public class MonitorController {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Object> getSystemHealth() {
        return ApiResponse.success(healthEndpoint.health());
    }
}