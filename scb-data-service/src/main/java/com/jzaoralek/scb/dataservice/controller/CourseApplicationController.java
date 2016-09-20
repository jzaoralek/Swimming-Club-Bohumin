package com.jzaoralek.scb.dataservice.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jzaoralek.scb.dataservice.domain.ActionResult;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;

/**
 * Rest Controller for applications to swimming courses.
 */

@RestController
@RequestMapping("/course-applications")
public class CourseApplicationController extends AbstractScbDataServiceController {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationController.class);

	@Autowired
	private CourseApplicationService courseApplicationService;

	/**
	 * Returns course application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public CourseApplication read(@PathVariable("uuid") String uuid) {
    	// just for test purposes
        return courseApplicationService.getByUuid(UUID.fromString(uuid));
    }

    /**
	 * Returns all course applications.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public List<CourseApplication> read() {
    	// just for test purposes
        return courseApplicationService.getAll(2016, 2017);
    }

    /**
     * Insert or update course application.
     * @param courseApplication
     * @return
     */
    @RequestMapping(value = "", consumes = "application/json;charset=UTF-8", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResult store(@RequestBody CourseApplication courseApplication) throws ScbValidationException {
        ActionResult result = new ActionResult();
        courseApplicationService.store(courseApplication);
		result.setResultCode(ActionResult.OK_RESULT);

        return result;
    }

    /**
	 * Delete application for uuid.
	 * @param uuid
	 * @return
	 */
    @RequestMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ActionResult delete(@PathVariable("uuid") String uuid) {
        ActionResult result = new ActionResult();
    	try {
    		courseApplicationService.delete(UUID.fromString(uuid));
    		result.setResultCode(ActionResult.OK_RESULT);
    	} catch (Exception e) {
    		LOG.error("Unexpected exception caught during delete courseApplication uuid: " + uuid, e);
    		result.setResultCode(ActionResult.FAIL_RESULT);
    		result.setMessage(e.toString());
    	}

        return result;
    }

    @RequestMapping(value = "/exception/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String readWithException(@PathVariable("uuid") String uuid) {
    	// just for test purposes
        throw new RuntimeException("this is RuntimeException");
    }
}