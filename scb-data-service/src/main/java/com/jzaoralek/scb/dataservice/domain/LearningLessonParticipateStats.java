package com.jzaoralek.scb.dataservice.domain;

/**
 * Obsahuje jmeno usastnika a procentualni ucast na lekcich.
 *
 */
public class LearningLessonParticipateStats {

	private final String particName;
	private final int particAttendance;
	
	public LearningLessonParticipateStats(String particName, int particAttendance) {
		super();
		this.particName = particName;
		this.particAttendance = particAttendance;
	}
	
	public String getParticName() {
		return particName;
	}
	public int getParticAttendance() {
		return particAttendance;
	}

	@Override
	public String toString() {
		return "LearningLessonParticipateStats [particName=" + particName + ", particAttendance=" + particAttendance
				+ "]";
	}
}
