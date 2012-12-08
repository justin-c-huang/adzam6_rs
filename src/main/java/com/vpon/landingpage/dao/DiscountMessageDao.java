package com.vpon.landingpage.dao;

import java.util.List;

import com.vpon.landingpage.domain.Mazda6DiscountMessage;

public interface DiscountMessageDao {
	
	Mazda6DiscountMessage save(Mazda6DiscountMessage message);
	
	List<Mazda6DiscountMessage> getNearDiscountMessage(double lon,double lat,int limit);
	
	Mazda6DiscountMessage getDiscountByShopId(String shopId);
}
