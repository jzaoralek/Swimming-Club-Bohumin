package com.sportologic.sprtadmin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parent for DTO used as REST response.
 */
@Getter
@Setter
@NoArgsConstructor
public class RestResponse {
    public enum ResponseStatus {
        OK,
        ERROR,
        WARN,
        VALIDATION;
    }

    private RestResponse.ResponseStatus status = RestResponse.ResponseStatus.OK;
    private String description;
}
