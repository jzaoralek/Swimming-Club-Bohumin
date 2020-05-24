package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Dynamic attributest on course application.
 *
 */
public class CourseApplDynAttr implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private UUID courseApplUuid;
	private CourseApplDynAttrConfig courseApplDynConfig;
	private String textValue;
	private Date dateValue;
	private int intValue;
	private double doubleValue;
	private boolean booleanValue;
	
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
	public UUID getCourseApplUuid() {
		return courseApplUuid;
	}
	public void setCourseApplUuid(UUID courseApplUuid) {
		this.courseApplUuid = courseApplUuid;
	}
	public CourseApplDynAttrConfig getCourseApplDynConfig() {
		return courseApplDynConfig;
	}
	public void setCourseApplDynConfig(CourseApplDynAttrConfig courseApplDynConfig) {
		this.courseApplDynConfig = courseApplDynConfig;
	}
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public boolean isBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	@Override
	public String toString() {
		return "CourseApplDynAttr [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", courseApplUuid="
				+ courseApplUuid + ", courseApplDynConfig=" + courseApplDynConfig + ", textValue=" + textValue
				+ ", dateValue=" + dateValue + ", intValue=" + intValue + ", doubleValue=" + doubleValue
				+ ", booleanValue=" + booleanValue + "]";
	}
}
