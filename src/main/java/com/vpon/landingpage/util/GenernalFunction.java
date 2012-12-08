package com.vpon.landingpage.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;



public class GenernalFunction {

	public static DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String getMQTTRegId() {
		String regId = "";
		
		try 
		{
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
		    final byte[] digest2 = md5.digest(UUID.randomUUID().toString().replace("-", "").getBytes("UTF-8"));
		    long result = 0;
		    for (int i = 0; i < 8; i++)
		    {
		        result |= (0xFFL & digest2[i]) << (i * 8);
		    }
		    
		    regId = Long.toHexString(result); 
		}
		catch(Exception ex) 
		{
			ex.printStackTrace();
		}
		
	    return regId;
	}
	/**
	 * Get GMT+0
	 * @return Calendar
	 */
	public static Calendar getGMTCalendar(){
		return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}
	
	/**
	 * Get Date of GMT+0
	 * @return date
	 */
	public static Date getGMTDate(){
		String date1 = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", getGMTCalendar());
		Date date = null;
		try {
			date = format.parse(date1);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * Get Date for specfic Calendar
	 * @param cal
	 * @return date
	 */
	public static Date getGMTDate(Calendar cal){
		String date1 = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", cal);
		Date date = null;
		try {
			date = format.parse(date1);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * Check date is expired
	 * @param expiredDate
	 * @return true if time was expired; return false if time was not expired
	 */
	public static boolean isExpired(Date expiredDate){
		Date current = getGMTDate();
		return (expiredDate.before(current));
	}
	
	/**
	 * Check obj is null or not
	 * @param obj
	 * @param defaultValue
	 * @return obj if obj was not null; defaultValue if obj is null
	 */
	public static Object isNull(Object obj, Object defaultValue){
		if (obj == null){
			return defaultValue;
		} else {
			return obj;
		}
	}
	
	/* cp code from http://www.zipcodeworld.com/samples/distance.java.html */
	
	public static double distance(double lat1, double lon1, double lat2,
			double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
	
	public static String  getRandomId(int length){
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
	}
}
