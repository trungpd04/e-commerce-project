package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.response.statistics.MonthlyRevenue;
import com.nhom7.ecommercebackend.response.statistics.ProductRevenue;
import com.nhom7.ecommercebackend.response.statistics.YearlyRevenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface StatisticsService {
    List<MonthlyRevenue> getMonthlyRevenue(int year);
    YearlyRevenue getYearlyRevenue(int year);
    Page<ProductRevenue> getProductRevenue(PageRequest request);
}
