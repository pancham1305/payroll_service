package com.payrollservice.service;

import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class EmployeePayrollServiceTest {

    private EmployeePayrollService employeePayrollService;

    @Before
    public void setup() {
        employeePayrollService = new EmployeePayrollService();
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        try {
            List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData();
            assertNotNull(employeePayrollData);
            // Assuming you have some data in your database
            assertTrue(employeePayrollData.size() > 0);
        } catch (PayrollServiceException e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    @Test
    public void givenNewSalaryForTerisa_WhenUpdated_ShouldSyncWithDatabase() {
        try {
            // Update Terisa's salary
            String employeeName = "Terisa";
            double newSalary = 3000000.00;
            
            // Update salary
            EmployeePayrollData updatedEmployee = employeePayrollService.updateEmployeeSalary(employeeName, newSalary);
            
            // Verify the update
            assertNotNull(updatedEmployee);
            assertEquals(newSalary, updatedEmployee.getSalary(), 0.01);
            
            // Verify sync with database
            assertTrue(employeePayrollService.checkEmployeePayrollInSync(employeeName, newSalary));
            
        } catch (PayrollServiceException e) {
            fail("Update failed: " + e.getMessage());
        }
    }

    @Test
    public void givenNewSalaryForNonExistentEmployee_WhenUpdated_ShouldThrowException() {
        try {
            employeePayrollService.updateEmployeeSalary("NonExistentEmployee", 3000000.00);
            fail("Expected PayrollServiceException for non-existent employee");
        } catch (PayrollServiceException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }
}