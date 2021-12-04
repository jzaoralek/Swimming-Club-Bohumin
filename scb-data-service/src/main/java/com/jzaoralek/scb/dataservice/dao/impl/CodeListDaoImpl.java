package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CodeListDao;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;

@Repository
public class CodeListDaoImpl extends BaseJdbcDao implements CodeListDao {

	private static final String ITEM_TYPE_PARAM = "item_type";
	private static final String NAME_PARAM = "name";
	private static final String DESCRIPTION_PARAM = "descriptio";

	private static final String SELECT_BY_TYPE = "SELECT uuid, item_type, name, description, modif_at, modif_by FROM codelist_item WHERE item_type = :" + ITEM_TYPE_PARAM;
	private static final String SELECT_BY_TYPE_AND_NAME = "SELECT uuid, item_type, name, description, modif_at, modif_by FROM codelist_item WHERE item_type = :" + ITEM_TYPE_PARAM + " AND name = :" + NAME_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, item_type, name, description, modif_at, modif_by FROM codelist_item WHERE uuid = :" + UUID_PARAM + " ORDER BY name";
	private static final String INSERT = "INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) VALUES (:"+UUID_PARAM+", :"+ITEM_TYPE_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";
	private static final String UPDATE = "UPDATE codelist_item SET item_type = :"+ITEM_TYPE_PARAM+", name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM codelist_item where uuid = :" + UUID_PARAM;

	@Autowired
	public CodeListDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<CodeListItem> getItemListByType(CodeListType type) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(ITEM_TYPE_PARAM, type.name());
		return namedJdbcTemplate.query(SELECT_BY_TYPE, paramMap, new CodeListItemRowMapper());
	}

	@Override
	public CodeListItem getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CodeListItemRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public CodeListItem getByTypeAndName(CodeListType type, String name) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(ITEM_TYPE_PARAM, type.name()).addValue(NAME_PARAM, name);
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_TYPE_AND_NAME, paramMap, new CodeListItemRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(CodeListItem item) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(item, paramMap);
		paramMap.addValue(ITEM_TYPE_PARAM, item.getType() != null ? item.getType().name() : null);
		paramMap.addValue(NAME_PARAM, item.getName());
		paramMap.addValue(DESCRIPTION_PARAM, item.getDescription());

		namedJdbcTemplate.update(INSERT, paramMap);

	}

	@Override
	public void update(CodeListItem item) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(item, paramMap);
		paramMap.addValue(ITEM_TYPE_PARAM, item.getType() != null ? item.getType().name() : null);
		paramMap.addValue(NAME_PARAM, item.getName());
		paramMap.addValue(DESCRIPTION_PARAM, item.getDescription());

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(CodeListItem item) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, item.getUuid().toString()));
	}

	public static final class CodeListItemRowMapper implements RowMapper<CodeListItem> {

		@Override
		public CodeListItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			CodeListItem ret = new CodeListItem();
			fetchIdentEntity(rs, ret);
			ret.setType(CodeListType.valueOf(rs.getString("item_type")));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));

			return ret;
		}
	}
}
