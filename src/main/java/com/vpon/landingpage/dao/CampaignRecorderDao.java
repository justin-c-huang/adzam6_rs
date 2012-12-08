package com.vpon.landingpage.dao;

import java.util.List;

import com.vpon.landingpage.domain.CampaignRecorder;

public interface CampaignRecorderDao {
	CampaignRecorder save(CampaignRecorder campRecord);
	CampaignRecorder getCampaignRecorderByCampaignId(String id);
	List<CampaignRecorder> getAllOfCampaignRecorder();
	void incCampaignRecorderUserCounter(String campaignId);
	void incGrantCouponNumByCampaignId(String campaignId);
}
