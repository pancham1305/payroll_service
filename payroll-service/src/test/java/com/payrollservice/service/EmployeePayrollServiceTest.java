package com.payrollservice.service;

import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.List;

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

    @Test
    public void givenDateRange_WhenRetrieved_ShouldReturnEmployeeList() {
        try {
            LocalDate startDate = LocalDate.of(2019, 1, 1);
            LocalDate endDate = LocalDate.of(2020, 12, 31);

            List<EmployeePayrollData> employeeList = employeePayrollService.getEmployeePayrollByDateRange(startDate,
                    endDate);

            assertNotNull(employeeList);
            // Verify employees within date range are returned
            for (EmployeePayrollData employee : employeeList) {
                assertTrue(
                        !employee.getStartDate().isBefore(startDate) &&
                                !employee.getStartDate().isAfter(endDate));
            }
        } catch (PayrollServiceException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test(expected = PayrollServiceException.class)
    public void givenInvalidDateRange_WhenRetrieved_ShouldThrowException()
            throws PayrollServiceException {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2019, 12, 31);

        employeePayrollService.getEmployeePayrollByDateRange(startDate, endDate);
    }

    @Test
    public void givenDateRangeWithNoEmployees_WhenRetrieved_ShouldReturnEmptyList() {
        try {
            LocalDate startDate = LocalDate.of(2015, 1, 1);
            LocalDate endDate = LocalDate.of(2015, 12, 31);

            List<EmployeePayrollData> employeeList = employeePayrollService.getEmployeePayrollByDateRange(startDate,
                    endDate);

            assertNotNull(employeeList);
            assertTrue(employeeList.isEmpty());
        } catch (PayrollServiceException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
}