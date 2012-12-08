package com.vpon.landingpage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vpon.landingpage.dao.ClickDao;
import com.vpon.landingpage.domain.Click;


@Service
public class ClickSerivceImpl implements ClickService {

	@Autowired
	private ClickDao dao;
	
	@Override
	public Click save(Click click) {
		return dao.save(click);
	}

}
