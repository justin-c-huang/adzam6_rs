package com.vpon.landingpage.service;

import com.vpon.landingpage.domain.Coupon;
import com.vpon.landingpage.domain.Mazda6Coupon;

public interface CouponService {
	
	Coupon save(Coupon coupon);
	
	Coupon getCouponByCampaignIdAndUserId(String campaignId,String userId);
	
	//boolean isCouponIdOccupied(String couponId);
	
	Mazda6Coupon getMazda6Coupon();
	
	Mazda6Coupon getMazda6CouponAndUpdate();
	
	boolean hasAvailableMazda6Coupon();
	
	Mazda6Coupon saveMazda6Coupon(Mazda6Coupon coupon);
	
	long getCouponNum();
	
	boolean canSendCouponByCampaignIdAndUserId(String campaignId,String userId);
	
}
