package com.sportologic.sprtadmin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBInitData {

    private String admUsername;
    private String admPassword;
    private String admContactFirstname;
    private String admContactSurname;
    private String admContactEmail;
    private String admContactPhone;
    private String configCaYear;
    private String configOrgName;
    private String configOrgPhone;
    private String configOrgEmail;
    private String configWelcomeInfo;
    private String configBaseUrl;
    private String configContactPerson;
    private String configCourseApplicationTitle;
    private String configSmtpUser;
    private String configSmptPwd;

    @Override
    public String toString() {
        return "DBInitData{" +
                "admUsername='" + admUsername + '\'' +
                ", admPassword='" + admPassword + '\'' +
                ", admContactFirstname='" + admContactFirstname + '\'' +
                ", admContactSurname='" + admContactSurname + '\'' +
                ", admContactEmail='" + admContactEmail + '\'' +
                ", admContactPhone='" + admContactPhone + '\'' +
                ", configCaYear='" + configCaYear + '\'' +
                ", configOrgName='" + configOrgName + '\'' +
                ", configOrgPhone='" + configOrgPhone + '\'' +
                ", configOrgEmail='" + configOrgEmail + '\'' +
                ", configWelcomeInfo='" + configWelcomeInfo + '\'' +
                ", configBaseUrl='" + configBaseUrl + '\'' +
                ", configContactPerson='" + configContactPerson + '\'' +
                ", configCourseApplicationTitle='" + configCourseApplicationTitle + '\'' +
                ", configSmtpUser='" + configSmtpUser + '\'' +
                ", configSmptPwd='" + configSmptPwd + '\'' +
                '}';
    }
}
