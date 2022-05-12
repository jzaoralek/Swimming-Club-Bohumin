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
public class RestResponse<T> {
    public enum RestResponseState {OK, WARN, ERROR};

    private RestResponseState state;
    private String message;
    private T object;
}
