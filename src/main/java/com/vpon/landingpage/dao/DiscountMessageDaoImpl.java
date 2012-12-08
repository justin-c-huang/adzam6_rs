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

@Repository
public class DiscountMessageDaoImpl implements DiscountMessageDao {

	
	@Autowired
	MongoOperations mongoOperations;
	
	@Override
	public Mazda6DiscountMessage save(Mazda6DiscountMessage message) {
		
		if(message.getShopId() == null){
			return null;
		}
		
		//TODO: Don't update only create new item.
		Mazda6DiscountMessage tmpMessage = this.getDiscountByShopId(message.getShopId());
		if(tmpMessage != null){
			return null;
		}
		
		mongoOperations.save(message);
		return message;
	}

	@Override
	public List<Mazda6DiscountMessage> getNearDiscountMessage(double lon,
			double lat, int limit) {
		List<Mazda6DiscountMessage> retList = new ArrayList<Mazda6DiscountMessage>();
		
		Point point = new Point(lon, lat);
		NearQuery nearQuery = NearQuery.near(point).maxDistance(
				new Distance(Double.MAX_VALUE, Metrics.KILOMETERS)).num(limit);
		nearQuery.distanceMultiplier(Metrics.KILOMETERS);
		Query query = new Query().limit(limit);
		
		nearQuery.query(query);
		GeoResults<Mazda6DiscountMessage> messageList = mongoOperations.geoNear(nearQuery, Mazda6DiscountMessage.class);
		List<GeoResult<Mazda6DiscountMessage>> geoResultList = messageList.getContent();
		for(GeoResult<Mazda6DiscountMessage> geoResult : geoResultList){
			retList.add(geoResult.getContent());
		}
		
		
		return retList;		
	}

	@Override
	public Mazda6DiscountMessage getDiscountByShopId(String shopId) {
		Query query = new Query(where("shopId").is(shopId));
		return mongoOperations.findOne(query, Mazda6DiscountMessage.class);
	}

}
