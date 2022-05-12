package com.sportologic.sprtadmin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustConfigDto {
    private String custName;
    private String custEmail;
    private String custPhone;
    private String admFirstname;
    private String admSurname;
    private String admEmail;
    private String admPhone;
}
