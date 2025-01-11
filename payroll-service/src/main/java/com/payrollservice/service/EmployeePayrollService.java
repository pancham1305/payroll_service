package com.payrollservice.service;

import com.payrollservice.db.PayrollDBConnection;
import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollService {

    public List<EmployeePayrollData> readEmployeePayrollData() throws PayrollServiceException {
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();

        try (Connection connection = PayrollDBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();

                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }

        } catch (SQLException e) {
            throw new PayrollServiceException("Error while retrieving employee payroll data", e);
        }

        return employeePayrollList;
    }
}
