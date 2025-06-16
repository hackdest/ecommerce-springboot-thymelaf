package com.ecommerce.admin.controller;

import com.ecommerce.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping("/yearly-revenue")
    public String getYearlyRevenue(@RequestParam(value = "year", required = false) Integer year, Model model) {
        if (year == null) {
            year = Year.now().getValue();  // Lấy năm hiện tại nếu không có tham số year
        }
        List<Double> monthlyRevenues = statisticsService.getYearlyRevenue(year);
        List<Double> profit = statisticsService.getMonthlyProfit(year);
        model.addAttribute("profit", profit);

        model.addAttribute("monthlyRevenues", monthlyRevenues);
        model.addAttribute("year", year);
        return "yearly-revenue";
    }



    @GetMapping("/best-selling-products")
    public String getBestSellingProductsByMonth( @RequestParam("year") Integer year, Model model) {
        if (year == null) {
            year = Year.now().getValue();  // Lấy năm hiện tại nếu không có tham số year
        }
        List<Object[]> products = statisticsService.getBestSellingProductsByMonth( year);
        model.addAttribute("products", products);
        model.addAttribute("year", year);
        return "best-selling-products";
    }

    @GetMapping("/best-selling-categories")
    public String getBestSellingCategoriesByMonth( @RequestParam("year") Integer year, Model model) {
        if (year == null) {
            year = Year.now().getValue();  // Lấy năm hiện tại nếu không có tham số year
        }
        List<Object[]> categories = statisticsService.getBestSellingCategoriesByMonth( year);
        model.addAttribute("categories", categories);
        model.addAttribute("year", year);
        return "best-selling-categories";
    }
}
