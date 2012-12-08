package com.vpon.landingpage.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import flexjson.JSONSerializer;


public class ResultMessage {

	private int retCode = 0;
	private String errorMsg = "";
	
	private List<Map<String, Object>> listOfDataMap;
	
	public int getRetCode() {
		return retCode;
	}
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public static final ResultMessage JSON_FORMAT_ERROR = new ResultMessage();
	static {
		JSON_FORMAT_ERROR.setRetCode(1);
		JSON_FORMAT_ERROR.setErrorMsg("JSON format is error!!");
	}
	
	public static final ResultMessage CANNOT_FIND_COMPAIGNT_ERROR = new ResultMessage();
	static {
		CANNOT_FIND_COMPAIGNT_ERROR.setRetCode(2);
		CANNOT_FIND_COMPAIGNT_ERROR.setErrorMsg("Cannot find the campaign id !!");
	}
	
	public static final ResultMessage OK = new ResultMessage();
	static {
		OK.setRetCode(0);
		OK.setErrorMsg("");
	}
	
	public static final ResultMessage CANNOT_FIND_NEED_FIELD_ERROR = new ResultMessage();
	static {
		CANNOT_FIND_NEED_FIELD_ERROR.setRetCode(3);
		CANNOT_FIND_NEED_FIELD_ERROR.setErrorMsg("Cannot find the needed field.");
	}
	
	public static final ResultMessage CANNOT_SEND_SMS_AGAIN_ERROR = new ResultMessage();
	static {
		CANNOT_SEND_SMS_AGAIN_ERROR.setRetCode(4);
		CANNOT_SEND_SMS_AGAIN_ERROR.setErrorMsg("Cannot Send SMS again!!");
	}
	
	public static final ResultMessage SYSTEM_BUSY_ERROR = new ResultMessage();
	static {
		SYSTEM_BUSY_ERROR.setRetCode(5);
		SYSTEM_BUSY_ERROR.setErrorMsg("System busy!!");
	}
	
	public static final ResultMessage COUPON_EXHAUSTED_ERROR = new ResultMessage();
	static {
		SYSTEM_BUSY_ERROR.setRetCode(6);
		SYSTEM_BUSY_ERROR.setErrorMsg("Coupon is exhausted!!");
	}
	
	public static final ResultMessage UNKNOWN_EXCEPTION_ERROR = new ResultMessage();
	static {
		UNKNOWN_EXCEPTION_ERROR.setRetCode(100);
		UNKNOWN_EXCEPTION_ERROR.setErrorMsg("UNKNOWN_EXCEPTION_ERROR");
	}
	
	public List<Map<String, Object>> getListOfDataMap() {
		if (listOfDataMap == null)
			listOfDataMap = new ArrayList<Map<String, Object>>();
		
		return listOfDataMap;
	}

	public void setListOfDataMap(List<Map<String, Object>> listOfDataMap) {
		this.listOfDataMap = listOfDataMap;
	}
	
	
	public String toJsonOnlyResult() {
		return new JSONSerializer().include("retCode","errorMsg").exclude("*").serialize(this);
	}
	
	public String toJsonListListOfDataMap(){
		return new JSONSerializer().include("retCode","errorMsg", "listOfDataMap.*").exclude("*").serialize(this);
	}
}
