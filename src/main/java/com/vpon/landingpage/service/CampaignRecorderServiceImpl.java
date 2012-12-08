package com.vpon.landingpage.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vpon.landingpage.dao.CampaignRecorderDao;
import com.vpon.landingpage.domain.CampaignRecorder;

@Service
public class CampaignRecorderServiceImpl implements CampaignRecorderService{
	
	private static final Logger logger = Logger
			.getLogger(CampaignRecorderServiceImpl.class);
	
	@Autowired
	private CampaignRecorderDao dao;
	
	public CampaignRecorder save(CampaignRecorder campRecorder){
		dao.save(campRecorder);
		return campRecorder;
	}
	
	public CampaignRecorder getCampaignRecorderByCampaignId(String id){
		return dao.getCampaignRecorderByCampaignId(id);
	}
	
	@Override
	public void incGrantCouponNumByCampaignId(String campaignId){
		dao.incGrantCouponNumByCampaignId(campaignId);
	}
	
//	@Override
//	public long getNumberOfAvailableCouponByCampaignId(String id){
//		CampaignRecorder record = dao.getCampaignRecorderByCampaignId(id);
//		if(record ==  null){
//			//TODO:
//			return 10000L;
//		}else{
//			return record.getTotalOfCoupon() - record.getCurrentGrantCoupon();
//		}
//	}
	
	//TODO:
//	@Override
//	public long getNumOfAllHasGrantCoupon(){
//		long total = 0;
//		List<CampaignRecorder> campList = dao.getAllOfCampaignRecorder();
//		for(CampaignRecorder camp:campList){
//			total+=camp.getCurrentGrantCoupon();
//		}
//		long remain = 10000L - total;
//		if(remain > 0)
//			return remain;
//		else
//			return 0L;
//		
//	}
	
	@Override
	public void incCampaignRecorderUserCounter(String campaignId){
		dao.incCampaignRecorderUserCounter(campaignId);
	}

}
