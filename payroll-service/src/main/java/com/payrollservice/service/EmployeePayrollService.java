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
    public EmployeePayrollData updateEmployeeSalary(String name, double salary) throws PayrollServiceException {
        String sql = "UPDATE employee_payroll SET salary = ? WHERE name = ?";
        
        try (Connection connection = PayrollDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDouble(1, salary);
            statement.setString(2, name);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new PayrollServiceException("Employee " + name + " not found");
            }
            
            return getEmployeePayrollData(name);
            
        } catch (SQLException e) {
            throw new PayrollServiceException("Error while updating employee salary", e);
        }
    }

    public EmployeePayrollData getEmployeePayrollData(String name) throws PayrollServiceException {
        String sql = "SELECT * FROM employee_payroll WHERE name = ?";
        
        try (Connection connection = PayrollDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, name);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new EmployeePayrollData(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("start_date").toLocalDate()
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PayrollServiceException("Error while retrieving employee data", e);
        }
    }

    public boolean checkEmployeePayrollInSync(String name, double salary) throws PayrollServiceException {
        EmployeePayrollData employeePayrollData = getEmployeePayrollData(name);
        return employeePayrollData != null && 
               Double.compare(employeePayrollData.getSalary(), salary) == 0;
    }
}
