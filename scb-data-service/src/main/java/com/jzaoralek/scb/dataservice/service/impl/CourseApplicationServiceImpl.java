package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;

@Service
public class CourseApplicationServiceImpl implements CourseApplicationService {

	@Autowired
	private CourseApplicationDao courseApplicationDao;
	
	@Override
	public List<String> getAll() {
		return courseApplicationDao.getAll();
	}
}