package com.payrollservice.exception;

public class PayrollServiceException extends Exception {
    public PayrollServiceException(String message) {
        super(message);
    }

    public PayrollServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}