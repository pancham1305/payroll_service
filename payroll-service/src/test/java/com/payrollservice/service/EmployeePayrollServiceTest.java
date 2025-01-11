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
}