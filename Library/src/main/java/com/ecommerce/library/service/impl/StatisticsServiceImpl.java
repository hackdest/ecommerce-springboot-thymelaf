package com.ecommerce.library.service.impl;

import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;






    @Override
    public List<Object[]> getBestSellingProductsByMonth(int year) {
        List<Object[]> bestSellingProducts = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Object[] product = orderRepository.getBestSellingProductByMonth(month, year);
            if (product != null) {
                bestSellingProducts.add(product);
            }
        }
        return bestSellingProducts;
    }

    @Override
    public List<Object[]> getBestSellingCategoriesByMonth(int year) {
        List<Object[]> bestSellingCategories = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Object[] category = orderRepository.getBestSellingCategoryByMonth(month, year);
            if (category != null) {
                bestSellingCategories.add(category);
            }
        }
        return bestSellingCategories;
    }

    @Override
    public List<Double> getMonthlyProfit( int year) {
        List<Double> Profit = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Double profit = orderRepository.getMonthlyProfit(month, year);
            Profit.add(profit != null ? profit : 0.0);
        }
        return Profit;

    }
///đúng
    @Override
    public List<Double> getYearlyRevenue(int year) {
        List<Double> monthlyRevenues = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Double revenue = orderRepository.getMonthlyRevenue(month, year);
            monthlyRevenues.add(revenue != null ? revenue : 0.0);
        }
        return monthlyRevenues;
    }
}
