package com.payrollservice.service;

import com.payrollservice.db.PayrollDBService;
import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;
import java.util.List;
import java.time.LocalDate;

public class EmployeePayrollService {
    private final PayrollDBService payrollDBService;

    public EmployeePayrollService() {
        this.payrollDBService = PayrollDBService.getInstance();
    }

    public List<EmployeePayrollData> readEmployeePayrollData() throws PayrollServiceException {
        return payrollDBService.readEmployeePayrollData();
    }

    public EmployeePayrollData updateEmployeeSalary(String name, double salary) throws PayrollServiceException {
        return payrollDBService.updateEmployeeSalary(name, salary);
    }

    public boolean checkEmployeePayrollInSync(String name, double salary) throws PayrollServiceException {
        EmployeePayrollData employeePayrollData = payrollDBService.getEmployeePayrollData(name);
        return employeePayrollData != null &&
                Double.compare(employeePayrollData.getSalary(), salary) == 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollByDateRange(LocalDate startDate, LocalDate endDate)
            throws PayrollServiceException {
        if (startDate == null || endDate == null) {
            throw new PayrollServiceException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new PayrollServiceException("Start date cannot be after end date");
        }
        return payrollDBService.getEmployeePayrollByDateRange(startDate, endDate);
    }
}