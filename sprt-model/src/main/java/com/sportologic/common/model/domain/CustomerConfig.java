package com.sportologic.common.model.domain;

import java.util.Date;
import java.util.UUID;

public class CustomerConfig implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String custId;
	private String custName;
	private boolean custDefault;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	
	@Override
	public UUID getUuid() {
		return uuid;
	}
	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	@Override
	public String getModifBy() {
		return modifBy;
	}
	@Override
	public void setModifBy(String modifBy) {
		this.modifBy = modifBy;
	}
	@Override
	public Date getModifAt() {
		return modifAt;
	}
	@Override
	public void setModifAt(Date modifAt) {
		this.modifAt = modifAt;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public boolean isCustDefault() {
		return custDefault;
	}
	public void setCustDefault(boolean custDefault) {
		this.custDefault = custDefault;
	}
	
	@Override
	public String toString() {
		return "CustomerConfig [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", custId=" + custId
				+ ", custName=" + custName + ", custDefault=" + custDefault + ", dbUrl=" + dbUrl + ", dbUser=" + dbUser
				+ ", dbPassword=" + dbPassword + "]";
	}
}