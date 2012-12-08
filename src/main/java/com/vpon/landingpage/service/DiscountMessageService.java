package com.vpon.landingpage.service;

import java.util.List;

import com.vpon.landingpage.domain.Mazda6DiscountMessage;

public interface DiscountMessageService {

	Mazda6DiscountMessage save(Mazda6DiscountMessage message);

	List<Mazda6DiscountMessage> getNearDiscountMessage(double lon, double lat,
			int limit);

	Mazda6DiscountMessage getDiscountByShopId(String shopId);

}
