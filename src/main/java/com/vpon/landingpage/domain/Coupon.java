package com.vpon.landingpage.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Coupon {
	@Id
	private String id;
	
	@Indexed
	private String campaignId;
	
	@Indexed
	private String userId;
	
	private Date date;
	
	private String couponId;
	
	private String smsReturnStr;
	
	private String phone;
	
	private boolean isOccupancy = false;
	
	
	public String getSmsReturnStr() {
		return smsReturnStr;
	}

	public void setSmsReturnStr(String smsReturnStr) {
		this.smsReturnStr = smsReturnStr;
	}

	public boolean isOccupancy() {
		return isOccupancy;
	}

	public void setOccupancy(boolean isOccupancy) {
		this.isOccupancy = isOccupancy;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
