package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.ResultDao;
import com.jzaoralek.scb.dataservice.domain.Result;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.ResultService;

@Service("resultService")
public class ResultServiceImpl extends BaseAbstractService implements ResultService {

	private static final Logger LOG = LoggerFactory.getLogger(ResultServiceImpl.class);

	@Autowired
	private ResultDao resultDao;

	@Override
	public List<Result> getByCourseParticipant(UUID courseParticUuid) {
		return resultDao.getByCourseParticipant(courseParticUuid);
	}

	@Override
	public List<Result> getByCourseParticipantAndStyle(UUID courseParticUuid, UUID styleUuid) {
		return resultDao.getByCourseParticipantAndStyle(courseParticUuid, styleUuid);
	}

	@Override
	public Result store(Result result) {
		if (result == null) {
			throw new IllegalArgumentException("result is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing Result: " + result);
		}

		boolean insert = result.getUuid() == null;

		fillIdentEntity(result);

		if (insert) {
			resultDao.insert(result);
		} else {
			resultDao.update(result);
		}

		return result;
	}

	@Override
	public void delete(UUID uuid) throws ScbValidationException {
		Result item = resultDao.getByUuid(uuid);
		if (item == null) {
			LOG.warn("Result not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.recordNotExistsInDB", null, Locale.getDefault()));
		}
		resultDao.delete(uuid);
	}

	@Override
	public void deleteByCourseParticipant(UUID courseParticUuid) {
		resultDao.deleteByCourseParticipant(courseParticUuid);
	}

}
