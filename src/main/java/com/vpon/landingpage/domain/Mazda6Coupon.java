package com.vpon.landingpage.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Mazda6Coupon {
	@Id
	private String id;
	
	private String couponId;
	
	private String campaignId;
	
	@Indexed
	private boolean isOccupancy = false;
	
	private String userId;

	
	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
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

	public boolean isOccupancy() {
		return isOccupancy;
	}

	public void setOccupancy(boolean isOccupancy) {
		this.isOccupancy = isOccupancy;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
