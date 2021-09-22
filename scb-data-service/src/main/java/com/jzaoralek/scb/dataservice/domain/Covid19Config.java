package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Covid-19 configuration.
 */
public class Covid19Config implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private boolean enabled;
	private int vaccinationRenewInterval;
	private int testAntigenRenewInterval;
	private int testPcrRenewInterval;
	private int testSelfRenewInterval;
	
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
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getVaccinationRenewInterval() {
		return vaccinationRenewInterval;
	}
	public void setVaccinationRenewInterval(int vaccinationRenewInterval) {
		this.vaccinationRenewInterval = vaccinationRenewInterval;
	}
	public int getTestAntigenRenewInterval() {
		return testAntigenRenewInterval;
	}
	public void setTestAntigenRenewInterval(int testAntigenRenewInterval) {
		this.testAntigenRenewInterval = testAntigenRenewInterval;
	}
	public int getTestPcrRenewInterval() {
		return testPcrRenewInterval;
	}
	public void setTestPcrRenewInterval(int testPcrRenewInterval) {
		this.testPcrRenewInterval = testPcrRenewInterval;
	}
	public int getTestSelfRenewInterval() {
		return testSelfRenewInterval;
	}
	public void setTestSelfRenewInterval(int testSelfRenewInterval) {
		this.testSelfRenewInterval = testSelfRenewInterval;
	}
	
	@Override
	public String toString() {
		return "Covid19Config [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", enabled=" + enabled
				+ ", vaccinationRenewInterval=" + vaccinationRenewInterval + ", testAntigenRenewInterval="
				+ testAntigenRenewInterval + ", testPcrRenewInterval=" + testPcrRenewInterval
				+ ", testSelfRenewInterval=" + testSelfRenewInterval + "]";
	}
}