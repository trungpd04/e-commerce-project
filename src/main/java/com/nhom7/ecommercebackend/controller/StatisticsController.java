package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.StatisticsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/monthly_revenue")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getMonthlyRevenue(
            @RequestParam(name = "year", required = true) int year
    ) {
        return ApiResponse
                .builder()
                .message("Monthly statistics")
                .status(HTTP_OK)
                .data(statisticsService.getMonthlyRevenue(year))
                .build();
    }
    @GetMapping("/yearly_revenue")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getYearlyRevenue(
            @RequestParam(name = "year", required = true) int year
    ) {
        return ApiResponse
                .builder()
                .status(HTTP_OK)
                .message("Yearly statistics")
                .data(statisticsService.getYearlyRevenue(year))
                .build();
    }
    @GetMapping("/product_revenue")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getProductRevenue(
            @RequestParam(name = "size", required = false, defaultValue = "5") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page
    ) {
        PageRequest request = PageRequest.of(page, size);
        return ApiResponse
                .builder()
                .message("Product statistics")
                .status(HTTP_OK)
                .data(statisticsService.getProductRevenue(request))
                .build();
    }
}
