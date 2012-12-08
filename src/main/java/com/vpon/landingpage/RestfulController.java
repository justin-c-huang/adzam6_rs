package com.vpon.landingpage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vpon.landingpage.util.GenernalFunction;
import com.vpon.landingpage.util.SmsUtil;
import com.vpon.landingpage.vo.ResultMessage;
import com.vpon.landingpage.domain.Campaign;
import com.vpon.landingpage.domain.CampaignRecorder;
import com.vpon.landingpage.domain.Click;
import com.vpon.landingpage.domain.Coupon;
import com.vpon.landingpage.domain.Mazda6Coupon;
import com.vpon.landingpage.domain.Mazda6DiscountMessage;
import com.vpon.landingpage.domain.User;
import com.vpon.landingpage.domain.UserLocation;
import com.vpon.landingpage.service.CampaignRecorderService;
import com.vpon.landingpage.service.ClickService;
import com.vpon.landingpage.service.CouponService;
import com.vpon.landingpage.service.DiscountMessageService;
import com.vpon.landingpage.service.UserService;

import flexjson.JSONDeserializer;
import flexjson.JSONException;

@Controller
public class RestfulController {
	
	static final String CAMPAIGNID = "campaignId";
	static final String IMEI = "imei";
	static final String REGION = "region";
	static final String LAT = "lat";
	static final String LON = "lon";
	static final String USER_DATA = "userData";
	static final String PAGE = "page";
	static final String BUTTON = "button";
	static final String PHONE = "phone";
	static final String MESSAGEID = "messageId";
	static final String USERID = "UserId";
	static final String DISCOUNTMSGLIST = "discountMsgList";
	static final String CARTYPEMSGID = "carTypeMsgId";
	static final String DISCOUNTMESSAGEID = "discountMessageId"; 
	static final String HASSENDDISCOUNTMSGSMS = "hasSendDiscountMsgSMS";
	static final String HASSENDCARTYPEMSGSMS = "hasSendCarTypeMsgSMS";
	
	
	static final int SMSLIMIT = 300;
	
	
	@Autowired
	UserService userService;
	
	@Autowired
	CampaignRecorderService campaignRecorderService;
	
	@Autowired
	CouponService couponService;
	
	@Autowired
	ClickService clickService;
	
	@Autowired
	DiscountMessageService discountMessageService;
	
	private static Executor smsThreadExecutor = Executors.newSingleThreadExecutor(); 
	private static BlockingQueue<Map<String,String>> smsDataQueue = new LinkedBlockingQueue<Map<String,String>>();
	private static int newSmsHandlerCounter = 0;
	
	private static Executor clickThreadExecutor = Executors.newSingleThreadExecutor(); 
	private static BlockingQueue<Click> clickDataQueue = new LinkedBlockingQueue<Click>();
	
	private static Executor sendCouponThreadExecutor = Executors.newSingleThreadExecutor(); 
	private static BlockingQueue<Map<String,Object>> sendCouponDataQueue = new LinkedBlockingQueue<Map<String,Object>>();
	
	private static final Logger logger = Logger
			.getLogger(RestfulController.class);
	
	
	@PostConstruct
	public void postConstruct() {
		if(newSmsHandlerCounter == 0){
			smsThreadExecutor.execute(new SMSHandler());
			clickThreadExecutor.execute(new ClickHandler());
			sendCouponThreadExecutor.execute(new SendCouponHandler());
			newSmsHandlerCounter++;
		}else{
			logger.error("newSmsHandlerCounter != 0 !!");
		}	
	}

	@PreDestroy
	public void PreDestroy() {}
	
	
	private class ClickHandler implements Runnable{

		@Override
		public void run() {
			logger.error("ClickHandler Thread start----------------------------------");
			while (true) {
				Click click = null;
				try {
					click = clickDataQueue.take();
					clickService.save(click);
				} catch (Exception e) {
					logger.error(" clickDataQueue.take() or clickService.save throw exception",e);
				}
			}
		}
	}
	
	
	private class SendCouponHandler implements Runnable{

		@Override
		public void run() {
			logger.error("SendCouponHandler Thread start----------------------------------");
			while (true) {
				Map<String, Object> data = null;
				try {
					data = sendCouponDataQueue.take();
				} catch (InterruptedException e) {
					logger.error(" sendCouponDataQueue.take() throw exception",e);
				}

				if (data != null) {
					try {
						long timestamp = (Long)data.get("timestamp");
						if(System.currentTimeMillis() > timestamp){
							sendCoupon(data);
						}else{
							sendCouponDataQueue.put(data);
						}
						Thread.sleep(100);
						
					} catch (Exception e) {
						logger.error("sendCouponDataQueue throw Exception", e);
					}
				}
			}
		}
		
		
		private void sendCoupon(Map<String, Object> data) {

			String userId = (String) data.get(RestfulController.USERID);
			String campaignId = (String) data.get(RestfulController.CAMPAIGNID);
			String phone = (String) data.get(RestfulController.PHONE);
			String messageId = (String) data.get(RestfulController.MESSAGEID);

			if (couponService == null) {
				logger.error("couponService == null");
				return;
			}
			
			if (couponService.canSendCouponByCampaignIdAndUserId(campaignId,
					userId) == false) {
				logger.error("[SendCouponHandler]couponService.canSendCouponByCampaignIdAndUserId(campaignId, userId) == false");
				String errorSMS = "感谢您参与一汽马自达Mazda6“品质生活 一部到位”活动，今日您已成功领取一张10元团购折价劵（每人每天限领一张），欢迎您明天继续参与分享活动。";
				SmsUtil.sendSMS(phone, errorSMS);
				return;
			}
			

			Mazda6Coupon mCoupon = couponService.getMazda6CouponAndUpdate();
			String couponId = null;
			if (mCoupon == null) {
				logger.error("[SendCouponHandler] cannot get Mazda6Coupon");
				return;
			} else {
				couponId = mCoupon.getCouponId();
				if (couponId == null) {
					logger.error("mCoupon.getCouponId() == null");
					return;
				}

				mCoupon.setCampaignId(campaignId);
				mCoupon.setOccupancy(true);
				mCoupon.setUserId(userId);
				couponService.saveMazda6Coupon(mCoupon);
			}

			// TODO:
			String smsTemplate = null;
			if (messageId.equals("Mazda6")) {
				smsTemplate = RestfulController.smsMessageTemplate;
			} else {
				smsTemplate = RestfulController.smsMessageTemplate;
			}
			String completeMessage = String.format(smsTemplate, couponId);

			// TODO:
			String retStr = SmsUtil.sendSMS(phone, completeMessage);
			// String retStr = SmsUtil.sendSMSMock(phone, completeMessage);
			if (!retStr.contains("<State>0</State>")) {
				logger.error("send sms return:" + retStr);
				mCoupon.setCampaignId(null);
				mCoupon.setOccupancy(false);
				mCoupon.setUserId(null);
				couponService.saveMazda6Coupon(mCoupon);
				return;
			}

			Coupon aCoupon = new Coupon();
			aCoupon.setCampaignId(campaignId);
			aCoupon.setCouponId(couponId);
			aCoupon.setDate(GenernalFunction.getGMTDate());
			aCoupon.setOccupancy(true);
			aCoupon.setUserId(userId);
			aCoupon.setSmsReturnStr(retStr);
			aCoupon.setPhone(phone);

			couponService.save(aCoupon);

			if (campaignRecorderService == null) {
				logger.error("campaignRecorderService == null");
				return;
			}
			campaignRecorderService.incGrantCouponNumByCampaignId(campaignId);

		}
	}
	
	
	private class SMSHandler implements Runnable{

		@Override
		public void run() {
			logger.error("SMSHandler Thread start----------------------------------");
			while (true) {
				Map<String, String> data = null;
				try {
					data = smsDataQueue.take();
				} catch (InterruptedException e) {
					logger.error(" smsDataQueue.take() throw exception",e);
				}

				if (data != null) {
					try {
						sendSMS(data);
					} catch (Exception e) {
						logger.error("sendSMS throw Exception", e);
					}
				}
			}
		}
		
		
		
		private String getCarTypeMessage(String campaignId){
			final String tempStr = "感谢您关注一汽马自达，2012款Mazda6在售2.0L手动型、2.0L时尚型、2.0L超豪华型三款车型，经典品质，铸就价值!13.58万元起，超值一部到位。详情请咨询当地经销商，免费热线";
			
			String hotPhone = null;
			if(campaignId.equals("mazda6_sina")){
				hotPhone = "400-697-9580";
			}else if(campaignId.equals("mazda6_3g")){
				hotPhone = "400-602-7318";
			}else if(campaignId.equals("mazda6_phoenix")){
				hotPhone = "400-699-3087";
			}else if (campaignId.equals("mazda6_uc")){
				hotPhone = "400-601-7719";
			}else{
				hotPhone = "400-653-1176";
			}
			
			return tempStr + hotPhone;
		}
		
		private String getDiscountMessage(Mazda6DiscountMessage discountMessage) {
			if (!discountMessage.getShopId().startsWith("http")
					&& discountMessage.getUri() != null) {
				if (discountMessage.getShopId().startsWith("aaa")) {
					return "Mazda6 为您送上的优惠讯息："
							+ discountMessage.getShopName() + "。详情"
							+ discountMessage.getUri();
				} else {
					return "Mazda6 为您送上的优惠讯息：优惠券＃"
							+ discountMessage.getShopId()
							+ discountMessage.getShopName() + "。详情"
							+ discountMessage.getUri();
				}
			}
			//old style
			return "Mazda6 为您送上的优惠讯息：优惠券＃"
					+ getDiscountMessageId(discountMessage.getShopId())
					+ discountMessage.getShopName() + "。详情"
					+ discountMessage.getShopId();
		}
		
		private String getDiscountMessageId(String shopId){
			if(shopId == null)
				return " ";
			
			if(shopId.contains("PromoDetail2")){
				String token[] = shopId.split("=");
				if(token != null && token.length > 0){
					return token[token.length - 1];
				}
			}else{
				String token[] = shopId.split("/");
				if(token != null && token.length > 0){
					return token[token.length - 1];
				}
			}
			
			return " ";
		}
		
		private void sendSMS(Map<String,String> data){
			
			if (data.containsKey(RestfulController.DISCOUNTMESSAGEID)) {
				String discountMsgId = data.get(RestfulController.DISCOUNTMESSAGEID);
				Mazda6DiscountMessage discountMsg = discountMessageService.getDiscountByShopId(discountMsgId);
				if(discountMsg != null && discountMsg.getShopName()!= null){
					String phone = data.get(RestfulController.PHONE);
					// TODO:
					String retStr = SmsUtil.sendSMS(phone, getDiscountMessage(discountMsg));
					//SmsUtil.sendSMSMock(phone, getDiscountMessage(discountMsg));
				}

			}else if(data.containsKey(RestfulController.CARTYPEMSGID)){
				String campaignId = data.get(RestfulController.CAMPAIGNID);
				String phone = data.get(RestfulController.PHONE);
				String carTypeMessageId = data.get(RestfulController.CARTYPEMSGID);
				
				// TODO:
				String retStr = SmsUtil.sendSMS(phone, getCarTypeMessage(campaignId));
				//SmsUtil.sendSMSMock(phone, getCarTypeMessage(campaignId));
				
			}else {
				
				logger.error("to here is fatal error!!");
			}   
		}
	}
	
	
	//TODO: only for Mazda6
	private static final String smsMessageTemplate = "Mazda6为您送上大众点评团购折价券,代码是%s,请您于2013年1月31日前登入大众点评使用";

	
	
	
	
	@RequestMapping(value = "/discountMsg", method = RequestMethod.GET)
	public ResponseEntity<String> getDisCountMsg(
			@RequestParam(value = "lat") Double lat,
			@RequestParam(value = "lon") Double lon,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {
		
		logger.info("enter /discountMsg limit:"+limit+" cookie:"+cookie);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");
		
		if (cookie != null && lat != null && lon != null) {
			User user = getUserFromDB(null, cookie);
			if (user != null) {
				user.getLocation().put(RestfulController.LAT, lat);
				user.getLocation().put(RestfulController.LON, lon);
				userService.save(user);
			}
		}

		if (limit == null) {
			limit = 6;
		}

		ResultMessage resultMessage = new ResultMessage();
		List<Mazda6DiscountMessage> messageList = discountMessageService
				.getNearDiscountMessage(lon, lat, limit);

		for (Mazda6DiscountMessage msg : messageList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("shopId", msg.getShopId());
			map.put("shopName", msg.getShopName());
			map.put("lon", msg.getLocation().get("lon"));
			map.put("lat", msg.getLocation().get("lat"));
			resultMessage.getListOfDataMap().add(map);
		}

		String retString = callback + "("
					+ resultMessage.toJsonListListOfDataMap() + ");";
		

		return new ResponseEntity<String>(retString, responseHeaders,
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/nearUser", method = RequestMethod.GET)
	public ResponseEntity<String> getNearUsers(
			@RequestParam(value = "lat") Double lat,
			@RequestParam(value = "lon") Double lon,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {
		
		logger.info("enter /nearUser limit:"+limit+" cookie:"+cookie);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");
		
		if (limit == null) {
			limit = 20;
		}

		ResultMessage resultMessage = new ResultMessage();
		List<UserLocation> userLocationList = userService.getNearUserLocation(lon, lat, limit);
				

		for (UserLocation userLocation : userLocationList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userName", userLocation.getUserName());
			map.put("lon", userLocation.getLocation().get("lon"));
			map.put("lat", userLocation.getLocation().get("lat"));
			resultMessage.getListOfDataMap().add(map);
		}

		String retString = callback + "("
					+ resultMessage.toJsonListListOfDataMap() + ");";
		

		return new ResponseEntity<String>(retString, responseHeaders,
				HttpStatus.OK);
	}

//	private void incCampaignRecorderUserCounter(String campaignId){
//		CampaignRecorder campRecorder = campaignRecorderService.getCampaignRecorderByCampaignId(campaignId);
//		if(campRecorder == null){
//			campRecorder = new CampaignRecorder();
//			campRecorder.setCampaignId(campaignId);
//		}
//		campRecorder.incUserCount();
//		campaignRecorderService.save(campRecorder);
//	}
	
//	@RequestMapping(value = "/numberOfAvailableCoupon/{campId}", method = RequestMethod.GET)
//	public ResponseEntity<String> getNumberOfAvailableCoupon(@PathVariable("campId") String campaignId) {
//		
//		logger.info("enter /numberOfAvailableCoupon/"+campaignId);
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", "application/json;charset=utf-8");
//		//long numberOfAvailableCoupon = campaignRecorderService.getNumberOfAvailableCouponByCampaignId(campaignId);
//		//TODO: for MAZDA6
//		long numberOfAvailableCoupon = campaignRecorderService.getNumOfAllHasGrantCoupon();
//		return new ResponseEntity<String>(
//				"{\"numberOfAvailableCoupon\":"+numberOfAvailableCoupon+"}", responseHeaders,
//				HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/canSendSMS", method = RequestMethod.GET)
	public ResponseEntity<String> canSendSMS(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "smsType") String smsType,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {
		
		final String COUPON_SMSTYPE = "coupon";
		final String DISCOUNT_SMSTYPE = "discountMsg";
		final String CARTYPE_SMSTYPE = "carTypeMsg";

		logger.info("enter /canSendSMS cookie:"+cookie);
		
		String YES = callback+"({\"result\": \"yes\"});";
		String NO =  callback+"({\"result\": \"no\"});";
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");
		
		if(campaignId == null || campaignId.equals("")){
			logger.error("campaignId == null || campaignId.equals(\"\")");
			return new ResponseEntity<String>(
					callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
					responseHeaders, HttpStatus.OK);
		}

		if(cookie == null){
			//NEW USER
			return new ResponseEntity<String>(YES, responseHeaders,
					HttpStatus.OK);
		}
		
		if(!smsType.equals(CARTYPE_SMSTYPE) && !smsType.equals(DISCOUNT_SMSTYPE) && !smsType.equals(COUPON_SMSTYPE) ){
			return new ResponseEntity<String>(NO, responseHeaders,
					HttpStatus.OK);
		}
		
		User user = getUserFromDB(null, cookie);
		if (user == null) {
			//NEW USER
			return new ResponseEntity<String>(YES, responseHeaders,
					HttpStatus.OK);
		}
		
		if(!userService.isExistCampaign(user, campaignId)){
			if (smsType.equals(COUPON_SMSTYPE)){
				if (couponService.hasAvailableMazda6Coupon() == false){
					return new ResponseEntity<String>(NO, responseHeaders,
							HttpStatus.OK);
				}
			}
			return new ResponseEntity<String>(NO, responseHeaders,
					HttpStatus.OK);
			
		}
		
		Campaign campaign  = userService.getCampaignByCampaignId(user, campaignId);
		
		if(smsType.equals(CARTYPE_SMSTYPE) ){
			if(campaign.getCampaignDataMap().containsKey(RestfulController.HASSENDCARTYPEMSGSMS)){
				return new ResponseEntity<String>(NO, responseHeaders,
						HttpStatus.OK);
			}else{
				return new ResponseEntity<String>(YES, responseHeaders,
						HttpStatus.OK);
			}
		}
		
		if(smsType.equals(DISCOUNT_SMSTYPE) ){
			return new ResponseEntity<String>(YES, responseHeaders,
					HttpStatus.OK);
			/*
			if(campaign.getCampaignDataMap().containsKey(RestfulController.HASSENDDISCOUNTMSGSMS)){
				return new ResponseEntity<String>(NO, responseHeaders,
						HttpStatus.OK);
			}else{
				return new ResponseEntity<String>(YES, responseHeaders,
						HttpStatus.OK);
			}
			*/
		}
		
		if (smsType.equals(COUPON_SMSTYPE)) {
			if (couponService.hasAvailableMazda6Coupon() == false) {
				logger.error("UserId:"+cookie+" call /canSendSMS want to get coupon but return NO");
				return new ResponseEntity<String>(NO, responseHeaders,
						HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(YES, responseHeaders,
						HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(YES, responseHeaders,
				HttpStatus.OK);
	}
		
	@RequestMapping(value = "/hasAvailableCoupon", method = RequestMethod.GET)
	public ResponseEntity<String> hasAvailableCoupon(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "callback") String callback) {

		logger.info("enter /numberOfAvailableCoupon?" + campaignId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");

		// TODO: for MAZDA6
		boolean ret = couponService.hasAvailableMazda6Coupon();
		String retString = callback + "(" + "{\"result\":" + ret + "}" + ");";

		return new ResponseEntity<String>(retString, responseHeaders,
				HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/sendSMS", method = RequestMethod.GET)
	public ResponseEntity<String> sendSMS(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "messageId",required = false) String messageId,
			@RequestParam(value = "carTypeMsgId", required = false) String carTypeMsgId,
			@RequestParam(value = "s1", required = false) String s1,
			@RequestParam(value = "s2", required = false) String s2,
			@RequestParam(value = "s3", required = false) String s3,
			@RequestParam(value = "s4", required = false) String s4,
			@RequestParam(value = "s5", required = false) String s5,
			@RequestParam(value = "s6", required = false) String s6,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {
		
		logger.info("enter /sendSMS  cookie:"+cookie);
		
		boolean hasCookie = (cookie != null) ? true : false;
		boolean isCreateNewUser = false;
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");

		try {
			if(phone.equals("18602744840")){
				return new ResponseEntity<String>(
						callback
								+ "("
								+ ResultMessage.CANNOT_SEND_SMS_AGAIN_ERROR.toJsonOnlyResult()
								+ ");", responseHeaders, HttpStatus.OK);
			}
			
			if(campaignId == null || campaignId.equals("")){
				logger.error("campaignId == null || campaignId.equals(\"\")");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
						responseHeaders, HttpStatus.OK);
			}
			
			User user = getUserFromDB(null, cookie);
			if (user == null) {
				isCreateNewUser = true;
				if(messageId != null){
					logger.error("Sending coupon cannot create new User!!");
					return new ResponseEntity<String>(
							callback+"("+ResultMessage.CANNOT_FIND_NEED_FIELD_ERROR.toJsonOnlyResult()+");",
							responseHeaders, HttpStatus.OK);
				}
				user = userService.createNewUser(null, 0.0, 0.0, null);

			} 

			
			boolean isExistCampaign = true;
			isExistCampaign = userService.isExistCampaign(user, campaignId);
			
			if(!isExistCampaign){
				Campaign campaign = userService.getCampaignByCampaignId(user,
						campaignId);

				campaign.incCounter();
			}

			
			if(isCreateNewUser || !isExistCampaign){
				userService.save(user);
			}
			
			Campaign campaign  = userService.getCampaignByCampaignId(user, campaignId);
			
			if(!isCreateNewUser && (s1 != null || carTypeMsgId != null)){
				if(/*(campaign.getCampaignDataMap().containsKey(RestfulController.HASSENDDISCOUNTMSGSMS) && s1 != null) ||*/
					(campaign.getCampaignDataMap().containsKey(RestfulController.HASSENDCARTYPEMSGSMS) && carTypeMsgId != null) ){
					logger.error("Cannot send SMS for carTypeMsg");
					return new ResponseEntity<String>(
							callback
									+ "("
									+ ResultMessage.CANNOT_SEND_SMS_AGAIN_ERROR.toJsonOnlyResult()
									+ ");", responseHeaders, HttpStatus.OK);
				}
			}
			
			
			if (isCreateNewUser || !hasCookie) {
				responseHeaders.add("Set-Cookie", "xvid=" + user.getId()
						+ "; Path=/; Expires=Wed, 13-Jan-2051 22:23:01 GMT;");
			}
			
			if(messageId != null && couponService.hasAvailableMazda6Coupon() == false){
				logger.error("couponService.hasAvailableMazda6Coupon() == false");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.COUPON_EXHAUSTED_ERROR.toJsonOnlyResult()+");", responseHeaders,
						HttpStatus.OK);
			}
			
			if(smsDataQueue.size() > RestfulController.SMSLIMIT){
				logger.error("smsDataQueue.size() > RestfulController.SMSLIMIT");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.SYSTEM_BUSY_ERROR.toJsonOnlyResult()+");", responseHeaders,
						HttpStatus.OK);
			}
			
			
			Map<String,String> smsData = new HashMap<String,String>();
			if(messageId != null){
				Map<String,Object> sendCouponData = new HashMap<String,Object>();
				sendCouponData.put(RestfulController.USERID, user.getId());
				sendCouponData.put(RestfulController.CAMPAIGNID,campaignId);
				sendCouponData.put(RestfulController.PHONE,phone);
				sendCouponData.put(RestfulController.MESSAGEID,messageId);
				sendCouponData.put("timestamp",new Long(System.currentTimeMillis()+60000L));
				sendCouponDataQueue.put(sendCouponData);
			}else if(s1 != null){
					smsData = new HashMap<String,String>();
					smsData.put(RestfulController.PHONE,phone);
					smsData.put(RestfulController.DISCOUNTMESSAGEID,s1);
					smsDataQueue.put(smsData);
					if(s2 != null){
						smsData = new HashMap<String,String>();
						smsData.put(RestfulController.PHONE,phone);
						smsData.put(RestfulController.DISCOUNTMESSAGEID,s2);
						smsDataQueue.put(smsData);
					}
					if(s3 != null){
						smsData = new HashMap<String,String>();
						smsData.put(RestfulController.PHONE,phone);
						smsData.put(RestfulController.DISCOUNTMESSAGEID,s3);
						smsDataQueue.put(smsData);
					}
					if(s4 != null){
						smsData = new HashMap<String,String>();
						smsData.put(RestfulController.PHONE,phone);
						smsData.put(RestfulController.DISCOUNTMESSAGEID,s4);
						smsDataQueue.put(smsData);
					}
					if(s5 != null){
						smsData = new HashMap<String,String>();
						smsData.put(RestfulController.PHONE,phone);
						smsData.put(RestfulController.DISCOUNTMESSAGEID,s5);
						smsDataQueue.put(smsData);
					}
					if(s6 != null){
						smsData = new HashMap<String,String>();
						smsData.put(RestfulController.PHONE,phone);
						smsData.put(RestfulController.DISCOUNTMESSAGEID,s6);
						smsDataQueue.put(smsData);
					}
				
				campaign.getCampaignDataMap().put(RestfulController.HASSENDDISCOUNTMSGSMS, true);
				campaign.getCampaignDataMap().put("discountMsgPhone", phone);
				userService.save(user);
				
			}else if(carTypeMsgId != null){
				smsData = new HashMap<String,String>();
				smsData.put(RestfulController.CAMPAIGNID, campaignId);
				smsData.put(RestfulController.PHONE,phone);
				smsData.put(RestfulController.CARTYPEMSGID,carTypeMsgId);
				smsDataQueue.put(smsData);	
				campaign.getCampaignDataMap().put(RestfulController.HASSENDCARTYPEMSGSMS, true);
				campaign.getCampaignDataMap().put("catTypeMsgPhone", phone);
				userService.save(user);
				
			}else{
				logger.error("/smsSend cannot find fields");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_NEED_FIELD_ERROR.toJsonOnlyResult()+");", responseHeaders,
						HttpStatus.OK);
			}
			
			
			
			
			
			return new ResponseEntity<String>(
					callback+"("+ResultMessage.OK.toJsonOnlyResult()+")", responseHeaders,
					HttpStatus.OK);
		
		} catch (Exception e) {
			logger.error("unknow exception error!!",e);
			return new ResponseEntity<String>(
					callback+"("+ResultMessage.UNKNOWN_EXCEPTION_ERROR.toJsonOnlyResult()+");",
					responseHeaders, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/numberOfUser", method = RequestMethod.GET)
	public ResponseEntity<String> getOccupancyCoupon(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "callback") String callback) {

		logger.info("enter /numberOfUser/" + campaignId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");
		
//		if(campaignId == null || campaignId.equals("")){
//			logger.error("campaignId.equals(\"\")");
//			return new ResponseEntity<String>(
//					callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
//					responseHeaders, HttpStatus.OK);
//			return new ResponseEntity<String>(callback
//					+ "({\"numberOfUser\":0}" + ");", responseHeaders,
//					HttpStatus.OK);
//			
//		}

		long couponNum  = couponService.getCouponNum();
		
		return new ResponseEntity<String>(callback+"({\"numberOfUser\":"
				+ couponNum + "});", responseHeaders,
				HttpStatus.OK);
	}
	
	
	
	
	
	
	
	@RequestMapping(value = "/numberOfUserOld", method = RequestMethod.GET)
	public ResponseEntity<String> getNumberOfUserByCampaignId(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "callback") String callback) {

		logger.info("enter /numberOfUser/" + campaignId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");
		
		if(campaignId == null || campaignId.equals("")){
			logger.error("campaignId.equals(\"\")");
//			return new ResponseEntity<String>(
//					callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
//					responseHeaders, HttpStatus.OK);
			return new ResponseEntity<String>(callback
					+ "({\"numberOfUser\":0}" + ");", responseHeaders,
					HttpStatus.OK);
			
		}

		CampaignRecorder campRecorder = campaignRecorderService
				.getCampaignRecorderByCampaignId(campaignId);
		if (campRecorder == null) {
			return new ResponseEntity<String>(callback
					+ "({\"numberOfUser\":0}" + ");", responseHeaders,
					HttpStatus.OK);
		}
		return new ResponseEntity<String>(callback+"({\"numberOfUser\":"
				+ campRecorder.getUserCount() + "});", responseHeaders,
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/click", method = RequestMethod.GET)
	public ResponseEntity<String> click(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "page",required = false) String page,
			@RequestParam(value = "button",required = false) String button,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {

		logger.info("enter /click  cookie:" + cookie);

		boolean hasCookie = (cookie != null) ? true : false;
		boolean isCreateNewUser = false;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json;charset=utf-8");
		responseHeaders.add("max-age", "0");
		

		try {
			
			if(campaignId == null || campaignId.equals("")){
				logger.error("campaignId == null || campaignId.equals(\"\")");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
						responseHeaders, HttpStatus.OK);
			}
			
			if (page == null && button == null) {
				logger.error("page == null && button == null");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_NEED_FIELD_ERROR
								.toJsonOnlyResult()+");",
						responseHeaders, HttpStatus.OK);
			}
			User user = getUserFromDB(null, cookie);
			if (user == null) {
				isCreateNewUser = true;
				user = userService.createNewUser(null, 0.0, 0.0, null);
			}
			
			boolean isExistCampaign = true;
			isExistCampaign = userService.isExistCampaign(user, campaignId);
			
			if(!isExistCampaign){
				Campaign campaign = userService.getCampaignByCampaignId(user,
						campaignId);

				campaign.incCounter();
			}

			if (isCreateNewUser || !isExistCampaign) {
				userService.save(user);
			}

			Click click = new Click();
			click.setButton(button);
			click.setCampaignId(campaignId);
			click.setUserId(user.getId());
			click.setPage(page);
			click.setTimestamp(System.currentTimeMillis());
			click.setDate(GenernalFunction.getGMTDate());

			clickDataQueue.put(click);

			if (isCreateNewUser || !hasCookie) {
				responseHeaders.add("Set-Cookie", "xvid=" + user.getId()
						+ "; Path=/; Expires=Wed, 13-Jan-2051 22:23:01 GMT;");
			}

			return new ResponseEntity<String>(
					callback+"("+ResultMessage.OK.toJsonOnlyResult()+");", responseHeaders,
					HttpStatus.OK);

		} catch (Exception e) {
			logger.error("unknow exception error!!", e);
			return new ResponseEntity<String>(
					callback+"("+ResultMessage.UNKNOWN_EXCEPTION_ERROR.toJsonOnlyResult()+");",
					responseHeaders, HttpStatus.OK);
		}
	}
	
	
	
	@RequestMapping(value = "/updateUserData", method = RequestMethod.GET)
	public ResponseEntity<String> updateUserData(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "lat",required = false) Double lat,
			@RequestParam(value = "lon",required = false) Double lon,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {

		logger.info("enter /updateUserData cookie:" + cookie);

		boolean hasCookie = (cookie != null) ? true : false;
		boolean isCreateNewUser = false;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");

		try {
			if(campaignId == null || campaignId.equals("")){
				logger.error("campaignId == null || campaignId.equals(\"\")");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
						responseHeaders, HttpStatus.OK);
			}
			
			Map<String, Object> userDataMap = new HashMap<String, Object>();
			userDataMap.put("userName", userName);
			userDataMap.put("phone", phone);
			
			User user = getUserFromDB(null, cookie);
			if (user == null) {
				isCreateNewUser = true;
				user = userService.createNewUser(null, 0.0, 0.0, null);
			}

			Campaign campaign = userService.getCampaignByCampaignId(user,
					campaignId);

			campaign.incCounter();

			campaign.addCampaignData(userDataMap);

			userService.save(user);
			
			////////////////////
			if (lat != null && lon != null) {

				UserLocation userLocation = userService
						.getUserLocationByUserId(user.getId());
				if (userLocation == null) {
					userLocation = new UserLocation();
				}
				
				userLocation.setUserName(userName);
				userLocation.setUserId(user.getId());
				userLocation.getLocation().put(RestfulController.LAT, lat);
				userLocation.getLocation().put(RestfulController.LON, lon);
				userService.saveUserLocation(userLocation);

			}
			///////////////////

			if (isCreateNewUser || !hasCookie) {
				responseHeaders.add("Set-Cookie", "xvid=" + user.getId()
						+ "; Path=/; Expires=Wed, 13-Jan-2051 22:23:01 GMT;");
			}

			return new ResponseEntity<String>(callback + "("
					+ ResultMessage.OK.toJsonOnlyResult() + ");",
					responseHeaders, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("unknow exception error!!", e);
			return new ResponseEntity<String>(callback + "("
					+ ResultMessage.UNKNOWN_EXCEPTION_ERROR.toJsonOnlyResult()
					+ ");", responseHeaders, HttpStatus.OK);
		}
	}
	
	
	@RequestMapping(value = "/createUser", method = RequestMethod.GET)
	public ResponseEntity<String> createUser(
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "imei",required = false) String imei,
			@RequestParam(value = "lat",required = false) Double lat,
			@RequestParam(value = "lon",required = false) Double lon,
			@RequestParam(value = "region",required = false) String region,
			@RequestParam(value = "callback") String callback,
			@CookieValue(value = "xvid", required = false) String cookie) {
		
		logger.info("enter /createUser cookie:"+cookie);
		logger.info(campaignId +" "+imei +" "+ lat +" "+lon+" "+region);
		
		boolean hasCookie = (cookie != null) ? true : false;
		boolean isCreateNewUser = false;
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/javascript;charset=utf-8");
		responseHeaders.add("max-age", "0");

		try {
			if(lat == null)
				lat = 0.0;
			if(lon == null)
				lon = 0.0;
			
			if(campaignId == null || campaignId.equals("")){
				logger.error(" campaignId == null || campaignId.equals(\"\")");
				return new ResponseEntity<String>(
						callback+"("+ResultMessage.CANNOT_FIND_COMPAIGNT_ERROR.toJsonOnlyResult()+");",
						responseHeaders, HttpStatus.OK);
			}
			
			User user = getUserFromDB(imei, cookie);
			if (user == null) {
				isCreateNewUser = true;
				user = userService.createNewUser(imei, lat, lon, region);

			} else {
				if(imei!= null)
					user.setImei(imei);
				if(region != null)
					user.setRegion(region);
				if (lat != 0.0 && lon != 0.0) {
					user.getLocation().put(RestfulController.LAT, lat);
					user.getLocation().put(RestfulController.LON, lon);
				}
			}

			Campaign campaign = userService.getCampaignByCampaignId(user,
					campaignId);

			campaign.incCounter();

			userService.save(user);

			if (isCreateNewUser || !hasCookie) {
				responseHeaders.add("Set-Cookie", "xvid=" + user.getId()
						+ "; Path=/; Expires=Wed, 13-Jan-2051 22:23:01 GMT;");
			}
			
			String retString = callback+"("+ResultMessage.OK.toJsonOnlyResult()+");";
			

			return new ResponseEntity<String>(
					retString, responseHeaders,
					HttpStatus.OK);
		
		} catch (Exception e) {
			logger.error("unknow exception error!!",e);
			return new ResponseEntity<String>(
					callback+"("+ResultMessage.UNKNOWN_EXCEPTION_ERROR.toJsonOnlyResult()+");",
					responseHeaders, HttpStatus.OK);
		}
	}
	
	
	private User getUserFromDB(String imei, String cookie) {
		User user = null;

		if (cookie != null) {
			user = userService.getUser(cookie);
			if (user != null) {
				if (imei != null && user.getImei() != null
						&& !user.getImei().equals(imei)) {
					logger.error("!user.getImei().equals(imei)");
					user.setImei(imei);
				}

			} else if (imei != null) { // cannot get user by cookie, try use
										// imei
				user = userService.getUserByImei(imei);
			}
		} else if (imei != null) { // cookie == null
			user = userService.getUserByImei(imei);
		} else {
			//logger.error("cookie and imei are NULL!!");
		}

		return user;
	}
	
//	private Click getNewClick(String page,String button){
//		Click click = new Click();
//		click.setButton(button);
//		click.setPage(page);
//		click.setTimestamp(System.currentTimeMillis());
//		return click;
//	}

}
