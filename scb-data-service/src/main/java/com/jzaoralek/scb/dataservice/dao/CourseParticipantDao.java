package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

public interface CourseParticipantDao {

	List<CourseParticipant> getAll();
	CourseParticipant getByUuid(UUID uuid, boolean deep);
	void insert(CourseParticipant courseParticipant);
	void update(CourseParticipant courseParticipant);
	void delete(CourseParticipant courseParticipant);
}
