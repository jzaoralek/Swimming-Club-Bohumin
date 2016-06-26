package com.jzaoralek.scb.dataservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for applications to swimming courses.
 */

@RestController
@RequestMapping("/course-applications")
public class CourseApplicationController extends AbstractScbDataServiceController {

	/**
	 * Returns cource application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String read(@PathVariable("uuid") String uuid) {
    	
    	// just for test purposes
        return "uuid: " + uuid;
    }
    
    @RequestMapping(value = "/exception/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String readWithException(@PathVariable("uuid") String uuid) {	
    	// just for test purposes
        throw new RuntimeException("this is RuntimeException");
    }
}