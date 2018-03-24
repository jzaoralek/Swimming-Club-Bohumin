package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.LearningLessonDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.service.impl.CourseApplicationServiceImpl;

@Repository
public class LearningLessonDaoImpl extends BaseJdbcDao implements LearningLessonDao {

	private static final Logger LOG = LoggerFactory.getLogger(LearningLessonDaoImpl.class);
	
	private static final String LESSON_DATE_PARAM = "LESSON_DATE";
	private static final String ADDITIONAL_COLUMN_INT_PARAM = "ADDITIONAL_COLUMN_INT";

	private static final String SELECT_BY_LESSON = "SELECT uuid, lesson_date, time_from, time_to, description, additional_column_int, modif_at, modif_by, lesson_uuid FROM learning_lesson WHERE lesson_uuid = :" + LESSON_UUID_PARAM;
	private static final String SELECT_BY_COURSE = "SELECT uuid, lesson_date, time_from, time_to, description, additional_column_int, modif_at, modif_by, lesson_uuid FROM learning_lesson WHERE lesson_uuid IN (select uuid from lesson where course_uuid = :"+COURSE_UUID_PARAM+")";

	private static final String SELECT_BY_COURSE_WITH_PARTICIPANTS =
			" SELECT ls.uuid, ls.lesson_date, ls.time_from, ls.time_to, ls.description, ls.additional_column_int, ls.modif_at, ls.modif_by, ls.lesson_uuid "
			+ ", cp.uuid \"COURSE_PARTICIPANT_UUID\"  "
			+ ", c.firstname, c.surname "
			+ "FROM participant_learning_lesson pls "
			+ "LEFT JOIN learning_lesson ls ON (ls.uuid = pls.learning_lesson_uuid) "
			+ "LEFT JOIN course_participant cp ON (pls.course_participant_uuid = cp.uuid) "
			+ "LEFT JOIN contact c ON (cp.contact_uuid = c.uuid) "
			+ "WHERE lesson_uuid IN (select uuid from lesson where course_uuid = :"+COURSE_UUID_PARAM+") "
			+ "ORDER BY lesson_date, time_from; ";

	private static final String SELECT_BY_UUID = "SELECT uuid, lesson_date, time_from, time_to, description, additional_column_int, modif_at, modif_by, lesson_uuid FROM learning_lesson WHERE uuid = :" + UUID_PARAM;
	private static final String INSERT = "INSERT INTO learning_lesson "
			+ "(uuid, lesson_date, time_from, time_to, description, additional_column_int, modif_at, modif_by, lesson_uuid) "
			+ "VALUES (:"+UUID_PARAM+", :"+LESSON_DATE_PARAM+", :"+TIME_FROM_PARAM+", :"+TIME_TO_PARAM+", :"+DESCRIPTION_PARAM+", :"+ADDITIONAL_COLUMN_INT_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+LESSON_UUID_PARAM+")";

	private static final String UPDATE = "UPDATE learning_lesson "
			+ "SET lesson_date = :"+LESSON_DATE_PARAM+", time_from = :"+TIME_FROM_PARAM+", time_to = :"+TIME_TO_PARAM+", description = :"+DESCRIPTION_PARAM+", additional_column_int = :"+ADDITIONAL_COLUMN_INT_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", lesson_uuid = :"+LESSON_UUID_PARAM+" "
			+ "WHERE uuid = :"+UUID_PARAM;

	private static final String DELETE = "DELETE FROM learning_lesson WHERE uuid = :"+UUID_PARAM;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private LessonDao lessonDao;

	@Autowired
	public LearningLessonDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<LearningLesson> getByLesson(UUID lessonUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(LESSON_UUID_PARAM, lessonUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_LESSON, paramMap, new LearningLessonRowMapper(courseParticipantDao, lessonDao, false));
	}

	@Override
	public List<LearningLesson> getByCourse(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE, paramMap, new LearningLessonRowMapper(courseParticipantDao, lessonDao, false));
	}

	@Override
	public List<LearningLesson> getByCourseWithFilledParticipantList(UUID courseUuid) {
		List<LearningLesson> flatStructure = getByCourseWithFlatParticipantList(courseUuid);
		if (CollectionUtils.isEmpty(flatStructure)) {
			return Collections.emptyList();
		}
		List<LearningLesson> ret = new ArrayList<LearningLesson>();
		Map<UUID,List<CourseParticipant>> lessonMap = new HashMap<>();
		for (LearningLesson item : flatStructure) {
			if (!CollectionUtils.isEmpty(item.getParticipantList())) {
				if (lessonMap.containsKey(item.getUuid())) {
					lessonMap.get(item.getUuid()).addAll(new ArrayList<CourseParticipant>(item.getParticipantList()));
				} else {
					lessonMap.put(item.getUuid(), new ArrayList<CourseParticipant>(item.getParticipantList()));
				}				
			} else {
				// pridat lekce, kde nebyl ani jeden ucastnik
				ret.add(item);
			}
			
		}
		
		LearningLesson lesson = null;
		for (Entry<UUID, List<CourseParticipant>> entry : lessonMap.entrySet()) {
			lesson = getItemFromList(entry.getKey(), flatStructure);
			lesson.setParticipantList(entry.getValue());
			ret.add(lesson);
		}
		
		Collections.sort(ret, LearningLesson.LESSON_DATE_TIME_COMPARATOR);
		
		// TODO: prevest na linkedList, u kazde lekce zjistit jestli je první v mesici
		// pokud je prvni v kolekci - ano
		// nacist predchozi a zjistit mesic, pokud jiny nez u plozky - ano
		
		LearningLesson learningLesson = null;
		LearningLesson learningLessonPrev = null;
		for (ListIterator<LearningLesson> iterator = ret.listIterator(); iterator.hasNext();) {
			if (!iterator.hasPrevious()) {
				iterator.next().setFirstInMonth(true);
			} else {
				learningLessonPrev = iterator.previous();
				iterator.next();
				learningLesson = iterator.next();
				if (learningLesson.getLessonDate().getMonth() != learningLessonPrev.getLessonDate().getMonth()) {
					learningLesson.setFirstInMonth(true);
				}							
			}
		}
		
		return ret;
	}

	private LearningLesson getItemFromList(UUID uuid, List<LearningLesson> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		for (LearningLesson lessonItem : list) {
			if (lessonItem.getUuid().toString().equals(uuid.toString())) {
				return lessonItem;
			}
		}

		return null;
	}

	/**
	 * Vraci flat strukturu seznamu learning lesson vzdy s jednim ucastnikem v seznamu.
	 * Potreba seskupit a ucastniky vlozit do listu pro kazdou polozku
	 * @param courseUuid
	 * @return
	 */
	private List<LearningLesson> getByCourseWithFlatParticipantList(UUID courseUuid) {
		try {
			MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());			
			return namedJdbcTemplate.query(SELECT_BY_COURSE_WITH_PARTICIPANTS, paramMap, new LearningLessonWithParticRowMapper(lessonDao));
		} catch (Exception e) {
			LOG.error("Unexpected exception during select: " + SELECT_BY_COURSE_WITH_PARTICIPANTS, e);
			e.printStackTrace();
		}
		return new ArrayList<LearningLesson>();
	}

	@Override
	public LearningLesson getByUUID(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new LearningLessonRowMapper(courseParticipantDao, lessonDao, true));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(LearningLesson lesson) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(lesson, paramMap);
		paramMap.addValue(LESSON_DATE_PARAM, lesson.getLessonDate());
		paramMap.addValue(TIME_FROM_PARAM, lesson.getTimeFrom());
		paramMap.addValue(TIME_TO_PARAM, lesson.getTimeTo());
		paramMap.addValue(DESCRIPTION_PARAM, lesson.getDescription());
		paramMap.addValue(ADDITIONAL_COLUMN_INT_PARAM, lesson.getAdditionalColumnInt());
		paramMap.addValue(LESSON_UUID_PARAM, lesson.getLesson().getUuid().toString());

		namedJdbcTemplate.update(INSERT, paramMap);

		if (!CollectionUtils.isEmpty(lesson.getParticipantList())) {
			courseParticipantDao.insertToLearningLesson(lesson.getUuid(), lesson.getParticipantList());
		}
	}

	@Override
	public void update(LearningLesson lesson) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(lesson, paramMap);
		paramMap.addValue(LESSON_DATE_PARAM, lesson.getLessonDate());
		paramMap.addValue(TIME_FROM_PARAM, lesson.getTimeFrom());
		paramMap.addValue(TIME_TO_PARAM, lesson.getTimeTo());
		paramMap.addValue(DESCRIPTION_PARAM, lesson.getDescription());
		paramMap.addValue(ADDITIONAL_COLUMN_INT_PARAM, lesson.getAdditionalColumnInt());
		paramMap.addValue(LESSON_UUID_PARAM, lesson.getLesson().getUuid().toString());

		namedJdbcTemplate.update(UPDATE, paramMap);

		// remove all participants
		courseParticipantDao.deleteAllFromLearningLesson(lesson.getUuid());
		// add participants
		if (!CollectionUtils.isEmpty(lesson.getParticipantList())) {
			courseParticipantDao.insertToLearningLesson(lesson.getUuid(), lesson.getParticipantList());
		}
	}

	@Override
	public void delete(LearningLesson lesson) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, lesson.getUuid().toString()));
		// remove all participants
		courseParticipantDao.deleteAllFromLearningLesson(lesson.getUuid());
	}

	public static final class LearningLessonRowMapper implements RowMapper<LearningLesson> {
		private final CourseParticipantDao courseParticipantDao;
		private final LessonDao lessonDao;
		private final boolean detail;

		public LearningLessonRowMapper(CourseParticipantDao courseParticipantDao, LessonDao lessonDao, boolean detail) {
			this.courseParticipantDao = courseParticipantDao;
			this.lessonDao = lessonDao;
			this.detail = detail;
		}

		@Override
		public LearningLesson mapRow(ResultSet rs, int rowNum) throws SQLException {
			LearningLesson ret = new LearningLesson();
			fetchIdentEntity(rs, ret);
			ret.setDescription(rs.getString("description"));
			ret.setAdditionalColumnInt(rs.getInt("additional_column_int"));
			ret.setLessonDate(transDate(rs.getDate("lesson_date")));
			ret.setTimeFrom(rs.getTime("time_from"));
			ret.setTimeTo(rs.getTime("time_to"));
			UUID lessonUuid = UUID.fromString(rs.getString("lesson_uuid"));
			ret.setLesson(lessonDao.getByUuid(lessonUuid));

			if (this.detail) {
				ret.setParticipantList(courseParticipantDao.getByLearningLessonUuid(ret.getUuid()));
			}

			return ret;
		}
	}

	public static final class LearningLessonWithParticRowMapper implements RowMapper<LearningLesson> {

		private final LessonDao lessonDao;

		public LearningLessonWithParticRowMapper(LessonDao lessonDao) {
			this.lessonDao = lessonDao;
		}

		@Override
		public LearningLesson mapRow(ResultSet rs, int rowNum) throws SQLException {
			LearningLesson ret = new LearningLesson();
			fetchIdentEntity(rs, ret);
			ret.setDescription(rs.getString("description"));
			ret.setAdditionalColumnInt(rs.getInt("additional_column_int"));
			ret.setLessonDate(transDate(rs.getDate("lesson_date")));
			ret.setTimeFrom(rs.getTime("time_from"));
			ret.setTimeTo(rs.getTime("time_to"));
			UUID lessonUuid = UUID.fromString(rs.getString("lesson_uuid"));
//			ret.setLesson(lessonDao.getByUuid(lessonUuid));

			String courseParticUuid = rs.getString("COURSE_PARTICIPANT_UUID");
			if (StringUtils.hasText(courseParticUuid)) {
				CourseParticipant coursePartic = new CourseParticipant();
				coursePartic.setUuid(UUID.fromString(courseParticUuid));
				Contact contact = new Contact();
				contact.setFirstname(rs.getString("firstname"));
				contact.setSurname(rs.getString("surname"));
				coursePartic.setContact(contact);
				
				ret.setParticipantList(Arrays.asList(coursePartic));				
			}

			return ret;
		}
	}
}