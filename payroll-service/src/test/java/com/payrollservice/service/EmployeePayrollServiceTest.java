package com.payrollservice.service;

import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmployeePayrollServiceTest {

    private EmployeePayrollService employeePayrollService;

    @Before
    public void setup() {
        employeePayrollService = new EmployeePayrollService();
    }

    @Test
    public void givenNewSalaryForTerisa_WhenUpdated_ShouldSyncWithDatabase() {
        try {
            // First update
            String employeeName = "Terisa";
            double newSalary = 3000000.00;
            EmployeePayrollData updatedEmployee = employeePayrollService.updateEmployeeSalary(employeeName, newSalary);
            assertTrue(employeePayrollService.checkEmployeePayrollInSync(employeeName, newSalary));

            // Second update to test prepared statement reuse
            double anotherSalary = 3500000.00;
            updatedEmployee = employeePayrollService.updateEmployeeSalary(employeeName, anotherSalary);
            assertTrue(employeePayrollService.checkEmployeePayrollInSync(employeeName, anotherSalary));

        } catch (PayrollServiceException e) {
            fail("Update failed: " + e.getMessage());
        }
    }
}