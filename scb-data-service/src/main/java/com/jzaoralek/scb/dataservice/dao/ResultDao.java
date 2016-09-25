package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Result;

public interface ResultDao {

	List<Result> getByCourseParticipant(UUID courseParticUuid);
	List<Result> getByCourseParticipantAndStyle(UUID courseParticUuid, UUID styleUuid);
	Result getByUuid(UUID uuid);
	void insert(Result result);
	void update(Result result);
	void delete(UUID uuid);
	void deleteByCourseParticipant(UUID courseParticUuid);

}