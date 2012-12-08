package com.vpon.landingpage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import com.vpon.landingpage.domain.Click;

@Repository
public class ClickDaoImpl implements ClickDao {

	@Autowired
	MongoOperations mongoOperations;
	
	@Override
	public Click save(Click click) {
		mongoOperations.save(click);
		return click;
	}

}
