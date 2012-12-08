package com.vpon.landingpage.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vpon.landingpage.RestfulController;
import com.vpon.landingpage.dao.UserDao;
import com.vpon.landingpage.domain.Campaign;
import com.vpon.landingpage.domain.CampaignRecorder;
import com.vpon.landingpage.domain.User;
import com.vpon.landingpage.domain.UserLocation;
import com.vpon.landingpage.util.GenernalFunction;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao dao ;
	@Autowired
	CampaignRecorderService campaignRecorderService;
	
	private static final Logger logger = Logger
			.getLogger(UserServiceImpl.class);
	
	public User getUser(String id) {
		return dao.getUser(id);
	}

	public User save(User user) {
		user.setUpdateTime(GenernalFunction.getGMTDate());
		return dao.save(user);
	}
	
	public User getUserByImei(String imei){
		return dao.getUserByImei(imei);
	}
	
	public User createNewUser(String imei,double lat, double lon,String region){
		final String UNKNOWN = "UNKNOWN";
		
		User user = new User();
		
		if(imei != null)
			user.setImei(imei);
		else
			user.setImei(UNKNOWN);
		
		if(region != null)
			user.setRegion(region);
		else
			user.setRegion(UNKNOWN);
		Date createDate = GenernalFunction.getGMTDate();
		//logger.info("------------------------------>>createTime:"+createDate);
		user.setCreateTime(createDate);
		user.getLocation().put("lat", lat);
		user.getLocation().put("lon", lon);
		
		return user;
	}
	
	
	public boolean isExistCampaign(User user,String campaignId){
		if(user == null || campaignId == null){
			return false;
		}
		List<Campaign> compList = user.getCampaignList();
		for(Campaign camp: compList){
			if(camp.getCampaignId().equals(campaignId)){
				return true;
			}
		}
		
		return false;
	}
	
	public Campaign getCampaignByCampaignId(User user,String campaignId){
		
		if(user == null || campaignId == null){
			return null;
		}
		
		List<Campaign> compList = user.getCampaignList();
		for(Campaign camp: compList){
			if(camp.getCampaignId().equals(campaignId)){
				return camp;
			}
		}
		
		Campaign newComp = new Campaign();
		newComp.setCampaignId(campaignId);
		newComp.setCreateTime(GenernalFunction.getGMTDate());
		
		user.getCampaignList().add(newComp);
		incCampaignRecorderUserCounter(campaignId);
		
		return newComp;
	}
	
	private void incCampaignRecorderUserCounter(String campaignId){
		CampaignRecorder campRecorder = campaignRecorderService.getCampaignRecorderByCampaignId(campaignId);
		if(campRecorder == null){
			campRecorder = new CampaignRecorder();
			campRecorder.setCampaignId(campaignId);
			campRecorder.incUserCount();
			campaignRecorderService.save(campRecorder);
		}else{
			campaignRecorderService.incCampaignRecorderUserCounter(campaignId);
		}
	}
	
	@Override
	public UserLocation saveUserLocation(UserLocation userLocation){
		return dao.saveUserLocation(userLocation);
	}
	
	@Override
	public UserLocation getUserLocationByUserId(String userId){
		return dao.getUserLocationByUserId(userId);
	}
	
	@Override
	public List<UserLocation> getNearUserLocation(double lon,double lat,int limit){
		return dao.getNearUserLocation(lon, lat, limit);
	}
	

}
