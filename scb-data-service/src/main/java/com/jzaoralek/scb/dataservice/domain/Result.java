package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Result implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Time resultTime;
	private Date resultDate;
	private SwimmingStyle style;
	private int distance;

	public Time getResultTime() {
		return resultTime;
	}
	public void setResultTime(Time resultTime) {
		this.resultTime = resultTime;
	}
	public Date getResultDate() {
		return resultDate;
	}
	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
	}
	public SwimmingStyle getStyle() {
		return style;
	}
	public void setStyle(SwimmingStyle style) {
		this.style = style;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
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

	@Override
	public String toString() {
		return "Result [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", resultTime=" + resultTime
				+ ", resultDate=" + resultDate + ", style=" + style + ", distance=" + distance + "]";
	}
}