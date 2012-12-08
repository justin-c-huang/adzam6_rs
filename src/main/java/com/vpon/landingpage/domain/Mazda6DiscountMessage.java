package com.vpon.landingpage.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;

public class Mazda6DiscountMessage {

	@Id
	private String id;
	
	@Indexed
	private String shopId;
	
	private String shopName;
	
	private String uri;
	
	@GeoSpatialIndexed
	private Map<String, Double> location = new HashMap<String, Double>();

	public String getId() {
		return id;
	}
	
	
	public String getUri() {
		return uri;
	}



	public void setUri(String uri) {
		this.uri = uri;
	}



	public void setId(String id) {
		this.id = id;
	}

	
	public Map<String, Double> getLocation() {
		return location;
	}

	public void setLocation(Map<String, Double> location) {
		this.location = location;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	
	
	
}
