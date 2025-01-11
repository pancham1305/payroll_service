package com.payrollservice.db;

import com.payrollservice.model.EmployeePayrollAnalysis;
import com.payrollservice.model.EmployeePayrollData;
import com.payrollservice.exception.PayrollServiceException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PayrollDBService {
    private static PayrollDBService instance;
    private PreparedStatement employeePayrollDataStatement;
    private PreparedStatement updateSalaryStatement;
    private Map<String, PreparedStatement> preparedStatementCache = new HashMap<>();
    private PreparedStatement employeesByDateRangeStatement;
    private PreparedStatement genderAnalysisStatement;

    private PayrollDBService() {
    }

    public static PayrollDBService getInstance() {
        if (instance == null) {
            synchronized (PayrollDBService.class) {
                if (instance == null) {
                    instance = new PayrollDBService();
                }
            }
        }
        return instance;
    }

    private synchronized PreparedStatement getPreparedStatement(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = preparedStatementCache.get(sql);
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatementCache.put(sql, preparedStatement);
        }
        return preparedStatement;
    }

    private void prepareStatements(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM employee_payroll WHERE name = ?";
        String updateSQL = "UPDATE employee_payroll SET salary = ? WHERE name = ?";
        String dateRangeSQL = "SELECT * FROM employee_payroll WHERE start_date BETWEEN ? AND ?";
        String genderAnalysisSQL = "SELECT gender, " +
                "SUM(salary) as sum_salary, " +
                "AVG(salary) as avg_salary, " +
                "MIN(salary) as min_salary, " +
                "MAX(salary) as max_salary, " +
                "COUNT(*) as employee_count " +
                "FROM employee_payroll " +
                "WHERE gender = ? " +
                "GROUP BY gender";
        genderAnalysisStatement = connection.prepareStatement(genderAnalysisSQL);

        employeesByDateRangeStatement = connection.prepareStatement(dateRangeSQL);
        employeePayrollDataStatement = connection.prepareStatement(selectSQL);
        updateSalaryStatement = connection.prepareStatement(updateSQL);
    }

    private EmployeePayrollData getEmployeePayrollDataFromResultSet(ResultSet resultSet) throws SQLException {
        return new EmployeePayrollData(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getDouble("salary"),
                resultSet.getDate("start_date").toLocalDate());
    }

    public List<EmployeePayrollData> readEmployeePayrollData() throws PayrollServiceException {
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();

        try (Connection connection = PayrollDBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employeePayrollList.add(getEmployeePayrollDataFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            throw new PayrollServiceException("Error while retrieving employee payroll data", e);
        }
        return employeePayrollList;
    }

    public EmployeePayrollData updateEmployeeSalary(String name, double salary) throws PayrollServiceException {
        try (Connection connection = PayrollDBConnection.getConnection()) {
            if (updateSalaryStatement == null) {
                prepareStatements(connection);
            }

            updateSalaryStatement.setDouble(1, salary);
            updateSalaryStatement.setString(2, name);

            int rowsAffected = updateSalaryStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new PayrollServiceException("Employee " + name + " not found");
            }

            return getEmployeePayrollData(name, connection);

        } catch (SQLException e) {
            throw new PayrollServiceException("Error while updating employee salary", e);
        }
    }

    public EmployeePayrollData getEmployeePayrollData(String name) throws PayrollServiceException {
        try (Connection connection = PayrollDBConnection.getConnection()) {
            return getEmployeePayrollData(name, connection);
        } catch (SQLException e) {
            throw new PayrollServiceException("Error while retrieving employee data", e);
        }
    }

    private EmployeePayrollData getEmployeePayrollData(String name, Connection connection)
            throws PayrollServiceException {
        try {
            if (employeePayrollDataStatement == null) {
                prepareStatements(connection);
            }

            employeePayrollDataStatement.setString(1, name);

            try (ResultSet resultSet = employeePayrollDataStatement.executeQuery()) {
                return resultSet.next() ? getEmployeePayrollDataFromResultSet(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new PayrollServiceException("Error while retrieving employee data", e);
        }
    }

    public List<EmployeePayrollData> getEmployeePayrollByDateRange(LocalDate startDate, LocalDate endDate)
            throws PayrollServiceException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();

        try (Connection connection = PayrollDBConnection.getConnection()) {
            if (employeesByDateRangeStatement == null) {
                prepareStatements(connection);
            }

            employeesByDateRangeStatement.setDate(1, Date.valueOf(startDate));
            employeesByDateRangeStatement.setDate(2, Date.valueOf(endDate));

            try (ResultSet resultSet = employeesByDateRangeStatement.executeQuery()) {
                while (resultSet.next()) {
                    employeePayrollList.add(getEmployeePayrollDataFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new PayrollServiceException(
                    "Error while retrieving employee data for date range: " +
                            startDate + " to " + endDate,
                    e);
        }

        return employeePayrollList;
    }

    public EmployeePayrollAnalysis getEmployeeAnalysisByGender(char gender)
            throws PayrollServiceException {
        try (Connection connection = PayrollDBConnection.getConnection()) {
            if (genderAnalysisStatement == null) {
                prepareStatements(connection);
            }

            genderAnalysisStatement.setString(1, String.valueOf(gender));

            try (ResultSet resultSet = genderAnalysisStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new EmployeePayrollAnalysis(
                            gender,
                            resultSet.getDouble("sum_salary"),
                            resultSet.getDouble("avg_salary"),
                            resultSet.getDouble("min_salary"),
                            resultSet.getDouble("max_salary"),
                            resultSet.getInt("employee_count"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PayrollServiceException(
                    "Error while analyzing employee data for gender: " + gender, e);
        }
    }

}