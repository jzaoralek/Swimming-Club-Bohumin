package com.sportologic.sprtadmin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object for cust-config-create REST action.
 */
@Getter
@Setter
@NoArgsConstructor
public class CustConfigResp extends RestResponse {
    private String custInstanceUrl;
    private String custEmail;
    private String admUsername;
    private String admPassword;
}
