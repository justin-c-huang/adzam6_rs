package com.vpon.landingpage.service;

import com.vpon.landingpage.domain.CampaignRecorder;

public interface CampaignRecorderService {
	CampaignRecorder save(CampaignRecorder campRecorder);
	CampaignRecorder getCampaignRecorderByCampaignId(String id);
	void incGrantCouponNumByCampaignId(String campaignId);
	//long getNumberOfAvailableCouponByCampaignId(String id);
	
	//TODO:
	//long getNumOfAllHasGrantCoupon();
	
	void incCampaignRecorderUserCounter(String campaignId);
}
