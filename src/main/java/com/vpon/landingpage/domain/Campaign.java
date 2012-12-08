package com.vpon.landingpage.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Campaign {
	
	private String campaignId;
	private String campaignName;
	
	private Date createTime;
	
	private List<Map<String,Object>> campaignDataList = new ArrayList<Map<String, Object>>();
	
	private Map<String,Object> campaignDataMap = new HashMap<String,Object>();
	
	
	
//	private boolean hasSendSMS;
//	
//	private String phoneSMS;
//	
//	private String responseSMS;
	
	public Map<String, Object> getCampaignDataMap() {
		return campaignDataMap;
	}

	public void setCampaignDataMap(Map<String, Object> campaignDataMap) {
		this.campaignDataMap = campaignDataMap;
	}

	private String couponNum;
		
	//private List<Click> clickList = new ArrayList<Click>();
	
	private long counter;
	
	
	
	

	
	public String getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(String couponNum) {
		this.couponNum = couponNum;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public void incCounter(){
		this.counter++;
	}
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<Map<String, Object>> getCampaignDataList() {
		return campaignDataList;
	}

	public void setCampaignDataList(List<Map<String, Object>> campaignDataList) {
		this.campaignDataList = campaignDataList;
	}
	
	public void addCampaignData(Map<String, Object> data){
		if(data != null){
			data.put("timeStamp", System.currentTimeMillis());
		}
		this.campaignDataList.add(data);
	}

//	public boolean isHasSendSMS() {
//		return hasSendSMS;
//	}
//
//	public void setSendSMS(boolean hasSendSMS) {
//		this.hasSendSMS = hasSendSMS;
//	}

//	public List<Click> getClickList() {
//		return clickList;
//	}
//
//	public void setClickList(List<Click> clickList) {
//		this.clickList = clickList;
//	}
}
