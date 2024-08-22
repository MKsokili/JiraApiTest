package com.example.jiratestapi.BatchError;

public enum ErrorType {
    CONNECTION_ERROR,        // Error related to API or server connection issues
    DATA_MAPPING_ERROR,      // Error when mapping data between platforms
    VALIDATION_ERROR,        // Error related to data validation
    TASK_PROCESSING_ERROR,   // Error while processing tasks
    AUTHENTICATION_ERROR,    // Error related to authentication issues
    UNKNOWN_ERROR,
    ASSIGNEE_NOT_FOUND
}
