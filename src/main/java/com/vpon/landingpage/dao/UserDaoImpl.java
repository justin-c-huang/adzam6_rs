package com.vpon.landingpage.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.vpon.landingpage.domain.Mazda6DiscountMessage;
import com.vpon.landingpage.domain.User;
import com.vpon.landingpage.domain.UserLocation;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	MongoOperations mongoOperations;

	@Override
	public User getUser(String id) {
		return mongoOperations.findById(id, User.class);
	}

	@Override
	public User save(User user) {
		mongoOperations.save(user);
		return user;
	}
	
	@Override
	public User getUserByImei(String imei) {
		return mongoOperations.findOne(new Query(where("imei").is(imei)),
				User.class);
	}
	
	@Override
	public UserLocation saveUserLocation(UserLocation userLocation){
		mongoOperations.save(userLocation);
		return userLocation;
	}
	
	@Override
	public UserLocation getUserLocationByUserId(String userId){
		
		return mongoOperations.findOne(new Query(where("userId").is(userId)),
				UserLocation.class);
	}
	
	@Override
	public List<UserLocation> getNearUserLocation(double lon,double lat,int limit){
		List<UserLocation> retList = new ArrayList<UserLocation>();
		
		Point point = new Point(lon, lat);
		NearQuery nearQuery = NearQuery.near(point).maxDistance(
				new Distance(10.0, Metrics.KILOMETERS)).num(limit);
		nearQuery.distanceMultiplier(Metrics.KILOMETERS);
		Query query = new Query().limit(limit);
		
		nearQuery.query(query);
		GeoResults<UserLocation> userLocationList = mongoOperations.geoNear(nearQuery, UserLocation.class);
		List<GeoResult<UserLocation>> geoResultList = userLocationList.getContent();
		for(GeoResult<UserLocation> geoResult : geoResultList){
			retList.add(geoResult.getContent());
		}
		
		
		return retList;		
		
		
	}

}
