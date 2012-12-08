package com.vpon.landingpage.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vpon.landingpage.RestfulController;

public class SmsUtil {
	
	static final String URI;// = "http://116.213.100.134:9080/realtime.html?USER=vpon&PASS=trwe3tyq6b&PRODUCTID=276&CHANNELID=1&";
//	static final String DESTERMID = "DESTERMID=";
//	static final String MSG = "&MSG=";
	
	private static final Logger logger = Logger
			.getLogger(SmsUtil.class);
	
	private static Properties props;

	private static void loadProperties() {
		props = new Properties();
		try {
			InputStream is = SmsUtil.class
					.getResourceAsStream("/sms.properties");
			props.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getConfig(String key) {
		loadProperties();
		return props.getProperty(key);
	}

	static {
		URI = getConfig("uri");
	}
	
	private static RestTemplate restTemplate = new RestTemplate();
	
//	public static String sendSMS(String phoneNum, String message){
//		
//		String completeURI = URI+DESTERMID+phoneNum+MSG+message;
//		
//		HttpHeaders headers = new HttpHeaders();
//		ResponseEntity<byte[]> result = null;
//		try {
//			result = restTemplate
//					.exchange(
//							completeURI,
//							HttpMethod.GET,new HttpEntity<byte[]>(headers),byte[].class);
//
//			return new String(result.getBody(), "UTF-8");
//			
//		} catch (Exception e) {
//			
//			return "throw exception:"+e.getMessage();
//		}
//	
//	}
	
	public static String sendSMSMock(String phoneNum, String message){
		String ret = "<State>0</State>";
		try {
			String PostData = "sname=dlwangzy&spwd=12345678&scorpid=&sprdid=1012818&sdst="
					+ phoneNum + "&smsg=" + URLEncoder.encode(message, "utf-8");
			
			System.out.println("URI"+URI+" PostData:"+PostData);
		} catch (UnsupportedEncodingException e) {
			
		}
		return ret;
	}
	
	public static String sendSMS(String phoneNum, String message) {
		String ret = "";
		try {
			
			logger.info("[SMS]Send phone:"+phoneNum+" message:"+message);
			
			String PostData = "sname=dlshydfs&spwd=87654321&scorpid=&sprdid=1012818&sdst="
					+ phoneNum + "&smsg=" + URLEncoder.encode(message, "utf-8"); //PRDUCTION
			
			//String PostData = "sname=dlwangzy&spwd=12345678&scorpid=&sprdid=1012818&sdst="
			//		+ phoneNum + "&smsg=" + URLEncoder.encode(message, "utf-8"); //TEST

			ret = SMS(PostData,URI);
			logger.info("[SMS]Return"+ret);

		} catch (UnsupportedEncodingException e) {
			
		}
		return ret;
	}
	
	public static String SMS(String postData, String postUrl) {
        try {
            
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "connect failed!";
            }
            
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }
	
}
