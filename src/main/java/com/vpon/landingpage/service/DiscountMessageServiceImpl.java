package com.vpon.landingpage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vpon.landingpage.dao.DiscountMessageDao;
import com.vpon.landingpage.domain.Mazda6DiscountMessage;

@Service
public class DiscountMessageServiceImpl implements DiscountMessageService {

	@Autowired
	private DiscountMessageDao dao;
	
	@Override
	public Mazda6DiscountMessage save(Mazda6DiscountMessage message) {
		return dao.save(message);
	}

	@Override
	public List<Mazda6DiscountMessage> getNearDiscountMessage(double lon,
			double lat, int limit) {
		return dao.getNearDiscountMessage(lon, lat, limit);
	}

	@Override
	public Mazda6DiscountMessage getDiscountByShopId(String shopId) {
		return dao.getDiscountByShopId(shopId);
	}

}
