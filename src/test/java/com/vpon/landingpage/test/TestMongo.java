package com.vpon.landingpage.test;

import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import au.com.bytecode.opencsv.CSVReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.vpon.landingpage.domain.Mazda6Coupon;
import com.vpon.landingpage.domain.Mazda6DiscountMessage;
import com.vpon.landingpage.util.GenernalFunction;

public class TestMongo {

	
	
	
	/**
	 * @param args
	 * @throws Throwable 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, Throwable {
		/*
		Mongo m = new Mongo(Arrays.asList(new ServerAddress("dev-justin1",
				10001), new ServerAddress("dev-justin1", 10002)));
		MongoOperations mongoOperations = new MongoTemplate(m, "test_vpon_1116"); 
		*/
		
		Mongo m = new Mongo(Arrays.asList(new ServerAddress("dev.gopartyon.com",
				27017), new ServerAddress("dev2.gopartyon.com", 27017)));
		MongoOperations mongoOperations = new MongoTemplate(m, "test_vpon_1116_charlies"); 
		
		/*
		String prefix = "dae21bc4cf8";
		for(int i = 13000; i<20000 ;i++){
			
			Mazda6Coupon coupon = new Mazda6Coupon();
			coupon.setCouponId(prefix+i);
			mongoOperations.save(coupon);
		}
		*/
		

		
		CSVReader reader = new CSVReader(new FileReader(
				"/Users/apple/mazda6_450_ok.txt"), '\t');
		String[] nextLine;
		Mazda6DiscountMessage message;
		while ((nextLine = reader.readNext()) != null) {
			String shopId = nextLine[1];
			String shopName = nextLine[2];
			Double lat = Double.parseDouble(nextLine[3]);
			Double lon = Double.parseDouble(nextLine[4]);
			
			message = new Mazda6DiscountMessage();
			message.setShopId(shopId);
			message.setShopName(shopName);
			message.getLocation().put("lat", lat);
			message.getLocation().put("lon", lon);
			mongoOperations.save(message);
			
		}
		
		
		
		
		
		
		
		
		
//		DBObject myDoc = coll.findOne();
//		
//		System.out.println((String)myDoc.get("shopId"));
//		
//		
//		DBCursor cursor = coll.find();
//        try {
//            while(cursor.hasNext()) {
//                System.out.println(cursor.next());
//            }
//        } finally {
//            cursor.close();
//        }
	}

}
