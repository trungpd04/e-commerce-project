package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.response.statistics.MonthlyRevenue;
import com.nhom7.ecommercebackend.response.statistics.ProductRevenue;
import com.nhom7.ecommercebackend.response.statistics.YearlyRevenue;
import com.nhom7.ecommercebackend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;

    @Override
    public List<MonthlyRevenue> getMonthlyRevenue(int year) {
        List<Object[]> monthlyRevenue =
                orderRepository.calculateMonthlyRevenueForYear(year);
        List<MonthlyRevenue> monthlyRevenues = new ArrayList<>();
        for(Object[] o : monthlyRevenue) {
            int month = (Integer) o[0];
            int byYear  = (Integer) o[1];
            BigDecimal revenue  = (BigDecimal) o[2];
            monthlyRevenues.add(
                    MonthlyRevenue
                            .builder()
                            .month(month)
                            .year(byYear)
                            .revenue(revenue)
                            .build()
            );
        }
        return monthlyRevenues;
    }

    @Override
    public YearlyRevenue getYearlyRevenue(int year) {
        List<Object[]> results =
                orderRepository.calculateRevenueForYear(year);
        YearlyRevenue yearlyRevenue = new YearlyRevenue();
        for(Object[] o : results) {
            int byYear  = (Integer) o[0];
            BigDecimal revenue  = (BigDecimal) o[1];
            yearlyRevenue.setYear(byYear);
            yearlyRevenue.setRevenue(revenue);
        }
        return yearlyRevenue;
    }

    @Override
    public Page<ProductRevenue> getProductRevenue(PageRequest request) {
        Page<Object[]> getProductRevenue =
                orderRepository.calculateProductRevenue(request);
        return getProductRevenue.map(objects -> {
            return new ProductRevenue(
                    (long) objects[0],
                    (long) objects[1],
                    (BigDecimal) objects[2]
            );
        });
    }
}
