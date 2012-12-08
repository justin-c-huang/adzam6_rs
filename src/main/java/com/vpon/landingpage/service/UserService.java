package com.vpon.landingpage.service;

import java.util.List;

import com.vpon.landingpage.domain.Campaign;
import com.vpon.landingpage.domain.User;
import com.vpon.landingpage.domain.UserLocation;

public interface UserService {

	User getUser(String id);
	
	User save(User user);
	
	User getUserByImei(String imei);
	
	User createNewUser(String imei,double lat, double lon,String region);
	
	boolean isExistCampaign(User user,String campaignId);
	
	Campaign getCampaignByCampaignId(User user,String compaignId);
	
	UserLocation saveUserLocation(UserLocation userLocation);
	
	UserLocation getUserLocationByUserId(String userId);
	
	List<UserLocation> getNearUserLocation(double lon,double lat,int limit);
	
}
