package com.sportologic.sprtadmin.vo;

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

    public String getAdmUsername() {
        return admUsername;
    }

    public void setAdmUsername(String admUsername) {
        this.admUsername = admUsername;
    }

    public String getAdmPassword() {
        return admPassword;
    }

    public void setAdmPassword(String admPassword) {
        this.admPassword = admPassword;
    }

    public String getAdmContactFirstname() {
        return admContactFirstname;
    }

    public void setAdmContactFirstname(String admContactFirstname) {
        this.admContactFirstname = admContactFirstname;
    }

    public String getAdmContactSurname() {
        return admContactSurname;
    }

    public void setAdmContactSurname(String admContactSurname) {
        this.admContactSurname = admContactSurname;
    }

    public String getAdmContactEmail() {
        return admContactEmail;
    }

    public void setAdmContactEmail(String admContactEmail) {
        this.admContactEmail = admContactEmail;
    }

    public String getAdmContactPhone() {
        return admContactPhone;
    }

    public void setAdmContactPhone(String admContactPhone) {
        this.admContactPhone = admContactPhone;
    }

    public String getConfigCaYear() {
        return configCaYear;
    }

    public void setConfigCaYear(String configCaYear) {
        this.configCaYear = configCaYear;
    }

    public String getConfigOrgName() {
        return configOrgName;
    }

    public void setConfigOrgName(String configOrgName) {
        this.configOrgName = configOrgName;
    }

    public String getConfigOrgPhone() {
        return configOrgPhone;
    }

    public void setConfigOrgPhone(String configOrgPhone) {
        this.configOrgPhone = configOrgPhone;
    }

    public String getConfigOrgEmail() {
        return configOrgEmail;
    }

    public void setConfigOrgEmail(String configOrgEmail) {
        this.configOrgEmail = configOrgEmail;
    }

    public String getConfigWelcomeInfo() {
        return configWelcomeInfo;
    }

    public void setConfigWelcomeInfo(String configWelcomeInfo) {
        this.configWelcomeInfo = configWelcomeInfo;
    }

    public String getConfigBaseUrl() {
        return configBaseUrl;
    }

    public void setConfigBaseUrl(String configBaseUrl) {
        this.configBaseUrl = configBaseUrl;
    }

    public String getConfigContactPerson() {
        return configContactPerson;
    }

    public void setConfigContactPerson(String configContactPerson) {
        this.configContactPerson = configContactPerson;
    }

    public String getConfigCourseApplicationTitle() {
        return configCourseApplicationTitle;
    }

    public void setConfigCourseApplicationTitle(String configCourseApplicationTitle) {
        this.configCourseApplicationTitle = configCourseApplicationTitle;
    }

    public String getConfigSmtpUser() {
        return configSmtpUser;
    }

    public void setConfigSmtpUser(String configSmtpUser) {
        this.configSmtpUser = configSmtpUser;
    }

    public String getConfigSmptPwd() {
        return configSmptPwd;
    }

    public void setConfigSmptPwd(String configSmptPwd) {
        this.configSmptPwd = configSmptPwd;
    }

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
