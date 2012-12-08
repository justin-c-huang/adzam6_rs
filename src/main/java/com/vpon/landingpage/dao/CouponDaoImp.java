package com.vpon.landingpage.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.vpon.landingpage.domain.Coupon;
import com.vpon.landingpage.domain.Mazda6Coupon;

@Repository
public class CouponDaoImp implements CouponDao {

	
	@Autowired
	MongoOperations mongoOperations;
	
	@Override
	public Coupon save(Coupon coupon) {
		mongoOperations.save(coupon);
		return coupon;
	}
	
	@Override
	public long getCouponNum(){
		Query query = new Query(where("isOccupancy").is(true));
		return mongoOperations.count(query, Coupon.class);
	}

	@Override
	public Coupon getCouponByCampaignIdAndUserId(String campaignId,
			String userId) {
		Query query = new Query(where("campaignId").is(campaignId));
		query.addCriteria(where("userId").is(userId));
		 return mongoOperations.findOne(query, Coupon.class);
	}
	
	@Override
	public Coupon getMostRecentCouponByCampaignIdAndUserId(String campaignId,String userId){
		Query query = new Query(where("campaignId").is(campaignId));
		query.addCriteria(where("userId").is(userId));
		query.sort().on("date", Order.DESCENDING);
		List<Coupon> couponList = mongoOperations.find(query, Coupon.class);
		if(couponList != null && couponList.size() > 0){
			return couponList.get(0);
		}else{
			return null;
		}
	}
	
//	@Override
//	public boolean isCouponIdOccupied(String couponId){
//		Query query = new Query(where("couponId").is(couponId));
//		return (mongoOperations.findOne(query, Coupon.class) == null) ? false
//				: true;
//		
//	}
	
	@Override
	public Mazda6Coupon getMazda6Coupon(){	
		Query query = new Query(where("isOccupancy").is(false));
		return mongoOperations.findOne(query, Mazda6Coupon.class);
	}
	
	@Override
	public Mazda6Coupon getMazda6CouponAndUpdate(){	
		Query query = new Query(where("isOccupancy").is(false));
		Update update = new Update();
		update.set("isOccupancy", true);
		return mongoOperations.findAndModify(query,update, Mazda6Coupon.class);
	}
	
	@Override
	public boolean hasAvailableMazda6Coupon(){
		if(getMazda6Coupon() != null){
			return true;
		}else{
			return false;
		}
			
	}
	
	@Override
	public Mazda6Coupon saveMazda6Coupon(Mazda6Coupon coupon){
		mongoOperations.save(coupon);
		
		return coupon;
	}

}
