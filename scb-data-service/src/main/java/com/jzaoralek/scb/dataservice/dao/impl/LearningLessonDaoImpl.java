package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.LearningLessonDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;

@Repository
public class LearningLessonDaoImpl extends BaseJdbcDao implements LearningLessonDao {

	private static final String LESSON_DATE_PARAM = "LESSON_DATE";

	private static final String SELECT_BY_LESSON= "SELECT uuid, lesson_date, time_from, time_to, description, modif_at, modif_by, lesson_uuid FROM learning_lesson WHERE lesson_uuid = " + LESSON_UUID_PARAM;
	private static final String INSERT = "INSERT INTO learning_lesson "
			+ "(uuid, lesson_date, time_from, time_to, description, modif_at, modif_by, lesson_uuid) "
			+ "VALUES (:"+UUID_PARAM+", :"+LESSON_DATE_PARAM+", :"+TIME_FROM_PARAM+", :"+TIME_TO_PARAM+", :"+DESCRIPTION_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+LESSON_UUID_PARAM+")";

	private static final String UPDATE = "UPDATE learning_lesson "
			+ "SET lesson_date = :"+LESSON_DATE_PARAM+", time_from = :"+TIME_FROM_PARAM+", time_to = :"+TIME_TO_PARAM+", description = :"+DESCRIPTION_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", lesson_uuid = :"+LESSON_UUID_PARAM+" "
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
		return namedJdbcTemplate.query(SELECT_BY_LESSON, paramMap, new LearningLessonRowMapper(courseParticipantDao, lessonDao));
	}

	@Override
	public void insert(LearningLesson lesson) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(lesson, paramMap);
		paramMap.addValue(LESSON_DATE_PARAM, lesson.getLessonDate());
		paramMap.addValue(TIME_FROM_PARAM, lesson.getTimeFrom());
		paramMap.addValue(TIME_TO_PARAM, lesson.getTimeTo());
		paramMap.addValue(DESCRIPTION_PARAM, lesson.getDescription());
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

		public LearningLessonRowMapper(CourseParticipantDao courseParticipantDao, LessonDao lessonDao) {
			this.courseParticipantDao = courseParticipantDao;
			this.lessonDao = lessonDao;
		}

		@Override
		public LearningLesson mapRow(ResultSet rs, int rowNum) throws SQLException {
			LearningLesson ret = new LearningLesson();
			fetchIdentEntity(rs, ret);
			ret.setDescription(rs.getString("description"));
			UUID lessonUuid = UUID.fromString(rs.getString("lesson_uuid"));
			ret.setLesson(lessonDao.getByUuid(lessonUuid));
			ret.setParticipantList(courseParticipantDao.getByLearningLessonUuid(ret.getUuid()));
			ret.setLessonDate(rs.getDate("lesson_date"));
			ret.setTimeFrom(rs.getTime("time_from"));
			ret.setTimeTo(rs.getTime("time_to"));

			return ret;
		}
	}
}