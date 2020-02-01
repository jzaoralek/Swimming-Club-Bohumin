package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseLocationDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseCourseParticipantVO;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BankPaymentService;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LessonService;
import com.jzaoralek.scb.dataservice.service.PaymentService;

@Service("courseService")
public class CourseServiceImpl extends BaseAbstractService implements CourseService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;
	
	@Autowired
	private CourseApplicationService courseApplicationService;
	
	@Autowired
	private LessonService lessonService;
	
	@Autowired
	private CourseLocationDao courseLocationDao;
	
	@Autowired
	private BankPaymentService bankPaymentService;
	
	@Autowired
	private PaymentService paymentService;

	@Override
	public void delete(UUID uuid) throws ScbValidationException {
		Course course = courseDao.getByUuid(uuid);
		if (course == null) {
			LOG.warn("CourseApplication not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseApplication: " + course);
		}

		courseDao.delete(course);
	}

	@Override
	public List<Course> getAll(int yearFrom, int yearTo, boolean withLessons) {
		List<Course> ret = courseDao.getAll(yearFrom, yearTo);
		if (withLessons) {
			addLessons(ret);
		}
		return ret;
	}
	
	@Override
	public List<Course> getByTrainer(UUID userUuid, int yearFrom, int yearTo, boolean withLessons) {
		List<Course> ret = courseDao.getByTrainer(userUuid, yearFrom, yearTo);
		if (withLessons) {
			addLessons(ret);
		}
		return ret;
	}
	
	private void addLessons(List<Course> courseList) {
		if (courseList != null && !courseList.isEmpty()) {
			for (Course item : courseList) {
				item.setLessonList(lessonService.getByCourse(item.getUuid()));								
			}
		}
	}

	@Override
	public List<Course> getAllExceptCourse(UUID courseUuid) {
		return courseDao.getAllExceptCourse(courseUuid);
	}

	@Override
	public Course getByUuid(UUID uuid) {
		return courseDao.getByUuid(uuid);
	}
	
	@Override
	public Course getPlainByUuid(UUID uuid) {
		return courseDao.getPlainByUuid(uuid);
	}

	@Override
	public List<CourseParticipant> getByCourseParticListByCourseUuid(UUID courseUuid, boolean inclInterrupted) {
		if (!inclInterrupted) {
			return courseParticipantDao.getByCourseUuid(courseUuid);
		} else {
			return courseParticipantDao.getByCourseIncInterruptedUuid(courseUuid);
		}
	}

	@Override
	public List<CourseParticipant> getCourseParticListByRepresentativeUuid(UUID representativeUserUuid) {
		return courseParticipantDao.getByUserUuid(representativeUserUuid);
	}
	
	@Override
	public CourseParticipant getCourseParticInOneCourse(UUID courseCourseParticUuid) {
		return courseParticipantDao.getCourseParticInOneCourse(courseCourseParticUuid);
	}
	
	@Override
	public Course store(Course course) throws ScbValidationException {
		if (course == null) {
			throw new IllegalArgumentException("course is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing course: " + course);
		}

		boolean insert = course.getUuid() == null;
		fillIdentEntity(course);
		
		if (course.isCourseStandard()) {
			course.setPriceSemester2(0L);
		}
		
		if (insert) {
			courseDao.insert(course);
		} else {
			courseDao.update(course);
		}

		return course;
	}
	
	@Override
	public Course buildCopy(UUID courseUuid, String courseApplicationYear, boolean nameFromOrig) {
		Objects.requireNonNull(courseUuid, "courseUuid is null");
		Objects.requireNonNull(courseApplicationYear, "courseApplicationYear is null");
		
		Course courseOrig = getByUuid(courseUuid);
		if (courseOrig == null) {
			throw new IllegalArgumentException("courseOrig is null");
		}
		
		Course courseNew = new Course();
		courseNew.fillYearFromTo(courseApplicationYear);
		if (nameFromOrig) {
			//  set course name from orig course
			courseNew.setName(messageSource.getMessage("txt.svc.course.name.copyFrom", 
					new Object[] {courseOrig.getName()}, Locale.getDefault()));
		}
		courseNew.setDescription(courseOrig.getDescription());
		courseNew.setCourseType(CourseType.STANDARD);
		courseNew.setCourseLocation(courseOrig.getCourseLocation());
		courseNew.setPriceSemester1(courseOrig.getPriceSemester1());
		courseNew.setPriceSemester2(courseOrig.getPriceSemester2());
		courseNew.setMaxParticipantCount(courseOrig.getMaxParticipantCount());
		
		return courseNew;
	}
	
	@Override
	public Course copy(UUID courseUuid, 
			Course courseNew,
			boolean copyCoursePartics, 
			boolean copyLessons, 
			boolean copyTrainers) throws ScbValidationException {
		Objects.requireNonNull(courseUuid, "courseUuid is null");
		Objects.requireNonNull(courseNew, "courseNew is null");
		
		Course courseOrig = getByUuid(courseUuid);
		if (courseOrig == null) {
			throw new IllegalArgumentException("courseOrig is null");
		}
		
		// create new course
		Course ret = store(courseNew);
		
		// copy participants
		if (copyCoursePartics) {
			courseParticipantDao.insetToCourse(courseOrig.getParticipantList(), ret.getUuid());	
		}
		
		// copy trainers
		if (copyTrainers) {
			List<ScbUser> trainerList = getTrainersByCourse(courseUuid);
			if (!CollectionUtils.isEmpty(trainerList)) {
				addTrainersToCourse(trainerList, ret.getUuid());			
			}			
		}
		
		// lessons
		if (copyLessons) {
			List<Lesson> lessonList = courseOrig.getLessonList();
			if (!CollectionUtils.isEmpty(lessonList)) {
				Lesson lessonToAdd = null;
				for (Lesson lesson : lessonList) {
					lessonToAdd =  new Lesson();
					lessonToAdd.setCourseUuid(ret.getUuid());
					lessonToAdd.setDayOfWeek(lesson.getDayOfWeek());
					lessonToAdd.setTimeFrom(lesson.getTimeFrom());
					lessonToAdd.setTimeTo(lesson.getTimeTo());
					// save to database
					lessonService.store(lessonToAdd);
				}
			}			
		}
		
		return ret;
	}

	@Override
	public void storeCourseParticipants(List<CourseParticipant> newCourseParticipantList, UUID courseUuid) throws ScbValidationException {
		if (courseUuid == null) {
			throw new IllegalArgumentException("courseUuid is null");
		}
		if (CollectionUtils.isEmpty(newCourseParticipantList)) {
			LOG.warn("CourseParticipantList is empty.");
			return;
		}

		// nacist kurz vcetne ucastniku - nove DAO
		Course courseDB = courseDao.getByUuid(courseUuid);
		List<CourseParticipant> courseParticipantListFinal = new ArrayList<>();
		if (!CollectionUtils.isEmpty(courseDB.getParticipantList())) {
			// courseParticipantList porovnat z puvodni mnozinou a vzbrat pouze pridane
			for (CourseParticipant item : newCourseParticipantList) {
				if (!containInList(item, courseDB.getParticipantList())) {
					courseParticipantListFinal.add(item);
				}
			}
		} else {
			courseParticipantListFinal = newCourseParticipantList;
		}
		
		// ulozeni nove pridanych ucastniku do COURSE_COURSE_PARTICIPANT
		if (!CollectionUtils.isEmpty(courseParticipantListFinal)) {
			courseParticipantDao.insetToCourse(courseParticipantListFinal, courseDB.getUuid());			
		}

		// pridat puvodni
		if (!CollectionUtils.isEmpty(courseDB.getParticipantList())) {
			courseParticipantListFinal.addAll(courseDB.getParticipantList());
		}
		// nastavit do course
		courseDB.setParticipantList(courseParticipantListFinal);
		// ulozit
		courseDao.update(courseDB);
	}

	@Override
	public void deleteParticipantFromCourse(UUID participantUuid, UUID courseUuid) {
		courseParticipantDao.deleteParticipantFromCourse(participantUuid, courseUuid);
	}
	
	@Override
	public CourseParticipant getCourseParticipantByUuid(UUID uuid) {
		return courseParticipantDao.getByUuid(uuid, true);
	}
	
	@Override
	public List<Course> getByCourseParticipantUuid(UUID courseParticipantUuid, int yearFrom, int yearTo) {
		List<Course> ret = courseDao.getByCourseParticipantUuid(courseParticipantUuid, yearFrom, yearTo);
		for (Course item : ret) {
			item.setLessonList(lessonService.getByCourse(item.getUuid()));
		}
		return ret;
	}

	private boolean containInList(CourseParticipant participant, List<CourseParticipant> courseParticipantList) {
		if (CollectionUtils.isEmpty(courseParticipantList)) {
			return false;
		}

		for (CourseParticipant item : courseParticipantList) {
			if (item.getUuid().toString().equals(participant.getUuid().toString())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<CourseLocation> getCourseLocationAll() {
		return courseLocationDao.getAll();
	}

	@Override
	public CourseLocation getCourseLocationByUuid(UUID uuid) {
		return courseLocationDao.getByUuid(uuid);
	}

	@Override
	public CourseLocation store(CourseLocation courseLocatiom) {
		if (courseLocatiom == null) {
			throw new IllegalArgumentException("courseLocatiom is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing courseLocatiom: " + courseLocatiom);
		}

		boolean insert = courseLocatiom.getUuid() == null;
		if (courseLocatiom.getUuid() == null) {
			courseLocatiom.setUuid(UUID.randomUUID());
		}
		
		if (insert) {
			courseLocationDao.insert(courseLocatiom);
		} else {
			courseLocationDao.update(courseLocatiom);
		}

		return courseLocatiom;
	}

	@Override
	public CourseCourseParticipantVO getCourseCourseParticipantVO(UUID courseParticUuid, UUID courseUuid) {
		return courseParticipantDao.getCourseCourseParticipantVO(courseParticUuid, courseUuid);
	}
	
	@Override
	public void deleteCourseLocation(CourseLocation courseLocatiom) {
		courseLocationDao.delete(courseLocatiom);
	}

	@Override
	public boolean existsByCourseLocation(UUID courseLocationUuid) {
		return courseDao.existsByCourseLocation(courseLocationUuid);
	}

	@Override
	public List<ScbUser> getTrainersByCourse(UUID courseUuid) {
		Objects.requireNonNull(courseUuid);
		return courseDao.getTrainersByCourse(courseUuid);
	}

	@Override
	public void addTrainersToCourse(List<ScbUser> trainers, UUID courseUuid) {
		Objects.requireNonNull(courseUuid);
		if (CollectionUtils.isEmpty(trainers)) {
			return;
		}
		courseDao.addTrainersToCourse(trainers, courseUuid);
		
	}

	@Override
	public void removeTrainersFromCourse(List<ScbUser> trainers, UUID courseUuid) {
		Objects.requireNonNull(courseUuid);
		if (CollectionUtils.isEmpty(trainers)) {
			return;
		}
		courseDao.removeTrainersFromCourse(trainers, courseUuid);
	}

	@Override
	public void updateState(List<UUID> courseUuidList, boolean active) {
		if (CollectionUtils.isEmpty(courseUuidList)) {
			return;
		}
		courseDao.updateState(courseUuidList, active);
	}
	
	@Override
	public void moveParticListToCourse(List<CourseParticipant> courseParticipantList, 
			UUID courseUuidSrc,
			UUID courseUuidDest,
			Calendar from, 
			Calendar to) {
		List<CourseCourseParticipantVO> courseCourseParticipantVOList = new ArrayList<>();
		for (CourseParticipant item : courseParticipantList) {
			// odstraneni z course_course_participant, puvodni reseni
//			courseService.deleteParticipantFromCourse(item.getUuid(), courseSelectedUuid);
			courseCourseParticipantVOList.add(getCourseCourseParticipantVO(item.getUuid(), courseUuidDest));
		}
		// aktualizace course_uuid v course_course_participant, cilem je nemenit varsymbol
		courseApplicationService.updateCourseParticCourseUuid(
				courseCourseParticipantVOList.stream().map(i -> i.getUuid()).collect(Collectors.toList()), 
				courseUuidSrc);
		
		// odstraneni plateb ucastnika v kurzu
		courseParticipantList.forEach(i -> paymentService.deleteByCourseAndParticipant(courseUuidDest, i.getUuid()));
		// znovu spusteni sparovani
		bankPaymentService.processPaymentPairing(from, to);

		/*
		 * Nastaveni ukonceni v kurzu pro penechani dochazky
		 * Problem:
		 * 	- zůstávají neunikátná záznamy v course_course_participant pro dvojici course_uuid  a course_participant_uuid, pada pri zpetnem  prirazeni
		 * 	- nepůjde odstranit přihlášku, protože neaktiví účastnící nejsou na detailu vidět a nejde je odebrat
		*/
//		courseApplicationService.updateCourseParticInterruption(
//				courseCourseParticipantVOList.stream().map(i -> i.getUuid()).collect(Collectors.toList()), 
//				Calendar.getInstance().getTime());
		
	}
}
