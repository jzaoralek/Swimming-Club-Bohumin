package com.jzaoralek.scb.dataservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for applications to swimming courses.
 */

@RestController
@RequestMapping("/course-applications")
public class CourseApplicationController {
	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationController.class);

	/**
	 * Returns cource application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public String read(@PathVariable("uuid") String uuid) {	
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("read claim: {}", uuid);
        }

    	// just for test purposes
        return "uuid: " + uuid;
    }
}