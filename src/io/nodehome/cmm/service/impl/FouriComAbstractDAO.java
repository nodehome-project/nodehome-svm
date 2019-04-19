package io.nodehome.cmm.service.impl;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;

import io.nodehome.cmm.service.dataaccess.FouriAbstractMapper;

public abstract class FouriComAbstractDAO extends FouriAbstractMapper {

	@Resource(name="fouri.sqlSession")
	public void setSqlSessionFactory(SqlSessionFactory sqlSession) {
		super.setSqlSessionFactory(sqlSession);
	}

}
