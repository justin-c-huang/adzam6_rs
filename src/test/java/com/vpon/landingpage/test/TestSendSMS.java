package com.vpon.landingpage.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vpon.landingpage.util.GenernalFunction;

import au.com.bytecode.opencsv.CSVReader;

public class TestSendSMS {

	static final String URI = "http://116.213.100.134:9080/realtime.html?USER=vpon&PASS=trwe3tyq6b&PRODUCTID=276&CHANNELID=1&";
	static final String DESTERMID = "DESTERMID=";
	static final String MSG = "&MSG=";
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	static String sendSMS(String URI){
		//List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		//acceptableMediaTypes.add(MediaType.);
		HttpHeaders headers = new HttpHeaders();
		//headers.setAccept(acceptableMediaTypes);
		
		ResponseEntity<byte[]> result = null;
		try {
			result = restTemplate
					.exchange(
							URI,
							HttpMethod.GET,new HttpEntity<byte[]>(headers),byte[].class);

			return new String(result.getBody(), "UTF-8");
			// return "resultpage";
		} catch (Exception e) {
			// logger.error(e);
			System.out.println("throw Excepiton:" + e);
		}
		return "some thing wrong.";
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

	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(GenernalFunction.getRandomId(11));
		// TODO Auto-generated method stub
		//String message = "1 Hello Serina, 有收到嗎？ MAZDA test 传递";
		//String completeURI = URI+DESTERMID+"13910687934"+MSG+message;
		
		//System.out.println("Send:"+completeURI);
		//System.out.println(sendSMS(completeURI));
		
//		String messageTemplate = "您的享乐达人团购折价券 編號為:%s";
//		String couponNum = "33356";
//		String completeMessage = String.format(messageTemplate, couponNum);
//		System.out.println(completeMessage);
		
//		for(int i = 0 ; i <10; i++){
//			 String completeString = UUID.randomUUID().toString().replaceAll("-", "");
//			 System.out.println(completeString);
//			 System.out.println(completeString.substring(0,16));
//		}
//		final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//	    System.out.println("uuid = " + uuid);
	    
//	    try {
//			CSVReader reader = new CSVReader(new FileReader("/Users/apple/testAddress.csv"));
//			 String [] nextLine;
//			    while ((nextLine = reader.readNext()) != null) {
//			        // nextLine[] is an array of values from the line
//			        System.out.println(nextLine[0] +"|"+nextLine[1]);
//			    }
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		String PostData = "sname=dlshydfs&spwd=87654321&scorpid=&sprdid=1012818&sdst=13910687934&smsg="+java.net.URLEncoder.encode( "mazda6 換短信uri 測試！！","utf-8");
		//String PostData = "sname=dlwangzy&spwd=12345678&scorpid=&sprdid=1012818&sdst=13910687934&smsg="+java.net.URLEncoder.encode( "hi sarian 這是用測試帳號發的 MAZDA test 传递","utf-8");
		//String ret = SMS(PostData, "http://10.1.120.22/SmsMmsWebService/Service.asmx/g_Submit");
		//String ret = SMS(PostData, "http://chufa.lmobile.cn/submitdata/service.asmx/g_Submit");
		//String ret = SMS(PostData, "http://60.28.200.150/submitdata/service.asmx/g_Submit");
		
		String ret = SMS(PostData, "http://cf.lmobile.cn/submitdata/service.asmx/g_Submit");
		
		System.out.println(ret);
		if(ret.contains("<State>0</State>"))
			System.out.println("success");
		else
			System.out.println("false");
	}

}
