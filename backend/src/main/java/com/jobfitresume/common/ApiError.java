package com.jobfitresume.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String code;
    private String details;
    private Map<String, String> validationErrors;
    private List<String> errorList;

    public ApiError() {
    }

    public ApiError(String code, String details) {
        this.code = code;
        this.details = details;
    }

    public ApiError(String code, String details, Map<String, String> validationErrors) {
        this.code = code;
        this.details = details;
        this.validationErrors = validationErrors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
