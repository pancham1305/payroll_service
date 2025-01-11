package com.payrollservice.model;

public class EmployeePayrollAnalysis {
    private char gender;
    private double sumSalary;
    private double avgSalary;
    private double minSalary;
    private double maxSalary;
    private int count;

    public EmployeePayrollAnalysis(char gender, double sumSalary, double avgSalary,
            double minSalary, double maxSalary, int count) {
        this.gender = gender;
        this.sumSalary = sumSalary;
        this.avgSalary = avgSalary;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.count = count;
    }

    // Getters
    public char getGender() {
        return gender;
    }

    public double getSumSalary() {
        return sumSalary;
    }

    public double getAvgSalary() {
        return avgSalary;
    }

    public double getMinSalary() {
        return minSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.format("Gender: %c, Total: %.2f, Average: %.2f, Min: %.2f, Max: %.2f, Count: %d",
                gender, sumSalary, avgSalary, minSalary, maxSalary, count);
    }
}