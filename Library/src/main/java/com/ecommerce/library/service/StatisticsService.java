package com.ecommerce.library.service;

import java.util.List;

public interface StatisticsService {

    List<Double> getMonthlyProfit( int year);

    List<Object[]> getBestSellingProductsByMonth(int year);

    List<Object[]> getBestSellingCategoriesByMonth( int year);

    List<Double> getYearlyRevenue(int year);  // Thêm phương thức này
}
