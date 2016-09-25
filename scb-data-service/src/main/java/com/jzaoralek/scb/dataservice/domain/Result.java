package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class Result implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Long resultTime;
	private Date resultDate;
	private CodeListItem style;
	private UUID courseParticipantUuid;
	private String description;
	private Integer distance;

	public Long getResultTime() {
		return resultTime;
	}
	public void setResultTime(Long resultTime) {
		this.resultTime = resultTime;
	}
	public Date getResultDate() {
		return resultDate;
	}
	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
	}
	public CodeListItem getStyle() {
		return style;
	}
	public void setStyle(CodeListItem style) {
		this.style = style;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
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
	public UUID getCourseParticipantUuid() {
		return courseParticipantUuid;
	}
	public void setCourseParticipantUuid(UUID courseParticipantUuid) {
		this.courseParticipantUuid = courseParticipantUuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Result [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", resultTime=" + resultTime
				+ ", resultDate=" + resultDate + ", style=" + style + ", courseParticipantUuid=" + courseParticipantUuid
				+ ", description=" + description + ", distance=" + distance + "]";
	}
}