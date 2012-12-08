package com.vpon.landingpage.dao;

import java.util.List;

import com.vpon.landingpage.domain.User;
import com.vpon.landingpage.domain.UserLocation;

public interface UserDao {
	
	User getUser(String id);

	User save(User user);
	
	User getUserByImei(String imei);
	
	UserLocation saveUserLocation(UserLocation userLocation);
	
	UserLocation getUserLocationByUserId(String userId);
	
	List<UserLocation> getNearUserLocation(double lon,double lat,int limit);
}
