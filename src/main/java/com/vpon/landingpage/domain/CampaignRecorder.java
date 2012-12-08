package com.vpon.landingpage.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CampaignRecorder {
	@Id
	private String id;
	@Indexed
	private String campaignId;
	private Date startDate;
	private Date endDate;
	private long userCount;
	private long totalOfCoupon = 10000L;
	private long currentGrantCoupon;
	
	
	public long getTotalOfCoupon() {
		return totalOfCoupon;
	}
	
	public void setTotalOfCoupon(long totalOfCoupon) {
		this.totalOfCoupon = totalOfCoupon;
	}
	
	public void incCurrentGrantCoupon(){
		currentGrantCoupon++;
	}
	
	public long getCurrentGrantCoupon() {
		return currentGrantCoupon;
	}
	
	public void setCurrentGrantCoupon(long currentGrantCoupon) {
		this.currentGrantCoupon = currentGrantCoupon;
	}
	
	public void incUserCount(){
		userCount++;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public long getUserCount() {
		return userCount;
	}
	public void setUserCount(long userCount) {
		this.userCount = userCount;
	}
	
	
	
}
