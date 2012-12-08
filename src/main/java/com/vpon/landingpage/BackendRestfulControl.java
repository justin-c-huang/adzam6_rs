package com.vpon.landingpage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import au.com.bytecode.opencsv.CSVReader;

import com.vpon.landingpage.domain.Mazda6Coupon;
import com.vpon.landingpage.domain.Mazda6DiscountMessage;
import com.vpon.landingpage.service.CampaignRecorderService;
import com.vpon.landingpage.service.CouponService;
import com.vpon.landingpage.service.DiscountMessageService;
import com.vpon.landingpage.service.UserService;
import com.vpon.landingpage.util.GenernalFunction;

import flexjson.JSONDeserializer;
import flexjson.JSONException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class BackendRestfulControl {

	@Autowired
	UserService userService;

	@Autowired
	CampaignRecorderService campaignRecorderService;

	@Autowired
	CouponService couponService;

	@Autowired
	DiscountMessageService discountMessageService;

	private static final Logger logger = Logger
			.getLogger(BackendRestfulControl.class);

	@RequestMapping(value = "/testredirect", method = RequestMethod.GET)
	public ModelAndView save(
			@RequestParam(value = "nameSearch", required = false) String nameSearch,
			HttpServletRequest request) {
		System.out.println(nameSearch);
		return new ModelAndView(
				new RedirectView(
						"https://www.google.com.tw/search?q=spring&aq=f&oq=spring&sugexp=chrome,mod=0&sourceid=chrome&ie=UTF-8"));
	}

	@RequestMapping(value = "/backend/importMazda6Coupon", method = RequestMethod.POST)
	public ResponseEntity<String> importMazda6Coupon(@RequestBody String json) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json;charset=utf-8");

		Map<String, Object> deserialized = null;
		try {
			deserialized = new JSONDeserializer<Map<String, Object>>()
					.deserialize(json);
		} catch (JSONException e) {

		}

		for (int i = 0; i < 100; i++) {
			Mazda6Coupon coupon = new Mazda6Coupon();
			coupon.setCouponId(GenernalFunction.getRandomId(16));
			couponService.saveMazda6Coupon(coupon);
		}

		return new ResponseEntity<String>("{\"result\":" + 0 + "}",
				responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value = "/backend/importMazda6DiscountMessage", method = RequestMethod.POST)
	public ResponseEntity<String> importMazda6DiscountMessage(
			@RequestBody String json) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json;charset=utf-8");

		// Map<String, Object> deserialized = null;
		// try {
		// deserialized = new JSONDeserializer<Map<String, Object>>()
		// .deserialize(json);
		// } catch (JSONException e) {
		//
		// }

		try {
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream("/Users/apple/mazda6_500_ok.txt"),
					"UTF-8"));
			Mazda6DiscountMessage message;
			int i = 0;
			while ((line = in.readLine()) != null) {

				String[] nextLine = line.split("\t");
				// System.out.println(nextLine[0]+":::"+nextLine[1]+":::"+nextLine[2]+":::"+nextLine[3]+":::"+nextLine[4]+":::");
				String shopId = nextLine[1];
				String shopName = nextLine[2];
				Double lat = Double.parseDouble(nextLine[3]);
				Double lon = Double.parseDouble(nextLine[4]);

				message = new Mazda6DiscountMessage();
				message.setShopId(shopId);
				message.setShopName(shopName);
				message.getLocation().put("lat", lat);
				message.getLocation().put("lon", lon);
				discountMessageService.save(message);
				System.out.println(++i);

				// StringTokenizer st = new StringTokenizer(line);
				// while (st.hasMoreTokens()) {
				// System.out.print(st.nextToken()+":::");
				// }
				// System.out.println("-------------------");
				// lineNum++;
				// if(lineNum == 30)
				// break;
				// String UTF8Str = new String(line.getBytes(),"UTF-8");
				// System.out.println(line);
			}
		} catch (Exception e) {
			logger.error("throw excepiton import DiscountMsg", e);
		}

		/*
		 * Mazda6DiscountMessage message = new Mazda6DiscountMessage();
		 * message.setShopId("005"); message.setShopName("能仁家商");
		 * message.getLocation().put("lat", 24.958307522);
		 * message.getLocation().put("lon", 121.54033693);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("006");
		 * message.setShopName("淡江大學"); message.getLocation().put("lat",
		 * 25.17606645355); message.getLocation().put("lon", 121.44760082);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("007");
		 * message.setShopName("虎林國中"); message.getLocation().put("lat",
		 * 24.806946897025); message.getLocation().put("lon", 120.9413685038);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("001");
		 * message.setShopName("中山國中"); message.getLocation().put("lat",
		 * 25.06237); message.getLocation().put("lon", 121.5438);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("002");
		 * message.setShopName("大同高中"); message.getLocation().put("lat",
		 * 25.057096); message.getLocation().put("lon", 121.5343468);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("003");
		 * message.setShopName("台北車站"); message.getLocation().put("lat",
		 * 25.0484186); message.getLocation().put("lon", 121.5171046);
		 * discountMessageService.save(message);
		 * 
		 * message = new Mazda6DiscountMessage(); message.setShopId("004");
		 * message.setShopName("秀朗國小"); message.getLocation().put("lat",
		 * 24.9991469); message.getLocation().put("lon", 121.521177);
		 * discountMessageService.save(message);
		 */

		return new ResponseEntity<String>("{\"result\":" + 0 + "}",
				responseHeaders, HttpStatus.OK);
	}
}
