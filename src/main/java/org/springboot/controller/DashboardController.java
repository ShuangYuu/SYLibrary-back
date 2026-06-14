package org.springboot.controller;

import org.springboot.common.Result;
import org.springboot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/trends")
    public Result getDashboardTrends() {
        return Result.success(dashboardService.getDashboardTrends());
    }
}
