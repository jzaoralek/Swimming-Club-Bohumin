package com.jzaoralek.scb.dataservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jzaoralek.scb.dataservice.service.CourseApplicationService;

/**
 * Rest Controller for applications to swimming courses.
 */

@RestController
@RequestMapping("/course-applications")
public class CourseApplicationController extends AbstractScbDataServiceController {

	@Autowired
	private CourseApplicationService courseApplicationService;
	
	/**
	 * Returns course application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String read(@PathVariable("uuid") String uuid) {
    	// just for test purposes
        return "uuid: " + uuid;
    }
    
    /**
	 * Returns course application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public List<String> read() {
    	// just for test purposes
        return courseApplicationService.getAll();
    }
    
    @RequestMapping(value = "/exception/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String readWithException(@PathVariable("uuid") String uuid) {	
    	// just for test purposes
        throw new RuntimeException("this is RuntimeException");
    }
}