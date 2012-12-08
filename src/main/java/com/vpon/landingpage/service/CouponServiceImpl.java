package com.vpon.landingpage.service;

import com.vpon.landingpage.util.GenernalFunction;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vpon.landingpage.dao.CouponDao;
import com.vpon.landingpage.domain.Coupon;
import com.vpon.landingpage.domain.Mazda6Coupon;


@Service
public class CouponServiceImpl implements CouponService {

	private static final Logger logger = Logger
			.getLogger(CouponServiceImpl.class);
	
	@Autowired
	private CouponDao dao;
	
	@Override
	public Coupon save(Coupon coupon) {
		return dao.save(coupon);
	}
	
	@Override
	public long getCouponNum(){
		return dao.getCouponNum();
	}
	
	@Override
	public boolean canSendCouponByCampaignIdAndUserId(String campaignId,String userId){
		Coupon coupon = dao.getMostRecentCouponByCampaignIdAndUserId(campaignId, userId);
		if(coupon == null){
			return true;
		}
		Date couponDate = coupon.getDate();
		
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(couponDate);
        c.add(Calendar.HOUR,24);
        Date couponAdd24Date = c.getTime();
        Date nowDate = GenernalFunction.getGMTDate();
        if(couponAdd24Date.compareTo(nowDate) < 0 ){
        	return true;
        }
        return false;
	}

	@Override
	public Coupon getCouponByCampaignIdAndUserId(String campaignId,
			String userId) {
		return dao.getCouponByCampaignIdAndUserId(campaignId, userId);
	}
	
//	@Override
//	public boolean isCouponIdOccupied(String couponId){
//		return dao.isCouponIdOccupied(couponId);
//	}
	
	@Override
	public Mazda6Coupon getMazda6Coupon(){	
		return dao.getMazda6Coupon();
	}
	
	@Override
	public Mazda6Coupon getMazda6CouponAndUpdate(){
		return dao.getMazda6CouponAndUpdate();
	}
	
	@Override
	public boolean hasAvailableMazda6Coupon(){
		return dao.hasAvailableMazda6Coupon();
	}
	
	@Override
	public Mazda6Coupon saveMazda6Coupon(Mazda6Coupon coupon){
		return dao.saveMazda6Coupon(coupon);
	}

}
