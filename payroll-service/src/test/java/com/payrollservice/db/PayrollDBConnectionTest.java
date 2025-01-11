package com.payrollservice.db;

import org.junit.Test;

import com.payrollservice.db.PayrollDBConnection;

import static org.junit.Assert.*;
import java.sql.Connection;

public class PayrollDBConnectionTest {

    @Test
    public void testDatabaseConnection() {
        try (Connection connection = PayrollDBConnection.getConnection()) {
            assertNotNull("Connection should not be null", connection);
            assertTrue("Connection should be valid", !connection.isClosed());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}