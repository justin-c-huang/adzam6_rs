package com.vpon.landingpage.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.vpon.landingpage.domain.CampaignRecorder;
import com.vpon.landingpage.domain.Mazda6Coupon;

@Repository
public class CampaignRecorderDaoImpl implements CampaignRecorderDao{
	
	
	@Autowired
	MongoOperations mongoOperations;
	
	@Override
	public CampaignRecorder save(CampaignRecorder campRecord){
		mongoOperations.save(campRecord);
		return campRecord;
	}
	
	@Override
	public CampaignRecorder getCampaignRecorderByCampaignId(String id){
		return mongoOperations.findOne(new Query(where("campaignId").is(id)),
				CampaignRecorder.class);
	}
	
	@Override
	public List<CampaignRecorder> getAllOfCampaignRecorder(){
		Query query = new Query();
		return mongoOperations.find(query, CampaignRecorder.class);
	}
	
	@Override
	public void incCampaignRecorderUserCounter(String campaignId){
		Query query = new Query(where("campaignId").is(campaignId));
		Update update = new Update();
		update.inc("userCount", 1);
		mongoOperations.findAndModify(query,update, CampaignRecorder.class);
	}
	
	@Override
	public void incGrantCouponNumByCampaignId(String campaignId){
		Query query = new Query(where("campaignId").is(campaignId));
		Update update = new Update();
		update.inc("currentGrantCoupon", 1);
		mongoOperations.findAndModify(query,update, CampaignRecorder.class);
	}

}
