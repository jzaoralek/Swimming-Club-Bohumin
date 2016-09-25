package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Result;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface ResultService {

	List<Result> getByCourseParticipant(UUID courseParticUuid);
	List<Result> getByCourseParticipantAndStyle(UUID courseParticUuid, UUID styleUuid);
	Result store(Result result);
	void delete(UUID uuid) throws ScbValidationException;
	void deleteByCourseParticipant(UUID courseParticUuid);
}
